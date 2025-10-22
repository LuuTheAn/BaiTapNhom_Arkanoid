package entity;

import java.awt.*;

public class Paddle extends MovableObject {
    private int speed;
    public Paddle(int x, int y, int width, int height, int speed) {
        super(x, y, width, height, 0, 0);
        this.speed = speed;
    }
    public void moveLeft() {
        x -= speed;
        if (x < 0) x = 0;
    }
    public void moveRight(int panelWidth) {
        x += speed;
        if (x + width > panelWidth) x = panelWidth - width;
    }
    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, width, height);
    }
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

}
