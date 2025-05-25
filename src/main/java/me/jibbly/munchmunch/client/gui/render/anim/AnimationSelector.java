package me.jibbly.munchmunch.client.gui.render.anim;

import me.jibbly.munchmunch.api.animation.HungerAnimation;
import me.jibbly.munchmunch.api.animation.HungerState;

public class AnimationSelector {
    private HungerState state;
    private HungerAnimation activeAnimation;
    private final AnimationManager manager;
    public AnimationSelector(AnimationManager manager) {
        this.manager = manager;
    }

    public void setState(HungerState state) {
        this.state = state;
        this.activeAnimation = manager.chooseAnimation();
    }

    public HungerState getState() {
        return state;
    }

    public static AnimationSelector getInstance() {
        return AnimationSelector.Holder.INSTANCE;
    }

    private static class Holder {
        private static final AnimationSelector INSTANCE = new AnimationSelector(new AnimationManager());
    }
}
