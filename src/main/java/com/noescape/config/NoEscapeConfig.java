package com.noescape.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class NoEscapeConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue ENABLE_MOD =
            BUILDER.comment("Master toggle for NoEscape's events.")
                    .define("enableMod", true);

    public static final ForgeConfigSpec.BooleanValue ENABLE_WHISPERS =
            BUILDER.comment("Occasional action-bar messages implying you're being watched.")
                    .define("enableWhispers", true);

    public static final ForgeConfigSpec.BooleanValue ENABLE_AMBIENT_SOUNDS =
            BUILDER.comment("Random unsettling ambient sound cues heard only by nearby players.")
                    .define("enableAmbientSounds", true);

    public static final ForgeConfigSpec.BooleanValue ENABLE_SCREEN_EFFECT =
            BUILDER.comment("Subtle vignette/static overlay effect during tense moments (client only).")
                    .define("enableScreenEffect", true);

    public static final ForgeConfigSpec.BooleanValue ENABLE_STALKER =
            BUILDER.comment("Enables rare, brief sightings of a stalking figure at the edge of view.")
                    .define("enableStalker", true);

    public static final ForgeConfigSpec.IntValue EVENT_CHANCE_PER_MINUTE =
            BUILDER.comment("Roughly how many random horror events occur per player per in-game minute.",
                            "Higher = more frequent. 1-2 is subtle, 10+ is relentless.")
                    .defineInRange("eventChancePerMinute", 3, 0, 60);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "noescape-common.toml");
    }
}
