package me.jibbly.munchmunch.client.gui.render.anim.empty;

import me.jibbly.munchmunch.MunchMunchClient;
import me.jibbly.munchmunch.client.gui.render.HungerRenderer;
import me.jibbly.munchmunch.client.gui.render.anim.HungerAnimation;
import me.jibbly.munchmunch.client.gui.render.anim.HungerState;
import me.jibbly.munchmunch.config.MunchMunchConfig;
import net.minecraft.client.MinecraftClient;
import org.joml.Vector2f;

public class EmptyAnimation implements HungerAnimation {
    private long startTick = -1;
    private boolean finished = false;

    @Override
    public HungerState getState() {
        return HungerState.EMPTY;
    }

    @Override
    public boolean shouldRun() {
        MunchMunchConfig config = MunchMunchClient.getConfig();
        return config.eatingAnimationSettings.enabled;
    }

    @Override
    public Vector2f offset(int slot, float time) {
        MinecraftClient client = MinecraftClient.getInstance();
        MunchMunchConfig config = MunchMunchClient.getConfig();
        if (finished || client.player == null || client.world == null) {
            return new Vector2f(0, 0);
        }

        long worldTick = client.world.getTime();
        if (startTick < 0) {
            // first call: latch the start time
            startTick = (long)(worldTick + time);
        }
        long elapsed = (long)((worldTick + time) - startTick);

        // compute how many slots we should affect
        int foodLevel = client.player.getHungerManager().getFoodLevel();
        int maxSlot   = Math.min(HungerRenderer.ICON_COUNT - 1, Math.max(0, foodLevel - 1));

        // if we've fully rippled all up to maxSlot, mark finished
        float slotDuration = 10;
        float slotDelayTicks = 10;
        long endOfLast = (long)(slotDelayTicks * maxSlot + slotDuration);
        if (elapsed > endOfLast) {
            finished = true;
            return new Vector2f(0, 0);
        }

        // only ripple up to your hunger level
        if (slot > maxSlot) {
            return new Vector2f(0, 0);
        }

        // each slot starts its ripple at slot*delay, lasts slotDuration
        float slotStart = slot * slotDelayTicks;
        float slotEnd   = slotStart + slotDuration;
        if (elapsed < slotStart || elapsed > slotEnd) {
            return new Vector2f(0, 0); // not in this slot’s window
        }

        // normalize progress 0→1 over the slot’s window
        float progress = (elapsed - slotStart) / slotDuration;
        // sin(progress * PI) goes 0→1→0; we only want 0→1, so clamp after π/2
        float sine = (float)Math.sin(progress * Math.PI);
        float upOnly = sine < 0 ? 0 : sine;

        // final upward offset
        float amplitude = 2;
        return new Vector2f(0, -amplitude * upOnly);
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void reset() {
        startTick = -1;
        finished  = false;
    }
}
