package entity;

import java.awt.*;

public class Ball extends MovableObject {
    public Ball(int x, int y, int width, int height, int dx, int dy) {
        super(x, y, width, height, dx, dy);
    }
    public void bounceOffWalls(int panelWidth, int panelHeight) {
        if (x <= 0 || x + width >= panelWidth) dx = -dx;
        if (y <= 0) dy = -dy;
    }
    public boolean bounceOff(Paddle paddle) {
        if (getBounds().intersects(paddle.getBounds()) && dy > 0) {
            dy = -dy;
            // Đổi hướng theo vị trí va chạm
            int hitPos = x + width / 2 - paddle.x;
            if (hitPos < paddle.width / 3) dx = -Math.abs(dx);
            else if (hitPos > 2 * paddle.width / 3) dx = Math.abs(dx);
            return true;
        }
        return false;
    }
    public boolean bounceOff(Brick brick) {
        if (!brick.isDestroyed() && getBounds().intersects(brick.getBounds())) {
            Rectangle ballRect = getBounds();
            Rectangle brickRect = brick.getBounds();

            // Tính độ giao nhau theo 4 hướng
            int overlapLeft   = ballRect.x + ballRect.width - brickRect.x;
            int overlapRight  = brickRect.x + brickRect.width - ballRect.x;
            int overlapTop    = ballRect.y + ballRect.height - brickRect.y;
            int overlapBottom = brickRect.y + brickRect.height - ballRect.y;

            // Lấy độ chồng nhỏ nhất để xác định mặt va chạm
            int minOverlapX = Math.min(overlapLeft, overlapRight);
            int minOverlapY = Math.min(overlapTop, overlapBottom);

            // Va chạm ngang
            if (minOverlapX < minOverlapY) {
                if (overlapLeft < overlapRight) {
                    // Đẩy bóng ra bên trái viên gạch
                    x -= overlapLeft;
                } else {
                    // Đẩy bóng ra bên phải viên gạch
                    x += overlapRight;
                }
                dx = -dx; // Đảo hướng X
            }
            // Va chạm dọc
            else {
                if (overlapTop < overlapBottom) {
                    // Đẩy bóng ra phía trên viên gạch
                    y -= overlapTop;
                } else {
                    // Đẩy bóng ra phía dưới viên gạch
                    y += overlapBottom;
                }
                dy = -dy; // Đảo hướng Y
            }

            return true;
        }
        return false;
    }

    public void reset(int x, int y, int dx, int dy) {
        this.x = x; this.y = y; this.dx = dx; this.dy = dy;
    }
    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillOval(x, y, width, height);
    }
}
