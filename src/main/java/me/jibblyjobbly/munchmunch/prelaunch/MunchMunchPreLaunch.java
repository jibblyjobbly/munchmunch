package me.jibblyjobbly.munchmunch.prelaunch;

import me.jibblyjobbly.munchmunch.config.MunchMunchConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class MunchMunchPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        AutoConfig.register(MunchMunchConfig.class, GsonConfigSerializer::new);
    }
}
