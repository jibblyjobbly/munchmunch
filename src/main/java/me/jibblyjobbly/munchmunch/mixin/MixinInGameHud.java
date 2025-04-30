package me.jibblyjobbly.munchmunch.mixin;

import me.jibblyjobbly.munchmunch.config.MunchMunchConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    MunchMunchConfig config = AutoConfig.getConfigHolder(MunchMunchConfig.class).getConfig();

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void cancelRenderFood(CallbackInfo ci) {
        if (config.useLastEatenFood) {
            ci.cancel();
        }
    }
}
