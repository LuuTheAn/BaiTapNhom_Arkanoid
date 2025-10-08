package entity;

import java.awt.*;

public class StrongBrick extends Brick {
    public StrongBrick(int x, int y, int width, int height) {
        super(x, y, width, height, 2);
    }
    @Override
    public void render(Graphics2D g) {
        g.setColor(hitPoints == 2 ? Color.RED : Color.PINK);
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
    }
}
