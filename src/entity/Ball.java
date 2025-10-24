package entity;

import java.awt.*;

public class Ball {
    private double x, y;
    private int width, height;
    private double dx, dy;             // vận tốc hiện tại
    private double baseDx, baseDy;     // tốc độ chuẩn ban đầu
    private boolean isFastBallActive;
    private int fastBallTimer;

    private static final int FAST_BALL_DURATION = 600; // ví dụ: 600 frame = 10 giây (tuỳ FPS)

    public Ball(int x, int y, int width, int height, double dx, double dy) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.dx = dx;
        this.dy = dy;
        this.baseDx = dx;
        this.baseDy = dy;
        this.isFastBallActive = false;
        this.fastBallTimer = 0;
    }

    // ===================================================
    // =============== PHẦN CHÍNH ========================
    // ===================================================

    // Di chuyển bóng
    public void move() {
        x += dx;
        y += dy;

        if (isFastBallActive) {
            fastBallTimer--;
            if (fastBallTimer <= 0) {
                resetSpeed(); // hết hiệu lực
            }
        }
    }

    // Render bóng
    public void render(Graphics2D g) {
        g.setColor(Color.WHITE); // luôn trắng, không đổi màu khi fastball
        g.fillOval((int) x, (int) y, width, height);
    }

    // Đảo hướng khi va chạm tường
    public void reverseX() { dx = -dx; }
    public void reverseY() { dy = -dy; }

    // Kích hoạt FastBall
    public void activateFastBall() {
        if (!isFastBallActive) {
            double factor = 1.5; // tăng 50%
            double speed = Math.sqrt(dx * dx + dy * dy);
            double newSpeed = speed * factor;

            double angle = Math.atan2(dy, dx);
            dx = Math.cos(angle) * newSpeed;
            dy = Math.sin(angle) * newSpeed;

            isFastBallActive = true;
            fastBallTimer = FAST_BALL_DURATION;
        } else {
            fastBallTimer = FAST_BALL_DURATION; // reset lại thời gian
        }
    }

    // Reset tốc độ về ban đầu (không đổi hướng)
    public void resetSpeed() {
        double currentSpeed = Math.sqrt(dx * dx + dy * dy);
        double normalSpeed = Math.sqrt(baseDx * baseDx + baseDy * baseDy);
        if (currentSpeed == 0) return;

        double dirX = dx / currentSpeed;
        double dirY = dy / currentSpeed;

        dx = dirX * normalSpeed;
        dy = dirY * normalSpeed;

        isFastBallActive = false;
        fastBallTimer = 0;
    }

    // ===================================================
    // =========== HÀM PHÙ HỢP VỚI GAME MANAGER ==========
    // ===================================================

    public void update(double deltaTime) {
        move(); // deltaTime không cần dùng ở đây
    }

    public void bounceOffWalls(int panelWidth, int panelHeight) {
        if (x <= 0) {
            x = 0;
            dx = Math.abs(dx);
        } else if (x + width >= panelWidth) {
            x = panelWidth - width;
            dx = -Math.abs(dx);
        }

        if (y <= 0) {
            y = 0;
            dy = Math.abs(dy);
        }
    }

    public boolean bounceOff(Paddle paddle) {
        Rectangle ballRect = getBounds();
        Rectangle paddleRect = paddle.getBounds();

        if (ballRect.intersects(paddleRect) && dy > 0) {
            dy = -Math.abs(dy); // bật lên

            // Tính vị trí va chạm để chỉnh hướng
            double hitPos = (x + width / 2.0) - paddle.x;
            if (hitPos < paddle.width / 3.0) dx = -Math.abs(dx);
            else if (hitPos > 2 * paddle.width / 3.0) dx = Math.abs(dx);

            return true;
        }
        return false;
    }

    public boolean bounceOff(Brick brick) {
        if (brick == null || brick.isDestroyed()) return false;

        Rectangle ballRect = getBounds();
        Rectangle brickRect = brick.getBounds();

        if (ballRect.intersects(brickRect)) {
            int overlapLeft   = (int)(ballRect.x + ballRect.width - brickRect.x);
            int overlapRight  = (int)(brickRect.x + brickRect.width - ballRect.x);
            int overlapTop    = (int)(ballRect.y + ballRect.height - brickRect.y);
            int overlapBottom = (int)(brickRect.y + brickRect.height - ballRect.y);

            int minOverlapX = Math.min(overlapLeft, overlapRight);
            int minOverlapY = Math.min(overlapTop, overlapBottom);

            if (minOverlapX < minOverlapY) {
                dx = -dx;
            } else {
                dy = -dy;
            }
            return true;
        }
        return false;
    }

    public void reset(int x, int y, int dx, int dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.baseDx = dx;
        this.baseDy = dy;
        this.isFastBallActive = false;
        this.fastBallTimer = 0;
    }

    // ===================================================
    // =============== HỖ TRỢ VA CHẠM ====================
    // ===================================================

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }

    // ===================================================
    // =============== GETTER / SETTER ===================
    // ===================================================

    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public double getDx() { return dx; }
    public double getDy() { return dy; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setDx(double dx) { this.dx = dx; }
    public void setDy(double dy) { this.dy = dy; }
    public boolean isFastBallActive() { return isFastBallActive; }
}
