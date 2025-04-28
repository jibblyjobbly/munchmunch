package me.jibblyjobbly.munchmunch.mixin;

import me.jibblyjobbly.munchmunch.MunchMunch;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    @Inject(
            method = "finishUsing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;",
                    shift = At.Shift.AFTER
            )
    )
    private void onFinishUsing(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (user instanceof ServerPlayerEntity player) {
            ItemStack stack = (ItemStack) (Object) this;

            // Check if the item is consumable
            if (stack.contains(DataComponentTypes.CONSUMABLE)) {
                // For food: Ensure hunger was not full before consumption
                if (stack.contains(DataComponentTypes.FOOD)) {
                    // Get food data
                    FoodComponent food = stack.get(DataComponentTypes.FOOD);
                    int currentHunger = player.getHungerManager().getFoodLevel();

                    // Only log if hunger increased (item was actually consumed)
                    if (currentHunger > player.getHungerManager().getFoodLevel() - food.nutrition()) {
                        MunchMunch.lastEatenId = Registries.ITEM.getId(stack.getItem());
                        MunchMunch.LOGGER.info("Burp! Fully ate: {}", MunchMunch.lastEatenId);
                    }
                } else {
                    // Non-food consumable (e.g., potion)
                    MunchMunch.lastEatenId = Registries.ITEM.getId(stack.getItem());
                    MunchMunch.LOGGER.info("Consumed: {}", MunchMunch.lastEatenId);
                }
            }
        }
    }
}