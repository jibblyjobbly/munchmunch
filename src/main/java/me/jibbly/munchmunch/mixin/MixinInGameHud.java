package me.jibbly.munchmunch.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    @Inject(method = "renderFood", at = @At(value = "HEAD"), cancellable = true)
    private void munchmunch$cancelRenderFood(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci) {
        ci.cancel();
    }
}