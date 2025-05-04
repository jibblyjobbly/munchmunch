package me.jibbly.munchmunch.mixin;

import me.jibbly.munchmunch.MunchMunchClient;
import me.jibbly.munchmunch.config.MunchMunchConfig;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.MinecraftClient;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Shadow public abstract Item getItem();

    @Inject(method = "finishUsing", at = @At("RETURN"))
    private void munchmunch$onFinishUsingClient(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (!world.isClient || !(user instanceof net.minecraft.client.network.ClientPlayerEntity) || user != MinecraftClient.getInstance().player) {
            return;
        }

        ItemStack stack = (ItemStack) (Object) this;

        if (!stack.contains(DataComponentTypes.FOOD)) {
            return;
        }

        MunchMunchConfig currentConfig = MunchMunchClient.getConfig(); // Get potentially updated config
        if (currentConfig != null && currentConfig.useLastEatenFoodIcon) {
            MunchMunchClient.setLastEatenFoodItem(this.getItem());
        }
    }
}