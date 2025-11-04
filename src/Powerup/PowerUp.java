package Powerup;

import java.awt.*;
import entity.GameObject;
import entity.Paddle;

public abstract class PowerUp extends GameObject {
    protected int speed = 3;
    protected boolean active = false;
    protected Color color = Color.YELLOW; // <-- thêm field color
    public abstract void applyEffect(Paddle paddle);
    // getters an toàn
    public int getY() { return y; }
    public int getX() { return x; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public PowerUp(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
//abc
    // cập nhật theo frame
    public void update() {
        y += speed;
    }

    // phải implement render để thỏa GameObject contract
    @Override
    public void render(Graphics2D g) {
        g.setColor(color);
        g.fillOval(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawOval(x, y, width, height);
    }
}