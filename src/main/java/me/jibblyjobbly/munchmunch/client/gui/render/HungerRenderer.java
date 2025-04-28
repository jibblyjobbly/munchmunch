package me.jibblyjobbly.munchmunch.client.gui.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jibblyjobbly.munchmunch.MunchMunch;
import me.jibblyjobbly.munchmunch.MunchMunchClient;
import me.jibblyjobbly.munchmunch.resource.FoodIconReloadListener;
import me.jibblyjobbly.munchmunch.resource.FoodTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class HungerRenderer {
    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        var client = MinecraftClient.getInstance();
        if (client.player == null) return;

        int foodLevel = client.player.getHungerManager().getFoodLevel();

        Map<Identifier, FoodTextures> map = FoodIconReloadListener.ICONS;
        Identifier id =  MunchMunch.lastEatenId;
        FoodTextures tex;
        if (id == null) {
            tex = FoodIconReloadListener.DEFAULT_TEXTURES;
        } else {
            tex = map.get(id);
        }

        if (client.player.getGameMode() != GameMode.CREATIVE) {
            if (client.player.getGameMode() != GameMode.SPECTATOR) {
                for (int j = 0; j < 10; j++) {
                    Identifier identifierEmpty;
                    Identifier identifierHalf;
                    Identifier identifierFull;
                    if (client.player.hasStatusEffect(StatusEffects.HUNGER)) {
                        identifierEmpty = tex.emptyHunger();
                        identifierHalf = tex.halfHunger();
                        identifierFull = tex.fullHunger();
                    } else {
                        identifierEmpty = tex.empty();
                        identifierHalf = tex.half();
                        identifierFull = tex.full();
                    }
                    int l = context.getScaledWindowWidth() / 2 + 91 - j * 8 - 9;
                    context.drawGuiTexture(RenderLayer::getGuiTextured, identifierEmpty, l, context.getScaledWindowHeight() - 39, 9, 9);
                    if (j * 2 + 1 < foodLevel) {
                        context.drawGuiTexture(RenderLayer::getGuiTextured, identifierFull, l, context.getScaledWindowHeight() - 39, 9, 9);
                    }

                    if (j * 2 + 1 == foodLevel) {
                        context.drawGuiTexture(RenderLayer::getGuiTextured, identifierHalf, l, context.getScaledWindowHeight() - 39, 9, 9);
                    }
                }
            }
        }
    }
}
