package me.jibbly.munchmunch.client.gui.render.anim;

import me.jibbly.munchmunch.MunchMunchClient;
import me.jibbly.munchmunch.api.animation.AnimationRegsitry;
import me.jibbly.munchmunch.api.animation.HungerAnimation;
import me.jibbly.munchmunch.api.animation.HungerState;
import me.jibbly.munchmunch.client.gui.render.anim.gain.RippleAnimation;
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
            if (state != HungerState.IDLE) {
                AnimationSelector.getInstance().setState(HungerState.IDLE);
                return chooseAnimation();
            }

            return new RippleAnimation();
        }

        HungerAnimation next = pool.get(random.nextInt(pool.size())).get();
        next.reset();
        active = next;
        return active;
    }

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
