package me.jibbly.munchmunch;

import me.jibbly.munchmunch.api.animation.AnimationEntrypoint;
import me.jibbly.munchmunch.client.gui.render.HungerRenderer;
import me.jibbly.munchmunch.client.gui.render.anim.AnimationManager;
import me.jibbly.munchmunch.client.gui.render.anim.AnimationSelector;
import me.jibbly.munchmunch.api.animation.HungerState;
import me.jibbly.munchmunch.client.resource.HungerIconResourceListener;
import me.jibbly.munchmunch.config.MunchMunchConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MunchMunchClient implements ClientModInitializer {
	public static final String MOD_ID = "munchmunch";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static MunchMunchConfig config;

	private static final Identifier HUNGER_LAYER = Identifier.of(MOD_ID, "hunger_layer");

	public static final RegistryKey<Registry<HungerState>> HUNGER_STATE_KEY = RegistryKey.ofRegistry(Identifier.of(MOD_ID, "hunger_state"));
	public static final Registry<HungerState> HUNGER_STATE = FabricRegistryBuilder.createSimple(HUNGER_STATE_KEY)
			.attribute(RegistryAttribute.SYNCED)
			.buildAndRegister();

	private static int lastFoodLevel = -1;

	private static boolean cleanupPerformedThisSession = false;

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing MunchMunch...");

		AnimationSelector.getInstance().setState(HungerState.IDLE);

		for (EntrypointContainer<AnimationEntrypoint> container : FabricLoader.getInstance().getEntrypointContainers("munchmunch", AnimationEntrypoint.class)) {
			container.getEntrypoint().registerAnimations();
		}

		AutoConfig.register(MunchMunchConfig.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(MunchMunchConfig.class).getConfig();

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(HungerIconResourceListener.getInstance());

		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container -> {
			ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of(MOD_ID, "default"), container, Text.translatable("resourcePack.munchmunch.default.name"), ResourcePackActivationType.DEFAULT_ENABLED);
		});

		HudLayerRegistrationCallback.EVENT.register(drawer -> {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client != null) {
				drawer.attachLayerBefore(IdentifiedLayer.HOTBAR_AND_BARS, HUNGER_LAYER, HungerRenderer::render);
			}
		});

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			if (!cleanupPerformedThisSession) {
				cleanupWorldConfigData(client);
				cleanupPerformedThisSession = true;
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(MunchMunchClient::onClientEndTick);

		LOGGER.info("MunchMunch Initialized!");
	}

	@Nullable
	private static String getCurrentWorldIdentifier(MinecraftClient client) {
		if (client == null) return null;

		if (client.getCurrentServerEntry() != null) {
			return client.getCurrentServerEntry().address;
		}

		if (client.isIntegratedServerRunning() && client.getServer() != null) {
			try {
				Path worldDir = client.getServer().getSavePath(WorldSavePath.ROOT).getParent();
				if (worldDir != null && worldDir.getFileName() != null) {
					return worldDir.getFileName().toString();
				}
			} catch (Exception e) {
				LOGGER.error("Failed to get singleplayer world save directory name ", e);
			}
		}

		return null;
	}

	public static MunchMunchConfig getConfig() { return config; }

	private static void onClientEndTick(MinecraftClient client) {
		AnimationManager.cleanupFinishedAnimation();

        if (client.player != null) {
			int current = client.player.getHungerManager().getFoodLevel();

			if (lastFoodLevel < 0) {
				lastFoodLevel = current;
				return;
			}

			ItemStack lastUsed = client.player.getStackInHand(Hand.MAIN_HAND);

			if (current > lastFoodLevel) {
				if (lastUsed.contains(DataComponentTypes.FOOD)) {
					setLastEatenFoodItem(lastUsed.getItem());
					AnimationSelector.getInstance().setState(HungerState.GAIN);
				}
			}

			lastFoodLevel = current;
		}
	}

	@Nullable
	public static Item getLastEatenFoodItem() {
		MinecraftClient client = MinecraftClient.getInstance();
		String worldId = getCurrentWorldIdentifier(client);

		if (config != null && worldId != null) {
			String itemIdString = config.worldLastEatenFoodMap.get(worldId);
			if (itemIdString != null && !itemIdString.isEmpty()) {
				Identifier itemId = Identifier.tryParse(itemIdString);
				if (itemId != null) {
					return Registries.ITEM.get(itemId);
				}
			}
		}

		return null;
	}

	public static void setLastEatenFoodItem(Item item) {
		MinecraftClient client = MinecraftClient.getInstance();
		String worldId = getCurrentWorldIdentifier(client);

		if (item != null && worldId != null && config != null) {
			Identifier itemId = Registries.ITEM.getId(item);
			String itemIdString = itemId.toString();

			config.worldLastEatenFoodMap.put(worldId, itemIdString);

			AutoConfig.getConfigHolder(MunchMunchConfig.class).save();
		}
	}

	private static void cleanupWorldConfigData(MinecraftClient client) {
		if (config == null || config.worldLastEatenFoodMap == null || config.worldLastEatenFoodMap.isEmpty()) {
			return;
		}

		LOGGER.info("Checking for old singleplayer world data in Munch Munch! config...");
		Path savesDir = client.runDirectory.toPath().resolve("saves");
		Set<String> currentSaveDirs;

		try (Stream<Path> stream = Files.list(savesDir)) {
			currentSaveDirs = stream
					.filter(Files::isDirectory)
					.map(path -> path.getFileName().toString())
					.collect(Collectors.toSet());
		} catch (IOException e) {
			LOGGER.error("Error looking in saves directory: ", e);
			return;
		}

		Set<String> keysToRemove = config.worldLastEatenFoodMap.keySet().stream()
				.filter(key -> key != null && !key.contains(":") && !currentSaveDirs.contains(key))
				.collect(Collectors.toSet());

		if (!keysToRemove.isEmpty()) {
			LOGGER.info("Removing {} old world entries from config: {}", keysToRemove.size(), keysToRemove);
			keysToRemove.forEach(config.worldLastEatenFoodMap::remove);
			AutoConfig.getConfigHolder(MunchMunchConfig.class).save();
		} else {
			LOGGER.info("No old singleplayer world data found to remove");
		}
	}
}