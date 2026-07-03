package com.noescape.client;

import com.noescape.config.NoEscapeConfig;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

/**
 * Purely cosmetic, purely client-side. Drives a fading vignette overlay
 * (see HorrorOverlay) with no gameplay effect and no interaction with
 * anything outside Minecraft's own rendering.
 */
public class ClientHorrorEvents {

    private static final Random RANDOM = new Random();
    private int tickCounter = 0;

    // Current overlay strength, 0f (invisible) to 1f (fully visible).
    public static float overlayIntensity = 0f;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!NoEscapeConfig.ENABLE_MOD.get() || !NoEscapeConfig.ENABLE_SCREEN_EFFECT.get()) {
            overlayIntensity = 0f;
            return;
        }

        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            // Small independent chance to start a "tense moment" pulse.
            if (overlayIntensity <= 0f && RANDOM.nextInt(400) == 0) {
                overlayIntensity = 0.01f; // kick off the fade-in
            }
        }

        if (overlayIntensity > 0f) {
            if (overlayIntensity < 0.35f) {
                overlayIntensity += 0.01f; // fade in
            } else {
                overlayIntensity -= 0.005f; // slow fade out
            }
            if (overlayIntensity <= 0f) {
                overlayIntensity = 0f;
            }
        }
    }
}
