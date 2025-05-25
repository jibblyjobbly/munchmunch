package me.jibbly.munchmunch.client.gui.render;

import me.jibbly.munchmunch.MunchMunchClient;
import me.jibbly.munchmunch.client.gui.render.anim.AnimationManager;
import me.jibbly.munchmunch.client.resource.FoodResource;
import me.jibbly.munchmunch.client.resource.HungerIconResourceListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.joml.Vector2f;

@Environment(EnvType.CLIENT)
public class HungerRenderer {
    public static final int ICON_COUNT = 10;
    public static final int ICON_WIDTH = 9;
    public static final int ICON_HEIGHT = 9;
    public static final int ICON_SPACING = 8;

    private static final AnimationManager animationManager = new AnimationManager();

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        float time = client.world.getTime() + tickCounter.getDynamicDeltaTicks();

        for (int slot = 0; slot < ICON_COUNT; ++slot) {
            Vector2f offset = animationManager.computeOffsets(slot, time);
            Vector2f scale = animationManager.computeScale(slot, time);
            renderIcon(context, offset.x, offset.y, scale, slot);
        }
    }

    public static void renderIcon(DrawContext context, float offsetX, float offsetY, Vector2f scale, int slot) {
        MinecraftClient client = MinecraftClient.getInstance();

        Item lastEatenItem = MunchMunchClient.getLastEatenFoodItem();
        boolean useFoodSpecificIcons = MunchMunchClient.getConfig().useLastEatenFoodIcon && lastEatenItem != null;
        FoodResource currentIcons;

        if (useFoodSpecificIcons) {
            currentIcons = HungerIconResourceListener.getInstance().getIconsFor(lastEatenItem);
        } else {
            currentIcons = FoodResource.defaults();
        }

        int iconX = context.getScaledWindowWidth() / 2 + 91 - slot * HungerRenderer.ICON_SPACING - HungerRenderer.ICON_WIDTH;
        int iconY = context.getScaledWindowHeight() - 39;

        Identifier backgroundIcon = FoodResource.currentBackgroundIcon(currentIcons, client.player.hasStatusEffect(StatusEffects.HUNGER));
        Identifier foregroundIcon = FoodResource.currentForegroundIcon(currentIcons, client.player.getHungerManager().getFoodLevel(), slot, client.player.hasStatusEffect(StatusEffects.HUNGER));

        context.getMatrices().push();
        context.getMatrices().translate(iconX + ICON_WIDTH / 2.0f + offsetX, iconY + ICON_HEIGHT / 2.0f + offsetY, 0);
        context.getMatrices().scale(scale.x, scale.y, 1.0f);
        context.getMatrices().translate(-(iconX + ICON_WIDTH / 2.0f), -(iconY + ICON_HEIGHT / 2.0f), 0);

        if (client.player != null && client.interactionManager != null && client.interactionManager.getCurrentGameMode().isSurvivalLike()) {
            context.drawTexture(RenderLayer::getGuiTextured, backgroundIcon, iconX, iconY, 0, 0, ICON_WIDTH, ICON_HEIGHT, ICON_WIDTH, ICON_HEIGHT);

            if (foregroundIcon != null) {
                context.drawTexture(RenderLayer::getGuiTextured, foregroundIcon, iconX, iconY, 0, 0, ICON_WIDTH, ICON_HEIGHT, ICON_WIDTH, ICON_HEIGHT);
            }
        }

        context.getMatrices().pop();
    }
}