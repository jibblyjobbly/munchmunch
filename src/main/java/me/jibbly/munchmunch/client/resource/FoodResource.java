package me.jibbly.munchmunch.client.resource;

import net.minecraft.util.Identifier;

public record FoodResource(Identifier full, Identifier half, Identifier empty, Identifier fullHunger, Identifier halfHunger, Identifier emptyHunger) {
    public static FoodResource defaults() {
        return new FoodResource(
                new Identifier("minecraft", "textures/gui/sprites/hunger/default/food_full.png"),
                new Identifier("minecraft", "textures/gui/sprites/hunger/default/food_half.png"),
                new Identifier("minecraft", "textures/gui/sprites/hunger/default/food_empty.png"),
                new Identifier("minecraft", "textures/gui/sprites/hunger/default/food_full_hunger.png"),
                new Identifier("minecraft", "textures/gui/sprites/hunger/default/food_half_hunger.png"),
                new Identifier("minecraft", "textures/gui/sprites/hunger/default/food_empty_hunger.png")
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