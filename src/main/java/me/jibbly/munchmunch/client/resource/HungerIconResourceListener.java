package me.jibbly.munchmunch.client.resource;

import me.jibbly.munchmunch.MunchMunchClient;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HungerIconResourceListener implements SimpleSynchronousResourceReloadListener {
    public static final Identifier LISTENER_ID = Identifier.of(MunchMunchClient.MOD_ID, "hunger_sprites");
    private static final Logger LOGGER = LoggerFactory.getLogger("Munch Munch");

    private final Map<Identifier, FoodResource> ICONS = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return LISTENER_ID;
    }

    // before your loop, define:
    private static final List<String> STATES = List.of(
            "full_hunger",
            "half_hunger",
            "empty_hunger",
            "full",
            "half",
            "empty"
    );

    @Override
    public void reload(ResourceManager manager) {
        LOGGER.info("=== HungerSpritesReloadListener.reload() ===");
        ICONS.clear();

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
            Identifier texId  = Identifier.of(resId.getNamespace(), resId.getPath());

            LOGGER.info(" → {} [{}] → {}", foodName, state, texId);
            ICONS.compute(foodId, (id, old) -> updateField(old, texId, slotFrom(state)));
        }

        LOGGER.info("Reloaded {} food sprites", ICONS.size());
    }



    public FoodResource getIconsFor(Item item) {
        Identifier id = Registries.ITEM.getId(item);
        return ICONS.getOrDefault(id, FoodResource.defaults());
    }

    private enum Slot {
        FULL, HALF, EMPTY, FULL_HUNGER, HALF_HUNGER, EMPTY_HUNGER
    }

    private Slot slotFrom(String state) {
        return switch (state.toLowerCase(Locale.ROOT)) {
            case "full" -> Slot.FULL;
            case "half" -> Slot.HALF;
            case "empty" -> Slot.EMPTY;
            case "full_hunger" -> Slot.FULL_HUNGER;
            case "half_hunger" -> Slot.HALF_HUNGER;
            case "empty_hunger" -> Slot.EMPTY_HUNGER;
            default -> {
                LOGGER.warn("Unknown state '{}', skipping", state);
                yield null;
            }
        };
    }

    private FoodResource updateField(FoodResource old, Identifier tex, Slot slot) {
        if (slot == null) return old;
        if (old == null) {
            old = new FoodResource(null, null, null, null, null, null);
            LOGGER.debug("  • Creating new FoodResource for slot {}", slot);
        }
        return switch (slot) {
            case FULL         -> new FoodResource(tex,      old.half(),  old.empty(),  old.fullHunger(), old.halfHunger(), old.emptyHunger());
            case HALF         -> new FoodResource(old.full(),  tex,         old.empty(),  old.fullHunger(), old.halfHunger(), old.emptyHunger());
            case EMPTY        -> new FoodResource(old.full(),  old.half(),  tex,          old.fullHunger(), old.halfHunger(), old.emptyHunger());
            case FULL_HUNGER  -> new FoodResource(old.full(),  old.half(),  old.empty(), tex,             old.halfHunger(), old.emptyHunger());
            case HALF_HUNGER  -> new FoodResource(old.full(),  old.half(),  old.empty(), old.fullHunger(), tex,             old.emptyHunger());
            case EMPTY_HUNGER -> new FoodResource(old.full(),  old.half(),  old.empty(), old.fullHunger(), old.halfHunger(), tex);
        };
    }

    public static HungerIconResourceListener getInstance() {
        return Holder.INSTANCE;
    }

    private class Holder {
        private static final HungerIconResourceListener INSTANCE = new HungerIconResourceListener();
    }
}
