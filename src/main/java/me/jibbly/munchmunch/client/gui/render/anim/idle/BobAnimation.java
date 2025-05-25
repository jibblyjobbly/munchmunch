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
        if (client.player == null || client.world == null) {
            return new Vector2f(0, 0);
        }

        float amplitude       = 2;
        float slotDelayTicks  = 5;
        float slotDuration    = 12;
        float cycleDelay      = 400;
        int   count           = HungerRenderer.ICON_COUNT;

        float fullCycle = slotDelayTicks * count + cycleDelay;

        long worldTicks = client.world.getTime();
        float t = (worldTicks + time) % fullCycle;

        float slotTime = t - slot * slotDelayTicks;
        if (slotTime < 0) {
            slotTime += fullCycle;
        }

        if (slotTime > slotDuration) {
            return new Vector2f(0, 0);
        }

        float progress = slotTime / slotDuration;
        float wave     = (float)Math.sin(progress * Math.PI);
        float yOff     = Math.max(0f, wave * amplitude);
        return new Vector2f(0, -yOff);
    }
}
