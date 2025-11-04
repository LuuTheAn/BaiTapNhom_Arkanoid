package entity;

import sound.Sound;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Ball extends MovableObject {
    private double baseDx, baseDy;  // tốc độ gốc để reset
    private boolean isFastBallActive;
    private int fastBallTimer;
    private Image ballImage;
    private static final int FAST_BALL_DURATION = 600;

    // 🔊 Hệ thống âm thanh dùng chung
    private static Sound sound = Sound.getInstance();

    public boolean isFastBallActive() {
        return isFastBallActive;
    }

    public void reverseX() { dx = -dx; }
    public void reverseY() { dy = -dy; }

    public void update(double deltaTime) {
        move();
    }

    public Ball(int x, int y, int width, int height, double dx, double dy) {
        super(x, y, width, height, dx, dy);  // ✅ dùng MovableObject
        this.baseDx = dx;
        this.baseDy = dy;
        this.isFastBallActive = false;
        this.fastBallTimer = 0;
        loadBallImage();
    }

    private void loadBallImage() {
        try {
            ballImage = ImageIO.read(getClass().getResource("/img/ball.png"));
            ballImage = ballImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("⚠️ Không thể tải ảnh ball.png, dùng hình tròn mặc định.");
            ballImage = null;
        }
    }

    public void move() {
        x += dx;
        y += dy;

        if (isFastBallActive) {
            fastBallTimer--;
            if (fastBallTimer <= 0) {
                resetSpeed();
            }
        }
    }

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
            fastBallTimer = FAST_BALL_DURATION;
        }
    }

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
            dy = -Math.abs(dy);

            double hitPos = (x + width / 2.0) - paddle.x;
            if (hitPos < paddle.width / 3.0) dx = -Math.abs(dx);
            else if (hitPos > 2 * paddle.width / 3.0) dx = Math.abs(dx);

            sound.play(11);
            return true;
        }
        return false;
    }

    public boolean bounceOff(Brick brick) {
        if (!brick.isDestroyed() && getBounds().intersects(brick.getBounds())) {
            Rectangle ballRect = getBounds();
            Rectangle brickRect = brick.getBounds();

            int overlapLeft   = ballRect.x + ballRect.width - brickRect.x;
            int overlapRight  = brickRect.x + brickRect.width - ballRect.x;
            int overlapTop    = ballRect.y + ballRect.height - brickRect.y;
            int overlapBottom = brickRect.y + brickRect.height - ballRect.y;

            int minOverlapX = Math.min(overlapLeft, overlapRight);
            int minOverlapY = Math.min(overlapTop, overlapBottom);

            if (minOverlapX < minOverlapY) {
                if (overlapLeft < overlapRight) x -= overlapLeft;
                else x += overlapRight;
                dx = -dx;
            } else {
                if (overlapTop < overlapBottom) y -= overlapTop;
                else y += overlapBottom;
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

    @Override
    public void render(Graphics2D g) {
        if (ballImage != null) {
            g.drawImage(ballImage, (int)x, (int)y, null);
        } else {
            g.setColor(Color.WHITE);
            g.fillOval((int)x, (int)y, width, height);
        }
    }
}
