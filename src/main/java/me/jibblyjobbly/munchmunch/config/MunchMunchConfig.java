package me.jibblyjobbly.munchmunch.config;

import me.jibblyjobbly.munchmunch.MunchMunchClient;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@me.shedaniel.autoconfig.annotation.Config(name = MunchMunchClient.MOD_ID)
public class MunchMunchConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean useLastEatenFood = true;

    @ConfigEntry.Gui.Excluded
    public String lastEatenFood;
}