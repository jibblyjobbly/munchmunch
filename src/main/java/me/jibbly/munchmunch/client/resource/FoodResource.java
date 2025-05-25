package me.jibbly.munchmunch.client.resource;

import net.minecraft.util.Identifier;

public record FoodResource(Identifier full, Identifier half, Identifier empty, Identifier fullHunger, Identifier halfHunger, Identifier emptyHunger) {
    public static FoodResource defaults() {
        return new FoodResource(
                Identifier.ofVanilla("textures/gui/sprites/hud/food_full.png"),
                Identifier.ofVanilla("textures/gui/sprites/hud/food_half.png"),
                Identifier.ofVanilla("textures/gui/sprites/hud/food_empty.png"),
                Identifier.ofVanilla("textures/gui/sprites/hud/food_full_hunger.png"),
                Identifier.ofVanilla("textures/gui/sprites/hud/food_half_hunger.png"),
                Identifier.ofVanilla("textures/gui/sprites/hud/food_empty_hunger.png")
        );
    }

    public static Identifier currentForegroundIcon(FoodResource icons, int foodLevel, int slot, boolean hasHungerEffect) {
        int foodThreshold = slot * 2 + 1;

        Identifier foregroundIcon = null;

        if (foodLevel >= foodThreshold + 1) foregroundIcon = hasHungerEffect ? icons.fullHunger : icons.full;
        else if (foodLevel == foodThreshold) foregroundIcon = hasHungerEffect ? icons.halfHunger : icons.half;

        return foregroundIcon;
    }

    public static Identifier currentBackgroundIcon(FoodResource icons, boolean hasHungerEffect) {
        Identifier backgroundIcon;

        backgroundIcon = hasHungerEffect ? icons.emptyHunger() : icons.empty();

        return backgroundIcon;
    }
}