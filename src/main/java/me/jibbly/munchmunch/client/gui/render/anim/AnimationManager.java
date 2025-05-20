package me.jibbly.munchmunch.client.gui.render.anim;

import me.jibbly.munchmunch.MunchMunchClient;
import net.minecraft.client.MinecraftClient;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class AnimationManager {
    public static List<Supplier<HungerAnimation>> animations = new ArrayList<>();
    private static HungerAnimation activeAnimation;
    private final Random random = new Random();

    public static void registerAnimation(Supplier<HungerAnimation> animation) {
        animations.add(animation);
        animation.get().reset();
    }

    public HungerAnimation chooseAnimation() {
        if (activeAnimation != null) {
            if (!activeAnimation.isFinished()) return activeAnimation;
            activeAnimation.reset();
        }

        HungerState state = AnimationSelector.getInstance().getState();
        List<Supplier<HungerAnimation>> filtered = animations.stream().filter(s -> s.get().getState() == state).toList();

        if (filtered.isEmpty()) {
            MunchMunchClient.LOGGER.error("No animations for type {}", state);
            return null;
        }

        HungerAnimation placeholder = filtered.get(random.nextInt(filtered.size())).get();

        MunchMunchClient.LOGGER.info("Playing animation {} for state {}", placeholder, placeholder.getState());

        activeAnimation = placeholder;
        return placeholder;
    }

    public static void cleanupFinishedAnimation() {
        if (activeAnimation.isFinished() && activeAnimation.getState() != HungerState.IDLE) {
            AnimationSelector.getInstance().setState(HungerState.IDLE);
        }
    }

    public static void runEmptyAnimation() {
        assert MinecraftClient.getInstance().player != null;
        int hungerLevel = MinecraftClient.getInstance().player.getHungerManager().getFoodLevel();

    }

    public Vector2f computeOffsets(int slot, float time) {
        Vector2f offsets;
        if (activeAnimation != null && activeAnimation.shouldRun()) {
            offsets = new Vector2f(activeAnimation.offset(slot, time));
        } else {
            offsets = new Vector2f(0, 0);
        }

        return offsets;
    }

    public Vector2f computeScale(int slot, float time) {
        Vector2f scale;
        if (activeAnimation != null && activeAnimation.shouldRun()) {
            scale = new Vector2f(activeAnimation.scale(slot, time));
        } else {
            scale = new Vector2f(1f, 1f);
        }

        return scale;
    }
}
