package me.jibbly.munchmunch.client.gui.render.anim.idle;

import me.jibbly.munchmunch.MunchMunchClient;
import me.jibbly.munchmunch.api.animation.HungerAnimation;
import me.jibbly.munchmunch.api.animation.HungerState;
import me.jibbly.munchmunch.config.MunchMunchConfig;

public interface IdleAnimation extends HungerAnimation {
    @Override
    default HungerState getState() {
        return HungerState.IDLE;
    }

    @Override
    default boolean isFinished() {
        return true;
    }

    @Override
    default boolean isTimed() { return false; }

    @Override
    default boolean shouldRun() {
        MunchMunchConfig config = MunchMunchClient.getConfig();
        return config.eatingAnimationSettings.enabled;
    }
}
