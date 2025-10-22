package entity;

import java.awt.*;

public class ExplosiveBrick extends Brick {
    public ExplosiveBrick(int x, int y, int width, int height) {
        // Phá trong 1 lần va chạm
        super(x, y, width, height, 1);
    }

    @Override
    public void render(Graphics2D g) {
        // Màu đỏ nổi bật cho gạch nổ
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
    }

    @Override
    public void takeHit() {
        hitPoints = 0; // Nổ ngay lập tức
    }
}
