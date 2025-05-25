package me.jibbly.munchmunch.api.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class AnimationRegsitry {
    private static final List<Supplier<HungerAnimation>> ANIMATIONS = new ArrayList<>();

    public static void register(Supplier<HungerAnimation> animation) {
        Objects.requireNonNull(animation, "Animation supplier must not be null");
        ANIMATIONS.add(animation);
        animation.get().reset();
    }

    public static List<Supplier<HungerAnimation>> getAll() {
        return List.copyOf(ANIMATIONS);
    }
}
