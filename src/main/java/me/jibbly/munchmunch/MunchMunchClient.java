package me.jibbly.munchmunch;

import me.jibbly.munchmunch.client.gui.render.HudParticle;
import me.jibbly.munchmunch.client.gui.render.HungerRenderer;
import me.jibbly.munchmunch.config.MunchMunchConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static int lastFoodLevel = 0;

	// --- Ripple Animation State ---
	private static long rippleStartTime = -1;
	private static int rippleTargetFoodLevel = -1;
	private static boolean lastRippleGolden = false;

	// --- Golden Apple Glow State --- Added ---
	private static long goldenAppleGlowStartTime = -1L;
	private static final long GOLDEN_APPLE_GLOW_DURATION_MS = 5000L;

	// --- Particle State ---
	private static final List<HudParticle> particles = new ArrayList<>();
	private static final Random random = Random.create();

	private static boolean cleanupPerformedThisSession = false;

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing MunchMunch...");

		AutoConfig.register(MunchMunchConfig.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(MunchMunchConfig.class).getConfig();

		HudLayerRegistrationCallback.EVENT.register(drawer -> {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client != null) {
				drawer.attachLayerBefore(IdentifiedLayer.HOTBAR_AND_BARS, HUNGER_LAYER, HungerRenderer::render);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			if (!cleanupPerformedThisSession) {
				cleanupWorldConfigData(client);
				cleanupPerformedThisSession = true;
			}
		});

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

	private void onClientTick(MinecraftClient client) {
		ClientWorld currentWorld = client.world;
		if (client.player == null || currentWorld == null) {
			particles.clear();
			lastFoodLevel = 0;
			resetRipple();
			goldenAppleGlowStartTime = -1L;
			return;
		}

		int currentFoodLevel = client.player.getHungerManager().getFoodLevel();
		float currentSaturationLevel = client.player.getHungerManager().getSaturationLevel();

		if (currentFoodLevel > lastFoodLevel) {
			rippleStartTime = client.world.getTime();
			rippleTargetFoodLevel = currentFoodLevel;

			Item lastItemForThisWorld = getLastEatenFoodItem();
			ItemStack lastStack = (lastItemForThisWorld != null) ? new ItemStack(lastItemForThisWorld) : ItemStack.EMPTY;
			lastRippleGolden = !lastStack.isEmpty() &&
					(lastStack.isOf(Items.GOLDEN_APPLE) ||
							lastStack.isOf(Items.ENCHANTED_GOLDEN_APPLE));
		}

		lastFoodLevel = currentFoodLevel;

		if (goldenAppleGlowStartTime > 0) {
			if (Util.getMeasuringTimeMs() - goldenAppleGlowStartTime > GOLDEN_APPLE_GLOW_DURATION_MS) {
				goldenAppleGlowStartTime = -1L;
			}
		}

		if (config.enableBiomeEffects && config.coldBiomeSettings.enabled && config.coldBiomeSettings.showSnowParticles) {
			var biomeEntry = client.world.getBiome(client.player.getBlockPos());
			if (biomeEntry.getKey().isPresent()) {
				Biome biome = biomeEntry.value();
				if (biome.getTemperature() < 0.2f && particles.size() < config.coldBiomeSettings.maxSnowParticles) {
					if (random.nextInt(5) == 0) {
						int screenWidth = client.getWindow().getScaledWidth();
						int screenHeight = client.getWindow().getScaledHeight();
						int rightX = screenWidth / 2 + 91;
						int topY = screenHeight - 39;
						float spawnX = rightX - HungerRenderer.ICON_COUNT * HungerRenderer.ICON_SPACING * random.nextFloat();
						float spawnY = topY - 5 - random.nextFloat() * 10;
						particles.add(HudParticle.createSnowParticle(spawnX, spawnY, random));
					}
				}
			}
		}
		Iterator<HudParticle> iterator = particles.iterator();
		while (iterator.hasNext()) {
			HudParticle particle = iterator.next();
			particle.tick();
			if (!particle.isAlive()) {
				iterator.remove();
			}
		}
	}

	public static MunchMunchConfig getConfig() { return config; }

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

			if (item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE) {
				goldenAppleGlowStartTime = Util.getMeasuringTimeMs();
			}
		}
	}

	public static List<HudParticle> getParticles() { return particles; }
	public static long getRippleStartTime() { return rippleStartTime; }
	public static boolean isLastRippleGolden() { return lastRippleGolden; }

	public static void resetRipple() {
		rippleStartTime = -1;
		rippleTargetFoodLevel = -1;
		lastRippleGolden = false;
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