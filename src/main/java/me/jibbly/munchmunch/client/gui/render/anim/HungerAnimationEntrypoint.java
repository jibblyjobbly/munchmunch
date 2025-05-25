package me.jibbly.munchmunch.client.gui.render.anim;

import me.jibbly.munchmunch.api.animation.AnimationEntrypoint;
import me.jibbly.munchmunch.api.animation.AnimationRegsitry;
import me.jibbly.munchmunch.client.gui.render.anim.gain.RippleAnimation;
import me.jibbly.munchmunch.client.gui.render.anim.idle.BobAnimation;

public class HungerAnimationEntrypoint implements AnimationEntrypoint {
    @Override
    public void registerAnimations() {
        AnimationRegsitry.register(RippleAnimation::new);
        AnimationRegsitry.register(BobAnimation::new);
    }
}
