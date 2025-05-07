package me.jibbly.munchmunch.client.resource;

import me.jibbly.munchmunch.MunchMunchClient;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HungerIconResourceListener implements SimpleSynchronousResourceReloadListener {
    private static final Logger LOGGER = LoggerFactory.getLogger("Munch Munch");

    private final Map<Identifier, FoodResource> icons = new HashMap<>();

    private static final List<String> STATES = List.of(
            "full_hunger",
            "half_hunger",
            "empty_hunger",
            "full",
            "half",
            "empty"
    );

    public void initialize() {
        LOGGER.info("Registering HungerIconReloadListener");
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(this);
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(MunchMunchClient.MOD_ID, "hunger_icons");
    }

    @Override
    public void reload(ResourceManager manager) {
        LOGGER.info("=== HungerIconReloadListener.reload() ===");
        icons.clear();

        String base = "textures/gui/sprites/hunger";
        var found = manager.findResources(base, path -> path.getPath().endsWith(".png"));
        LOGGER.info("Found {} PNGs under '{}'", found.size(), base);

        for (Identifier resId : found.keySet()) {
            LOGGER.debug("Inspecting {}", resId);

            String[] parts = resId.getPath().split("/");
            if (parts.length != 6) {
                LOGGER.warn(" → unexpected path structure ({} parts): {}", parts.length, resId.getPath());
                continue;
            }
            String folderName = parts[4];    // e.g. "cooked_porkchop"
            String fileName   = parts[5];    // e.g. "cooked_porkchop_full_hunger.png"

            // ---- new matching logic ----
            String nameNoExt = fileName.substring(0, fileName.length() - 4);
            String matchedState = null;
            for (String s : STATES) {
                if (nameNoExt.endsWith("_" + s)) {
                    matchedState = s;
                    break;
                }
            }
            if (matchedState == null) {
                LOGGER.warn(" → file '{}' has no known state suffix", fileName);
                continue;
            }
            int cut = nameNoExt.length() - matchedState.length() - 1;
            String foodName = nameNoExt.substring(0, cut);
            String state    = matchedState;
            Identifier foodId = Identifier.of(resId.getNamespace(), foodName);
            Identifier texId  = Identifier.of(
                    resId.getNamespace(),
                    base + "/" + folderName + "/" + fileName
            );

            LOGGER.info(" → {} [{}] → {}", foodName, state, texId);
            icons.compute(foodId, (id, old) -> updateField(old, texId, slotFrom(state)));
        }

        LOGGER.info("Reloaded {} food sprites", icons.size());
    }


    private enum Slot {
        FULL, HALF, EMPTY, FULL_HUNGER, HALF_HUNGER, EMPTY_HUNGER
    }

    private Slot slotFrom(String state) {
        switch (state.toLowerCase(Locale.ROOT)) {
            case "full":          return Slot.FULL;
            case "half":          return Slot.HALF;
            case "empty":         return Slot.EMPTY;
            case "full_hunger":   return Slot.FULL_HUNGER;
            case "half_hunger":   return Slot.HALF_HUNGER;
            case "empty_hunger":  return Slot.EMPTY_HUNGER;
            default:
                LOGGER.warn("Unknown state '{}', skipping", state);
                return null;
        }
    }

    private FoodResource updateField(FoodResource old, Identifier tex, Slot slot) {
        if (slot == null) return old;
        if (old == null) {
            old = new FoodResource(null, null, null, null, null, null);
            LOGGER.debug("  • Creating new FoodResource for slot {}", slot);
        }
        return switch (slot) {
            case FULL ->
                    new FoodResource(tex, old.half(), old.empty(), old.fullHunger(), old.halfHunger(), old.emptyHunger());
            case HALF ->
                    new FoodResource(old.full(), tex, old.empty(), old.fullHunger(), old.halfHunger(), old.emptyHunger());
            case EMPTY ->
                    new FoodResource(old.full(), old.half(), tex, old.fullHunger(), old.halfHunger(), old.emptyHunger());
            case FULL_HUNGER ->
                    new FoodResource(old.full(), old.half(), old.empty(), tex, old.halfHunger(), old.emptyHunger());
            case HALF_HUNGER ->
                    new FoodResource(old.full(), old.half(), old.empty(), old.fullHunger(), tex, old.emptyHunger());
            case EMPTY_HUNGER ->
                    new FoodResource(old.full(), old.half(), old.empty(), old.fullHunger(), old.halfHunger(), tex);
        };
    }

    public FoodResource getIconsFor(Item item) {
        Identifier id = Registries.ITEM.getId(item);
        return icons.getOrDefault(id, defaults());
    }

    private static FoodResource defaults() { return FoodResource.defaults(); }

    public static HungerIconResourceListener getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final HungerIconResourceListener INSTANCE = new HungerIconResourceListener();
    }
}
