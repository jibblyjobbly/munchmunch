package me.jibbly.munchmunch.api.animation;

import me.jibbly.munchmunch.MunchMunchClient;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class HungerState {
    private static final Map<String, HungerState> REGISTRY = new LinkedHashMap<>();

    public static final HungerState IDLE = register("idle");
    public static final HungerState GAIN = register("gain");
    public static final HungerState EMPTY = register("empty");

    private final String id;
    private HungerState(String id) { this.id = id; }

    public static HungerState register(String id) {
        if (REGISTRY.containsKey(id)) MunchMunchClient.LOGGER.error("Hunger state already exists {}", id);
        HungerState state = new HungerState(id);
        REGISTRY.put(id, state);
        return state;
    }

    public static @Nullable HungerState byId(String id) {
        return REGISTRY.get(id);
    }

    public static Collection<HungerState> values() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    @Override public String toString() { return id; }
}
