package entity;

import java.awt.*;

public class UnbreakableBrick extends Brick {

    public UnbreakableBrick(int x, int y, int width, int height) {
        // HitPoints đặt cao nhất vì gạch không bao giờ bị phá.
        super(x, y, width, height, Integer.MAX_VALUE);
    }

    @Override
    public void takeHit() {
        // Gạch cứng không giảm hitPoints
        // Có thể thêm hiệu ứng khi bóng chạm'
        System.out.println("Unbreakable brick hit! No damage taken.");
    }

    @Override
    public boolean isDestroyed() {
        // Luôn tồn tại
        return false;
    }

    @Override
    public void render(Graphics2D g) {
        // Vẽ gạch cứng màu xám đậm để phân biệt.
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK); // viền
        g.drawRect(x, y, width, height);
    }
}

