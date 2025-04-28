package me.jibblyjobbly.munchmunch.resource;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
    private static final Logger LOGGER = LoggerFactory.getLogger("MunchMunch");
    private static final Gson GSON = new Gson();
    private static final Identifier FOODS_JSON = Identifier.of("munchmunch", "foods.json");

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
        return Identifier.of("munchmunch", "foods");  // unique ID :contentReference[oaicite:4]{index=4}
    }

    @Override
    public void reload(ResourceManager manager) {
        ICONS.clear();

        for (String ns : manager.getAllNamespaces()) {                             // :contentReference[oaicite:0]{index=0}
            // 2. Build the path data/<ns>/munchmunch/foods.json
            Identifier path = Identifier.of(ns, "munchmunch/foods.json");
            // 3. Grab ALL matching files (in pack load order) for that path
            List<Resource> jsons = manager.getAllResources(path);                 // :contentReference[oaicite:1]{index=1}
            for (Resource res : jsons) {
                try (var reader = new InputStreamReader(res.getInputStream())) {
                    JsonArray arr = new Gson().fromJson(reader, JsonArray.class);
                    for (JsonElement e : arr) {
                        JsonObject obj       = e.getAsJsonObject();
                        Identifier itemId    = Identifier.of(obj.get("item").getAsString());
                        JsonObject t         = obj.getAsJsonObject("textures");
                        FoodTextures tex     = new FoodTextures(
                                Identifier.of(t.get("full").getAsString()),
                                Identifier.of(t.get("half").getAsString()),
                                Identifier.of(t.get("empty").getAsString()),
                                Identifier.of(t.get("full_hunger").getAsString()),
                                Identifier.of(t.get("half_hunger").getAsString()),
                                Identifier.of(t.get("empty_hunger").getAsString())
                        );
                        // 4. Put or override in your map
                        ICONS.put(itemId, tex);
                    }
                } catch (Exception ex) {
                    LOGGER.error("Failed loading " + path, ex);
                }
            }
        }

        LOGGER.info("Merged {} food entries from all namespaces", ICONS.size());
    }
}