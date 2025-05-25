package me.jibbly.munchmunch.client.gui.render.anim.gain;

import me.jibbly.munchmunch.MunchMunchClient;
import me.jibbly.munchmunch.api.animation.HungerAnimation;
import me.jibbly.munchmunch.api.animation.HungerState;
import me.jibbly.munchmunch.config.MunchMunchConfig;

public interface GainAnimation extends HungerAnimation {
    @Override
    default HungerState getState() {
        return HungerState.GAIN;
    }

    @Override
    default boolean shouldRun() {
        MunchMunchConfig config = MunchMunchClient.getConfig();
        return config.eatingAnimationSettings.enabled && config.eatingAnimationSettings.gainAnimationSettings.enabled && config.eatingAnimationSettings.gainAnimationSettings.rippleAnimationSettings.enabled;
    }
}
