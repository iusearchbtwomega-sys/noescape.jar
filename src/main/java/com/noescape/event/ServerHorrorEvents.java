package com.noescape.event;

import com.noescape.config.NoEscapeConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Server-side pacing for NoEscape's ambient horror events.
 * Every event here is a normal, vanilla-legal in-game action:
 * a message to one player, a sound sent to one player, or a
 * temporary vanilla mob spawn that despawns itself shortly after.
 */
public class ServerHorrorEvents {

    private static final Random RANDOM = new Random();

    private static final String[] WHISPERS = new String[] {
            "Something shifts just out of view.",
            "You feel like you're being watched.",
            "It's quiet. Too quiet.",
            "Did that shadow just move?",
            "You get the sense you should look behind you.",
            "The air feels heavier here.",
            "Something knows you're here."
    };

    // Tracks per-player tick counters so we only roll dice once a second, not every tick.
    private final Map<UUID, Integer> tickCounters = new HashMap<>();

    // Tracks stalker mobs we've spawned, mapped to remaining lifetime in ticks.
    private final Map<UUID, Integer> stalkerLifetimes = new HashMap<>();

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!NoEscapeConfig.ENABLE_MOD.get()) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        UUID id = player.getUUID();
        int counter = tickCounters.merge(id, 1, Integer::sum);
        if (counter < 20) return; // once per second
        tickCounters.put(id, 0);

        double chancePerMinute = NoEscapeConfig.EVENT_CHANCE_PER_MINUTE.get();
        double chancePerSecond = chancePerMinute / 60.0;
        if (RANDOM.nextDouble() >= chancePerSecond) return;

        triggerRandomEvent(player);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        // Clean up stalker mobs whose time is up.
        Iterator<Map.Entry<UUID, Integer>> it = stalkerLifetimes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Integer> entry = it.next();
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                it.remove();
            } else {
                entry.setValue(remaining);
            }
        }
    }

    private void triggerRandomEvent(ServerPlayer player) {
        int roll = RANDOM.nextInt(100);

        if (roll < 45 && NoEscapeConfig.ENABLE_WHISPERS.get()) {
            sendWhisper(player);
        } else if (roll < 85 && NoEscapeConfig.ENABLE_AMBIENT_SOUNDS.get()) {
            playAmbientSound(player);
        } else if (NoEscapeConfig.ENABLE_STALKER.get()) {
            trySpawnStalker(player);
        }
    }

    private void sendWhisper(ServerPlayer player) {
        String message = WHISPERS[RANDOM.nextInt(WHISPERS.length)];
        player.displayClientMessage(Component.literal(message).withStyle(s -> s.withItalic(true)), true);
    }

    private void playAmbientSound(ServerPlayer player) {
        BlockPos pos = player.blockPosition();
        var sound = RANDOM.nextBoolean() ? SoundEvents.AMBIENT_CAVE.value() : SoundEvents.ENDERMAN_STARE;
        player.connection.send(new ClientboundSoundPacket(
                net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound),
                SoundSource.AMBIENT,
                pos.getX() + RANDOM.nextInt(6) - 3,
                pos.getY(),
                pos.getZ() + RANDOM.nextInt(6) - 3,
                0.4f,
                0.6f + RANDOM.nextFloat() * 0.3f,
                RANDOM.nextLong()
        ));
    }

    /**
     * Spawns a plain vanilla zombie a short distance behind the player, standing
     * still (no AI), which despawns itself after a few seconds whether or not
     * the player looks at it. No custom entity, no persistent world changes.
     */
    private void trySpawnStalker(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;

        double angle = Math.toRadians(player.getYRot() + 180 + (RANDOM.nextDouble() - 0.5) * 40);
        double distance = 10 + RANDOM.nextInt(6);
        double x = player.getX() + Math.sin(angle) * distance;
        double z = player.getZ() - Math.cos(angle) * distance;
        BlockPos spawnPos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
                new BlockPos((int) x, (int) player.getY(), (int) z));

        if (!level.getWorldBorder().isWithinBounds(spawnPos)) return;

        Zombie stalker = net.minecraft.world.entity.EntityType.ZOMBIE.create(level);
        if (stalker == null) return;

        stalker.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, RANDOM.nextFloat() * 360f, 0f);
        stalker.setNoAi(true);
        stalker.setSilent(true);
        stalker.setPersistenceRequired();
        stalker.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0, false, false));

        level.addFreshEntity(stalker);
        stalkerLifetimes.put(stalker.getUUID(), 100); // 5 seconds

        // Schedule its removal.
        level.getServer().tell(new net.minecraft.server.TickTask(level.getServer().getTickCount() + 100, () -> {
            if (stalker.isAlive()) {
                stalker.discard();
            }
        }));
    }
}
