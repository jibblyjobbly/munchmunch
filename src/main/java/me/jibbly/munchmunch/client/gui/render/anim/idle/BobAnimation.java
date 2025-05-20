package me.jibbly.munchmunch.client.gui.render.anim.idle;

import me.jibbly.munchmunch.MunchMunchClient;
import me.jibbly.munchmunch.client.gui.render.HungerRenderer;
import me.jibbly.munchmunch.config.MunchMunchConfig;
import net.minecraft.client.MinecraftClient;
import org.joml.Vector2f;

public class BobAnimation implements IdleAnimation {
    @Override
    public Vector2f offset(int slot, float time) {
        MinecraftClient client = MinecraftClient.getInstance();
        MunchMunchConfig config = MunchMunchClient.getConfig();
        if (client.player == null || client.world == null) {
            return new Vector2f(0, 0);
        }

        MunchMunchConfig.EatingAnimationSettings.IdleAnimationSettings.BobAnimationSettings s = config.eatingAnimationSettings
                .idleAnimationSettings
                .bobAnimationSettings;

        float amplitude       = s.amplitude;
        float slotDelayTicks  = s.slotDelayTicks;
        float slotDuration    = s.slotDurationTicks;
        float cycleDelay      = s.cycleDelayTicks;
        int   count           = HungerRenderer.ICON_COUNT;

        // total per-slot spacing plus the post-cycle pause
        float fullCycle = slotDelayTicks * count + cycleDelay;

        long worldTicks = client.world.getTime();
        // wrap world time + partial render-time into [0, fullCycle)
        float t = (worldTicks + time) % fullCycle;

        // shift the time window so slot 0 starts at t=0, slot 1 at t=slotDelayTicks, etc.
        float slotTime = t - slot * slotDelayTicks;
        // if negative, wrap to the end of our cycle
        if (slotTime < 0) {
            slotTime += fullCycle;
        }

        // if we're past the duration window for this slot, don't animate it
        if (slotTime > slotDuration) {
            return new Vector2f(0, 0);
        }

        // within [0 → slotDuration]: compute progress & sine bob
        float progress = slotTime / slotDuration;
        float wave     = (float)Math.sin(progress * Math.PI);
        float yOff     = Math.max(0f, wave * amplitude);
        return new Vector2f(0, -yOff);
    }
}
