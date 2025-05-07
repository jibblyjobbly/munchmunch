package me.jibbly.munchmunch.client.gui.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jibbly.munchmunch.MunchMunchClient;
import me.jibbly.munchmunch.client.resource.FoodResource;
import me.jibbly.munchmunch.client.resource.HungerIconResourceListener;
import me.jibbly.munchmunch.config.MunchMunchConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class HungerRenderer {
    public static final int ICON_COUNT = 10;
    private static final int ICON_WIDTH = 9;
    private static final int ICON_HEIGHT = 9;
    public static final int ICON_SPACING = 8;

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        MunchMunchConfig config = MunchMunchClient.getConfig();
        int foodLevel = client.player.getHungerManager().getFoodLevel();
        boolean hasHungerEffect = client.player.hasStatusEffect(StatusEffects.HUNGER);

        Item lastEatenItem = MunchMunchClient.getLastEatenFoodItem();
        boolean useFoodSpecificIcons = config.useLastEatenFoodIcon && lastEatenItem != null;
        FoodResource currentIcons;

        if (useFoodSpecificIcons) {
            currentIcons = HungerIconResourceListener.getInstance().getIconsFor(lastEatenItem);
        } else {
            currentIcons = FoodResource.defaults();
        }

        var biomeEntry = client.world.getBiome(client.player.getBlockPos());

        if (biomeEntry.getKey().isEmpty()) return;
        Biome biome = biomeEntry.value();
        float temperature = biome.getTemperature();
        boolean isCold = config.enableBiomeEffects && config.coldBiomeSettings.enabled && temperature < 0.2f;

        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();
        int rightX = screenWidth / 2 + 91;
        int topY = screenHeight - 39;

        float time = (client.world.getTime() + tickCounter.getDynamicDeltaTicks());

        List<HudParticle> particles = MunchMunchClient.getParticles();
        for (HudParticle particle : particles) {
            particle.render(context);
        }

        for (int slot = 0; slot < ICON_COUNT; ++slot) {
            int iconX = rightX - slot * ICON_SPACING - ICON_WIDTH;
            int iconY = topY;

            float offsetX = 0, offsetY = 0, scale = 1.0f;
            float tintR = 1.0f, tintG = 1.0f, tintB = 1.0f, tintA = 1.0f;

            if (config.idleAnimationSettings.enabled) {
                offsetY += MathHelper.sin((time / 20.0f) + slot * 0.5f) * config.idleAnimationSettings.bobbingIntensity;
            }

            if (isCold) {
                float shiverPhase = time * 1.5f + slot * MathHelper.PI * 0.4f;
                offsetX += MathHelper.cos(shiverPhase * 0.7f) * config.coldBiomeSettings.shiverIntensity * 0.4f;
                tintR *= 0.6f;
                tintG *= 0.8f;
                tintB *= 1.0f;
            }

            long rippleStartTime = MunchMunchClient.getRippleStartTime();
            if (config.eatingAnimationSettings.enabled && rippleStartTime > 0) {
                long timeSinceRippleStartTicks = client.world.getTime() - rippleStartTime;
                float baseDurationTicks = config.eatingAnimationSettings.rippleDurationMs / 50.0f;
                float durationMultiplier = MunchMunchClient.isLastRippleGolden() ? config.eatingAnimationSettings.goldenFoodMultiplier : 1.0f;
                float rippleDurationTicks = baseDurationTicks * durationMultiplier;

                if (timeSinceRippleStartTicks < rippleDurationTicks) {
                    int lastAffectedIndex = (foodLevel > 0) ? (foodLevel - 1) / 2 : -1;
                    lastAffectedIndex = Math.min(ICON_COUNT - 1, lastAffectedIndex);

                    float rippleIntensity = config.eatingAnimationSettings.rippleIntensity * (MunchMunchClient.isLastRippleGolden() ? config.eatingAnimationSettings.goldenFoodMultiplier : 1.0f);
                    float overallProgress = timeSinceRippleStartTicks / rippleDurationTicks;

                    int rippleRangeWidth = ICON_COUNT;
                    float iconDistanceFromLeft = (float) slot;
                    float iconDelayFactor = iconDistanceFromLeft / (float) rippleRangeWidth;

                    float effectStartProgress = iconDelayFactor * 0.5f;
                    float effectEndProgress = effectStartProgress + 0.5f;

                    if (slot <= lastAffectedIndex && overallProgress >= effectStartProgress && overallProgress <= effectEndProgress) {
                        float iconProgress = (overallProgress - effectStartProgress) / Math.max(0.001f, effectEndProgress - effectStartProgress);

                        float bounceAmplitude = 4.0f * rippleIntensity;
                        offsetY -= MathHelper.sin(iconProgress * MathHelper.PI) * bounceAmplitude;
                    }

                } else {
                    MunchMunchClient.resetRipple();
                }
            }

            Identifier backgroundIconToDraw = FoodResource.currentBackgroundIcon(currentIcons, hasHungerEffect);
            Identifier foregroundIconToDraw = FoodResource.currentForegroundIcon(currentIcons, foodLevel, slot, hasHungerEffect);

            context.getMatrices().push();
            context.getMatrices().translate(iconX + ICON_WIDTH / 2.0f + offsetX, iconY + ICON_HEIGHT / 2.0f + offsetY, 0);
            context.getMatrices().scale(scale, scale, 1.0f);
            context.getMatrices().translate(-(iconX + ICON_WIDTH / 2.0f), -(iconY + ICON_HEIGHT / 2.0f), 0);

            RenderSystem.setShaderColor(tintR, tintG, tintB, tintA);

            if (client.player != null && client.interactionManager != null && client.interactionManager.getCurrentGameMode().isSurvivalLike()) {
                context.drawGuiTexture(RenderLayer::getGuiTextured, backgroundIconToDraw, iconX, iconY, ICON_WIDTH, ICON_HEIGHT);

                if (foregroundIconToDraw != null) {
                    context.drawGuiTexture(RenderLayer::getGuiTextured, foregroundIconToDraw, iconX, iconY, ICON_WIDTH, ICON_HEIGHT);
                }

                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

                context.getMatrices().pop();
            }

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}