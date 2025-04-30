package me.jibblyjobbly.munchmunch.config;

import me.shedaniel.autoconfig.ConfigData;

@me.shedaniel.autoconfig.annotation.Config(name = "munchmunch")
public class Config implements ConfigData {
    public String lastEatenFood = "";
}
