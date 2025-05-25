package me.jibbly.munchmunch.api.animation;

import me.jibbly.munchmunch.MunchMunchClient;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.HugeBrownMushroomFeature;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class HungerState {
    private final Identifier id;

    private HungerState(Identifier id) {
        this.id = id;
    }

    public static final Registry<HungerState> REGISTRY = MunchMunchClient.HUNGER_STATE;

    public static final HungerState IDLE = register(Identifier.of(MunchMunchClient.MOD_ID, "idle"));
    public static final HungerState GAIN = register(Identifier.of(MunchMunchClient.MOD_ID, "gain"));

    public static HungerState register(Identifier id) {
        if (REGISTRY.containsId(id)) {
            MunchMunchClient.LOGGER.error("HungerState already exists: {}", id);
            return REGISTRY.get(id);
        }

        HungerState state = new HungerState(id);
        return Registry.register(REGISTRY, id, state);
    }
}
