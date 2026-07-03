package com.noescape;

import com.noescape.client.ClientHorrorEvents;
import com.noescape.client.HorrorOverlay;
import com.noescape.config.NoEscapeConfig;
import com.noescape.event.ServerHorrorEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * NoEscape
 *
 * An atmospheric, paranoia-driven horror mod for Minecraft.
 * Everything this mod does happens inside the game: ambient sound cues,
 * on-screen messages, a rare "stalker" mob variant, and a subtle screen
 * overlay. It never touches files, processes, or anything outside the
 * Minecraft client/server it's running in.
 */
@Mod(NoEscapeMod.MODID)
public class NoEscapeMod {

    public static final String MODID = "noescape";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public NoEscapeMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register config
        modEventBus.register(NoEscapeConfig.class);
        NoEscapeConfig.register();

        // Register global (Forge) event listeners
        MinecraftForge.EVENT_BUS.register(new ServerHorrorEvents());

        if (FMLEnvironment.dist.isClient()) {
            MinecraftForge.EVENT_BUS.register(new ClientHorrorEvents());
            modEventBus.addListener(HorrorOverlay::registerOverlays);
        }

        LOGGER.info("NoEscape loaded. You are not alone.");
    }
}
