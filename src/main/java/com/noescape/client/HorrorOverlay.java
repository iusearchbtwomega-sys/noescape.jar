package com.noescape.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

/**
 * A dark, fading vignette drawn around the screen edges whose strength is
 * controlled by ClientHorrorEvents.overlayIntensity. Rendering-only; does
 * not read or write anything outside the game window.
 */
public class HorrorOverlay {

    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("noescape_vignette", VIGNETTE_OVERLAY);
    }

    public static final IGuiOverlay VIGNETTE_OVERLAY = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        float intensity = ClientHorrorEvents.overlayIntensity;
        if (intensity <= 0f) return;

        renderVignette(guiGraphics, screenWidth, screenHeight, intensity);
    };

    private static void renderVignette(GuiGraphics guiGraphics, int width, int height, float intensity) {
        int alpha = (int) (intensity * 180) << 24;
        int color = alpha | 0x000000; // black, variable alpha

        int edge = Math.max(width, height) / 4;

        // Top
        guiGraphics.fillGradient(0, 0, width, edge, color, 0);
        // Bottom
        guiGraphics.fillGradient(0, height - edge, width, height, 0, color);
        // Left
        guiGraphics.fillGradient(0, 0, edge, height, color, 0);
        // Right
        guiGraphics.fillGradient(width - edge, 0, width, height, 0, color);
    }
}
