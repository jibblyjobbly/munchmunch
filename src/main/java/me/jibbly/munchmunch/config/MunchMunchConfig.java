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

    @ConfigEntry.Gui.CollapsibleObject
    public EatingAnimationSettings eatingAnimationSettings = new EatingAnimationSettings();

    public static class EatingAnimationSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enabled = true;

        @ConfigEntry.Gui.CollapsibleObject
        public IdleAnimationSettings idleAnimationSettings = new IdleAnimationSettings();

        @ConfigEntry.Gui.CollapsibleObject
        public GainAnimationSettings gainAnimationSettings = new GainAnimationSettings();

        @ConfigEntry.Gui.CollapsibleObject
        public LoseAnimationSettings loseAnimationSettings = new LoseAnimationSettings();

        @ConfigEntry.Gui.CollapsibleObject
        public EmptyAnimationSettings emptyAnimationSettings = new EmptyAnimationSettings();

        @ConfigEntry.Gui.CollapsibleObject
        public FullAnimationSettings fullAnimationSettings = new FullAnimationSettings();

        public static class IdleAnimationSettings {
            @ConfigEntry.Gui.Tooltip
            public boolean enabled = true;

            @ConfigEntry.Gui.CollapsibleObject
            public BobAnimationSettings bobAnimationSettings = new BobAnimationSettings();

            public static class BobAnimationSettings {
                @ConfigEntry.Gui.Tooltip
                public boolean enabled = true;

                @ConfigEntry.Gui.Tooltip
                @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
                public int amplitude = 2;

                @ConfigEntry.Gui.Tooltip
                @ConfigEntry.BoundedDiscrete(min = 2, max = 10)
                public int slotDelayTicks = 5;

                @ConfigEntry.Gui.Tooltip
                @ConfigEntry.BoundedDiscrete(min = 4, max = 16)
                public int slotDurationTicks = 12;

                @ConfigEntry.Gui.Tooltip
                @ConfigEntry.BoundedDiscrete(min = 20, max = 240)
                public int cycleDelayTicks = 120;
            }
        }

        public static class GainAnimationSettings {
            @ConfigEntry.Gui.Tooltip
            public boolean enabled = true;

            @ConfigEntry.Gui.CollapsibleObject
            public RippleAnimationSettings rippleAnimationSettings = new RippleAnimationSettings();

            @ConfigEntry.Gui.CollapsibleObject
            public BulgeAnimationSettings bulgeAnimationSettings = new BulgeAnimationSettings();

            public static class RippleAnimationSettings {
                @ConfigEntry.Gui.Tooltip
                public boolean enabled = true;

                @ConfigEntry.Gui.Tooltip
                @ConfigEntry.BoundedDiscrete(min = 3, max = 9)
                public int amplitude = 5;

                @ConfigEntry.Gui.Tooltip
                @ConfigEntry.BoundedDiscrete(min = 1, max = 6)
                public int slotDelayTicks = 1;

                @ConfigEntry.Gui.Tooltip
                @ConfigEntry.BoundedDiscrete(min = 2, max = 12)
                public int slotDurationTicks = 8;
            }

            public static class BulgeAnimationSettings {
                @ConfigEntry.Gui.Tooltip
                public boolean enabled = true;

                @ConfigEntry.Gui.Tooltip
                @ConfigEntry.BoundedDiscrete(min = 101, max = 180)
                public int maxScalePercent = 120;

                @ConfigEntry.Gui.Tooltip
                @ConfigEntry.BoundedDiscrete(min = 2, max = 7)
                public int amplitude = 3;

                @ConfigEntry.Gui.Tooltip
                @ConfigEntry.BoundedDiscrete(min = 1, max = 5)
                public int slotDelayTicks = 2;

                @ConfigEntry.Gui.Tooltip
                @ConfigEntry.BoundedDiscrete(min = 2, max = 10)
                public int slotDurationTicks = 6;
            }
        }

        public static class LoseAnimationSettings {

        }

        public static class EmptyAnimationSettings {

        }

        public static class FullAnimationSettings {

        }
    }

    @ConfigEntry.Gui.Excluded
    public Map<String, String> worldLastEatenFoodMap = new HashMap<>();
}