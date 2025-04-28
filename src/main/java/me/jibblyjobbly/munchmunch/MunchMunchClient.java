package me.jibblyjobbly.munchmunch;

import me.jibblyjobbly.munchmunch.client.gui.render.HungerRenderer;
import me.jibblyjobbly.munchmunch.resource.FoodTextures;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class MunchMunchClient implements ClientModInitializer {
	public static final String MOD_ID = "munchmunch";
	private static final Logger LOGGER = LoggerFactory.getLogger("Munch Munch");

	@Override
	public void onInitializeClient() {
		HudLayerRegistrationCallback.EVENT.register(drawer -> {
			drawer.attachLayerBefore(IdentifiedLayer.HOTBAR_AND_BARS, Identifier.of("munchmunch", "hunger_layer"), HungerRenderer::render);
		});
	}
}





























