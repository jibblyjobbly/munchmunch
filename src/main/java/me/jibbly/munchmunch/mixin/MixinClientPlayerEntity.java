package me.jibbly.munchmunch.mixin;

import me.jibbly.munchmunch.MunchMunchClient;
import me.jibbly.munchmunch.api.animation.HungerState;
import me.jibbly.munchmunch.client.gui.render.anim.AnimationSelector;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {
    @Unique private int lastFoodLevel = 1;
    @Unique private float lastSaturationLevel = -1f;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;

        int currentFood = player.getHungerManager().getFoodLevel();
        float currentSaturation = player.getHungerManager().getSaturationLevel();

        if ((currentFood > lastFoodLevel || currentSaturation > lastSaturationLevel) && MunchMunchClient.getConfig().useLastEatenFoodIcon && MunchMunchClient.lastUsed != null) {
            MunchMunchClient.setLastEatenFoodItem(MunchMunchClient.lastUsed);
            AnimationSelector.getInstance().setState(HungerState.GAIN);
            MunchMunchClient.lastUsed = null;
        }

        lastFoodLevel = currentFood;
        lastSaturationLevel = currentSaturation;
    }
}
