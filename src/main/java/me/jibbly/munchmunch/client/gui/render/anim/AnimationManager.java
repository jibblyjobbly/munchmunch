package me.jibbly.munchmunch.client.gui.render.anim;

import me.jibbly.munchmunch.MunchMunchClient;
import me.jibbly.munchmunch.api.animation.AnimationRegsitry;
import me.jibbly.munchmunch.api.animation.HungerAnimation;
import me.jibbly.munchmunch.api.animation.HungerState;
import org.joml.Vector2f;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class AnimationManager {
    private static HungerAnimation active;
    private final Random random = new Random();

    public HungerAnimation chooseAnimation() {
        if (active != null && !active.isFinished()) return active;

        if (active != null && active.isFinished() && active.isTimed()) {
            active = null;
            AnimationSelector.getInstance().setState(HungerState.IDLE);
        }

        HungerState state = AnimationSelector.getInstance().getState();
        List<Supplier<HungerAnimation>> pool = AnimationRegsitry.getAll().stream().filter(a -> a.get().getState() == state).toList();

        if (pool.isEmpty()) {
            MunchMunchClient.LOGGER.warn("No animations registered for state {}", state);

            if (state != HungerState.IDLE) {
                AnimationSelector.getInstance().setState(HungerState.IDLE);
                return chooseAnimation();
            }

            return new BaseAnimation();
        }

        HungerAnimation next = pool.get(random.nextInt(pool.size())).get();
        next.reset();
        active = next;
        return active;
    }

//    public HungerAnimation chooseAnimation() {
//        if (activeAnimation != null) {
//            if (!activeAnimation.isFinished()) return activeAnimation;
//            activeAnimation.reset();
//        }
//
//        HungerState state = AnimationSelector.getInstance().getState();
//        List<Supplier<HungerAnimation>> filtered = AnimationRegsitry.getAll().stream().filter(s -> s.get().getState() == state).toList();
//
//        if (filtered.isEmpty()) {
//            MunchMunchClient.LOGGER.error("No animations for type {}", state);
//            return null;
//        }
//
//        HungerAnimation placeholder = filtered.get(random.nextInt(filtered.size())).get();
//
//        MunchMunchClient.LOGGER.info("Playing animation {} for state {}", placeholder, placeholder.getState());
//
//        activeAnimation = placeholder;
//        return placeholder;
//    }

    public static void cleanupFinishedAnimation() {
        if (active != null) {
            if (active.isFinished() && active.getState() != HungerState.IDLE) {
                AnimationSelector.getInstance().setState(HungerState.IDLE);
            }
        }
    }

    public Vector2f computeOffsets(int slot, float time) {
        Vector2f offsets;
        if (active != null && active.shouldRun()) {
            offsets = new Vector2f(active.offset(slot, time));
        } else {
            offsets = new Vector2f(0, 0);
        }

        return offsets;
    }

    public Vector2f computeScale(int slot, float time) {
        Vector2f scale;
        if (active != null && active.shouldRun()) {
            scale = new Vector2f(active.scale(slot, time));
        } else {
            scale = new Vector2f(1f, 1f);
        }

        return scale;
    }
}
