package me.jibbly.munchmunch.client.gui.render.anim;

import me.jibbly.munchmunch.api.animation.HungerAnimation;
import me.jibbly.munchmunch.api.animation.HungerState;

public class BaseAnimation implements HungerAnimation {
    @Override
    public HungerState getState() {
        return HungerState.IDLE;
    }

    @Override
    public boolean shouldRun() {
        return false;
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
