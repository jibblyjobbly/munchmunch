package me.jibbly.munchmunch.client.resource;

import net.minecraft.util.Identifier;

public record FoodResource(Identifier full, Identifier half, Identifier empty, Identifier fullHunger, Identifier halfHunger, Identifier emptyHunger) {
    public static FoodResource defaults() {
        return new FoodResource(
                Identifier.ofVanilla("hud/food_full"),
                Identifier.ofVanilla("hud/food_half"),
                Identifier.ofVanilla("hud/food_empty"),
                Identifier.ofVanilla("hud/food_full_hunger"),
                Identifier.ofVanilla("hud/food_half_hunger"),
                Identifier.ofVanilla("hud/food_empty_hunger")
        );
    }

    public static Identifier currentForegroundIcon(FoodResource icons, int foodLevel, int slot, boolean hasHungerEffect) {
        int foodThreshold = slot * 2 + 1;
        boolean isFull = foodLevel >= foodThreshold + 1;

        Identifier foregroundIcon;

        foregroundIcon = hasHungerEffect ? isFull ? icons.fullHunger() : icons.halfHunger() : isFull ? icons.full() : icons.half();

        return foregroundIcon;
    }

    public static Identifier currentBackgroundIcon(FoodResource icons, boolean hasHungerEffect) {
        Identifier backgroundIcon;

        backgroundIcon = hasHungerEffect ? icons.emptyHunger() : icons.empty();

        return backgroundIcon;
    }
}