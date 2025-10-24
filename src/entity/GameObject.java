
package entity;

import java.awt.*;

public abstract class GameObject {
    public int x, y, width, height;
    public GameObject(int x, int y, int width, int height) {
        this.x = x; this.y = y; this.width = width; this.height = height;
    }
    public abstract void render(Graphics2D g);
    public void update() {}
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
