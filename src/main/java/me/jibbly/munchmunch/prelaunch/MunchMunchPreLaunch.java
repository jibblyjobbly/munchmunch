package me.jibbly.munchmunch.prelaunch;

import me.jibbly.munchmunch.client.gui.render.anim.AnimationManager;
import me.jibbly.munchmunch.client.gui.render.anim.empty.EmptyAnimation;
import me.jibbly.munchmunch.client.gui.render.anim.gain.BulgeAnimation;
import me.jibbly.munchmunch.client.gui.render.anim.gain.RippleAnimation;
import me.jibbly.munchmunch.client.gui.render.anim.idle.BobAnimation;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class MunchMunchPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        registerAnimations();
    }

    private static void registerAnimations() {
        AnimationManager.registerAnimation(BobAnimation::new);
        AnimationManager.registerAnimation(RippleAnimation::new);
        AnimationManager.registerAnimation(BulgeAnimation::new);
        AnimationManager.registerAnimation(EmptyAnimation::new);
    }
}
