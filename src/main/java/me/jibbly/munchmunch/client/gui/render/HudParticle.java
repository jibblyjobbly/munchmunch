package me.jibbly.munchmunch.client.gui.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.random.Random;

public class HudParticle {

    Vec2f pos;
    Vec2f vel;
    float scale;
    int age;
    int maxAge;
    float initialAlpha;
    float gravity = 0.02f;
    final boolean isInFront;

    public HudParticle(float x, float y, float initialVelX, float initialVelY, int maxAge, float scale, float alpha, boolean isInFront) {
        this.pos = new Vec2f(x, y);
        this.vel = new Vec2f(initialVelX, initialVelY);
        this.maxAge = maxAge;
        this.scale = scale;
        this.initialAlpha = alpha;
        this.age = 0;
        this.isInFront = isInFront;
    }

    public void tick() {
        this.age++;
        this.vel = new Vec2f(this.vel.x * 0.98f, this.vel.y + gravity);
        this.pos = new Vec2f(this.pos.x + this.vel.x, this.pos.y + this.vel.y);
    }

    public boolean isAlive() {
        return this.age < this.maxAge;
    }

    public boolean isInFront() {
        return this.isInFront;
    }

    public void render(DrawContext context) {
        float progress = (float) this.age / this.maxAge;
        float alpha = Math.max(0f, initialAlpha * (1.0f - progress));
        int size = Math.max(1, (int)(2 * scale));

        int x1 = (int) pos.x - size / 2;
        int y1 = (int) pos.y - size / 2;
        int x2 = x1 + size;
        int y2 = y1 + size;

        int color = ColorHelper.getArgb((int)(alpha * 255), 255, 255, 255); // White with fade alpha
        context.fill(x1, y1, x2, y2, color);
    }

    public static HudParticle createSnowParticle(float x, float y, Random random) {
        float initialVelX = (random.nextFloat() - 0.5f) * 0.3f;
        float initialVelY = random.nextFloat() * 0.1f;
        int maxAge = 40 + random.nextInt(40);
        float scale = 0.5f + random.nextFloat() * 0.5f;
        boolean front = random.nextBoolean(); // Randomly choose front or back
        return new HudParticle(x, y, initialVelX, initialVelY, maxAge, scale, 0.8f, front);
    }
}