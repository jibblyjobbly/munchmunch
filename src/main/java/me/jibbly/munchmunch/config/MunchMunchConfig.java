package me.jibbly.munchmunch.config;

import me.jibbly.munchmunch.MunchMunchClient;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.HashMap;
import java.util.Map;

@Config(name = MunchMunchClient.MOD_ID)
public class MunchMunchConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart(false)
    public boolean useLastEatenFoodIcon = true;

    @ConfigEntry.Gui.CollapsibleObject
    public EatingAnimationSettings eatingAnimationSettings = new EatingAnimationSettings();

    public static class EatingAnimationSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enabled = true;

        @ConfigEntry.Gui.CollapsibleObject
        public IdleAnimationSettings idleAnimationSettings = new IdleAnimationSettings();

        @ConfigEntry.Gui.CollapsibleObject
        public GainAnimationSettings gainAnimationSettings = new GainAnimationSettings();

        public static class IdleAnimationSettings {
            @ConfigEntry.Gui.Tooltip
            public boolean enabled = true;
        }

        public static class GainAnimationSettings {
            @ConfigEntry.Gui.Tooltip
            public boolean enabled = true;
        }
    }

    @ConfigEntry.Gui.Excluded
    public Map<String, String> worldLastEatenFoodMap = new HashMap<>();
}