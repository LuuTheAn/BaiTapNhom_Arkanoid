package Powerup;

import entity.Paddle;
import java.awt.*;

public class ExpandPaddlePowerUp extends PowerUp {
    private final int expandAmount = 50; // độ dài mở rộng thêm
    private final Color color = Color.GREEN;

    public ExpandPaddlePowerUp(int x, int y) {
        super(x, y, 20, 10);
    }

    @Override
    public void applyEffect(Paddle paddle) {
        if (!paddle.isExpanded()) {
            paddle.setWidth(paddle.getWidth() * 2);  // ví dụ: nhân đôi chiều rộng
            paddle.setExpanded(true);                 // đánh dấu đang mở rộng
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }
}
