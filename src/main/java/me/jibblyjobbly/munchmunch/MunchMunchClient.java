package me.jibblyjobbly.munchmunch;

import me.jibblyjobbly.munchmunch.client.gui.render.HungerRenderer;
import me.jibblyjobbly.munchmunch.config.MunchMunchConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MunchMunchClient implements ClientModInitializer {
	public static final String MOD_ID = "munchmunch";
	private static final Logger LOGGER = LoggerFactory.getLogger("Munch Munch");

	MunchMunchConfig config = AutoConfig.getConfigHolder(MunchMunchConfig.class).getConfig();

	@Override
	public void onInitializeClient() {
		MunchMunchConfig munchMunchConfig = AutoConfig.getConfigHolder(MunchMunchConfig.class).getConfig();
		String id = munchMunchConfig.lastEatenFood;
		if (!id.isEmpty()) {
			MunchMunch.lastEatenId = Identifier.of(id);
		}

		HudLayerRegistrationCallback.EVENT.register(drawer -> {
			drawer.attachLayerBefore(IdentifiedLayer.HOTBAR_AND_BARS, Identifier.of(MOD_ID, "hunger_layer"), HungerRenderer::render);
		});
	}
}