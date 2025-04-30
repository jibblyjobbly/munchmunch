package me.jibblyjobbly.munchmunch;

import me.jibblyjobbly.munchmunch.client.gui.render.HungerRenderer;
import me.jibblyjobbly.munchmunch.config.Config;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MunchMunchClient implements ClientModInitializer {
	public static final String MOD_ID = "munchmunch";
	private static final Logger LOGGER = LoggerFactory.getLogger("Munch Munch");

	@Override
	public void onInitializeClient() {
		HudLayerRegistrationCallback.EVENT.register(drawer -> {
			drawer.attachLayerBefore(IdentifiedLayer.HOTBAR_AND_BARS, Identifier.of("munchmunch", "hunger_layer"), HungerRenderer::render);
		});

		AutoConfig.register(Config.class, GsonConfigSerializer::new);

		Config config = AutoConfig.getConfigHolder(Config.class).getConfig();
		String id = config.lastEatenFood;
		if (!id.isEmpty()) {
			MunchMunch.lastEatenId = Identifier.of(id);
		}
	}
}