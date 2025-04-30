package me.jibblyjobbly.munchmunch;

import me.jibblyjobbly.munchmunch.resource.FoodIconReloadListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class MunchMunch implements ModInitializer {
    public static volatile Identifier lastEatenId = null;

    @Override
    public void onInitialize() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new FoodIconReloadListener());
    }
}
