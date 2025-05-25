package me.jibbly.munchmunch.mixin;

import me.jibbly.munchmunch.MunchMunchClient;
import me.jibbly.munchmunch.client.gui.render.anim.AnimationSelector;
import me.jibbly.munchmunch.client.gui.render.anim.HungerState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Shadow public abstract Item getItem();

    @Inject(method = "finishUsing", at = @At("TAIL"))
    private void onFinishUsing(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = getItem().getDefaultStack();

        if (world.isClient() && user instanceof ClientPlayerEntity) {
            MunchMunchClient.setLastEatenFoodItem(stack.getItem());
            AnimationSelector.getInstance().setState(HungerState.GAIN);
        }
    }
}
