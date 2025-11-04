package entity;

import sound.Sound;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Lớp {@code Ball} đại diện cho quả bóng trong trò chơi Arkanoid.
 * <p>
 * Bóng có thể di chuyển, va chạm với tường, gạch và thanh chắn (paddle).
 * Ngoài ra, bóng còn có thể được tăng tốc tạm thời (Fast Ball Power-up)
 * và có thể reset về trạng thái ban đầu.
 * </p>
 */
public class Ball extends MovableObject {

    /** Tốc độ X gốc để reset */
    private double baseDx;
    /** Tốc độ Y gốc để reset */
    private double baseDy;
    /** Cờ xác định bóng đang ở trạng thái tăng tốc */
    private boolean isFastBallActive;
    /** Bộ đếm thời gian còn lại cho Fast Ball */
    private int fastBallTimer;
    /** Ảnh của bóng */
    private Image ballImage;
    /** Thời gian hiệu lực của Fast Ball (tính theo frame) */
    private static final int FAST_BALL_DURATION = 600;

    /** Hệ thống âm thanh dùng chung */
    private static Sound sound = Sound.getInstance();

    /**
     * Kiểm tra xem bóng có đang ở trạng thái tăng tốc không.
     *
     * @return {@code true} nếu bóng đang ở trạng thái Fast Ball, ngược lại {@code false}.
     */
    public boolean isFastBallActive() {
        return isFastBallActive;
    }

    /** Đảo chiều di chuyển theo trục X */
    public void reverseX() { dx = -dx; }

    /** Đảo chiều di chuyển theo trục Y */
    public void reverseY() { dy = -dy; }

    /**
     * Cập nhật trạng thái bóng mỗi frame.
     *
     * @param deltaTime khoảng thời gian giữa hai frame (chưa dùng trong bản này)
     */
    public void update(double deltaTime) {
        move();
    }

    /**
     * Khởi tạo đối tượng {@code Ball}.
     *
     * @param x      Tọa độ X ban đầu
     * @param y      Tọa độ Y ban đầu
     * @param width  Chiều rộng của bóng
     * @param height Chiều cao của bóng
     * @param dx     Vận tốc theo trục X
     * @param dy     Vận tốc theo trục Y
     */
    public Ball(int x, int y, int width, int height, double dx, double dy) {
        super(x, y, width, height, dx, dy);
        this.baseDx = dx;
        this.baseDy = dy;
        this.isFastBallActive = false;
        this.fastBallTimer = 0;
        loadBallImage();
    }

    /** Tải ảnh cho bóng từ thư mục /img/ball.png */
    private void loadBallImage() {
        try {
            ballImage = ImageIO.read(getClass().getResource("/img/ball.png"));
            ballImage = ballImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("⚠️ Không thể tải ảnh ball.png, dùng hình tròn mặc định.");
            ballImage = null;
        }
    }

    /** Di chuyển bóng và kiểm soát thời gian hiệu lực của Fast Ball */
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

    /**
     * Kích hoạt hiệu ứng bóng nhanh (Fast Ball).
     * Bóng sẽ tăng tốc 50% và hiệu lực duy trì trong thời gian giới hạn.
     */
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

    /** Đưa bóng trở lại tốc độ bình thường sau khi hết hiệu lực Fast Ball */
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

    /**
     * Xử lý va chạm giữa bóng và tường của màn chơi.
     *
     * @param panelWidth  Chiều rộng khung game
     * @param panelHeight Chiều cao khung game
     */
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

    /**
     * Xử lý va chạm giữa bóng và thanh chắn (paddle).
     *
     * @param paddle đối tượng {@code Paddle} để kiểm tra va chạm
     * @return {@code true} nếu có va chạm, ngược lại {@code false}
     */
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

    /**
     * Xử lý va chạm giữa bóng và gạch.
     *
     * @param brick đối tượng {@code Brick} để kiểm tra va chạm
     * @return {@code true} nếu có va chạm, ngược lại {@code false}
     */
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

    /**
     * Đưa bóng về vị trí và tốc độ ban đầu.
     *
     * @param x  Tọa độ X mới
     * @param y  Tọa độ Y mới
     * @param dx Vận tốc X mới
     * @param dy Vận tốc Y mới
     */
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

    /**
     * Vẽ quả bóng lên màn hình.
     *
     * @param g Đối tượng {@link Graphics2D} để vẽ
     */
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
