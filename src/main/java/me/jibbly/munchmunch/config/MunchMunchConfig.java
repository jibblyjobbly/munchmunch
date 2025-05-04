package me.jibbly.munchmunch.config;

import me.jibbly.munchmunch.MunchMunchClient;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.HashMap;
import java.util.Map;

@Config(name = MunchMunchClient.MOD_ID)
public class MunchMunchConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip(count = 2)
    @ConfigEntry.Gui.RequiresRestart(false)
    public boolean useLastEatenFoodIcon = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean enableBiomeEffects = true;

    @ConfigEntry.Gui.CollapsibleObject
    public ColdBiomeSettings coldBiomeSettings = new ColdBiomeSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public HotBiomeSettings hotBiomeSettings = new HotBiomeSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public EatingAnimationSettings eatingAnimationSettings = new EatingAnimationSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public IdleAnimationSettings idleAnimationSettings = new IdleAnimationSettings();

    public static class ColdBiomeSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enabled = true;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 50)
        public float shiverIntensity = 0.7f;
        @ConfigEntry.Gui.Tooltip
        public boolean showSnowParticles = true;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public int maxSnowParticles = 10;
    }

    public static class HotBiomeSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enabled = true;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 50)
        public float hazeIntensity = 1.5f;
    }

    public static class EatingAnimationSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enabled = true;
        @ConfigEntry.Gui.Tooltip(count = 2)
        @ConfigEntry.BoundedDiscrete(min = 100, max = 2000)
        public int rippleDurationMs = 500;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public float rippleIntensity = 1.0f;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 100, max = 300)
        public float goldenFoodMultiplier = 1.5f;
    }

    public static class IdleAnimationSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enabled = true;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public float bobbingIntensity = 0.5f;
    }

    @ConfigEntry.Gui.Excluded
    public Map<String, String> worldLastEatenFoodMap = new HashMap<>();

    @Override
    public void validatePostLoad() {
        if (coldBiomeSettings.shiverIntensity < 0) coldBiomeSettings.shiverIntensity = 0;
        if (hotBiomeSettings.hazeIntensity < 0) hotBiomeSettings.hazeIntensity = 0;
        if (eatingAnimationSettings.rippleIntensity < 0) eatingAnimationSettings.rippleIntensity = 0;
        if (idleAnimationSettings.bobbingIntensity < 0) idleAnimationSettings.bobbingIntensity = 0;
        if (eatingAnimationSettings.goldenFoodMultiplier < 1.0f) eatingAnimationSettings.goldenFoodMultiplier = 1.0f;
    }
}