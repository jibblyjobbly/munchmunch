package me.jibblyjobbly.munchmunch.resource;

import com.google.gson.*;
import me.jibblyjobbly.munchmunch.MunchMunchClient;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodIconReloadListener implements SimpleSynchronousResourceReloadListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MunchMunchClient.MOD_ID);
    private static final Gson GSON = new Gson();
    private static final Identifier FOODS_JSON = Identifier.of(MunchMunchClient.MOD_ID, "foods.json");

    public static final Map<Identifier, FoodTextures> ICONS = new HashMap<>();
    public static final FoodTextures DEFAULT_TEXTURES = new FoodTextures(
            Identifier.ofVanilla("hud/food_full"),
            Identifier.ofVanilla("hud/food_half"),
            Identifier.ofVanilla("hud/food_empty"),
            Identifier.ofVanilla("hud/food_full_hunger"),
            Identifier.ofVanilla("hud/food_half_hunger"),
            Identifier.ofVanilla("hud/food_empty_hunger")
    );

    @Override
    public Identifier getFabricId() {
        return Identifier.of(MunchMunchClient.MOD_ID, "foods");
    }

    @Override
    public void reload(ResourceManager manager) {
        ICONS.clear();

        for (String ns : manager.getAllNamespaces()) {
            Identifier path = Identifier.of(ns, "munchmunch/foods.json");
            List<Resource> jsons = manager.getAllResources(path);
            for (Resource res : jsons) {
                try (var reader = new InputStreamReader(res.getInputStream())) {
                    JsonArray arr = JsonParser.parseReader(reader).getAsJsonArray();
                    for (JsonElement elem : arr) {
                        JsonObject obj = elem.getAsJsonObject();
                        Identifier itemId = Identifier.of(obj.get("item").getAsString());
                        JsonObject tex  = obj.getAsJsonObject("textures");
                        Identifier full  = tex.has("full")
                                ? Identifier.of(tex.get("full").getAsString())
                                : DEFAULT_TEXTURES.full();
                        Identifier half  = tex.has("half")
                                ? Identifier.of(tex.get("half").getAsString())
                                : DEFAULT_TEXTURES.half();
                        Identifier empty = tex.has("empty")
                                ? Identifier.of(tex.get("empty").getAsString())
                                : DEFAULT_TEXTURES.empty();
                        Identifier fullHunger  = tex.has("full_hunger")
                                ? Identifier.of(tex.get("full_hunger").getAsString())
                                : DEFAULT_TEXTURES.fullHunger();
                        Identifier halfHunger  = tex.has("half_hunger")
                                ? Identifier.of(tex.get("half_hunger").getAsString())
                                : DEFAULT_TEXTURES.halfHunger();
                        Identifier emptyHunger = tex.has("empty_hunger")
                                ? Identifier.of(tex.get("empty_hunger").getAsString())
                                : DEFAULT_TEXTURES.emptyHunger();
                        ICONS.put(itemId, new FoodTextures(full, half, empty, fullHunger, halfHunger, emptyHunger));
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed loading {}", path, e);
                }
            }
        }
    }
}