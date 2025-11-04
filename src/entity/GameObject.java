package entity;

import java.awt.*;

public abstract class GameObject {
    // ⚙️ Dùng protected để lớp con truy cập được, tránh public lộ toàn bộ
    protected int x, y, width, height;



    public abstract void render(Graphics2D g);

    public void update() {}

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // 🧩 Getter & Setter — để các lớp khác truy cập an toàn
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
