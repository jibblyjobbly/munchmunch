package me.jibbly.munchmunch.client.gui.render.anim;

import org.joml.Vector2f;

public interface HungerAnimation {
    HungerState getState();
    boolean shouldRun();
    default Vector2f offset(int slot, float time) {
        return new Vector2f(0, 0);
    }
    default Vector2f scale(int slot, float time) {
        return new Vector2f(1f, 1f);
    }
    boolean isFinished();
    default void reset() {}
}
