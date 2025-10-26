package Game;

import Powerup.PowerUpManager;
import entity.*;
import sound.Sound;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private int width, height;
    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks;
    private PowerUpManager powerUpManager;

    private int score = 0, lives = 3;
    private boolean leftPressed = false, rightPressed = false;
    private boolean gameOver = false;
    private boolean gameWin = false;

    private BufferedImage backgroundImage;
    private Sound sound; // 🎵 Thêm âm thanh

    public GameManager(int width, int height) {
        this.width = width;
        this.height = height;

        try {
            backgroundImage = ImageIO.read(getClass().getResource("/img/game_bg.jpg"));
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Không tìm thấy ảnh nền, dùng nền đen mặc định");
            backgroundImage = null;
        }

        sound = new Sound(); // 🔊 Khởi tạo âm thanh
        reset();
    }

    public void reset() {
        paddle = new Paddle(width / 2 - 40, height - 40, 80, 15, 6);
        ball = new Ball(width / 2, height / 2, 12, 12, 4, -4);
        bricks = BrickFactory.createDefaultBricks();
        powerUpManager = new PowerUpManager();

        score = 0;
        lives = 3;
        gameOver = false;
        gameWin = false;
    }

    public void update() {
        if (gameOver || gameWin) return;

        // 🕹 Điều khiển paddle
        if (leftPressed) paddle.moveLeft();
        if (rightPressed) paddle.moveRight(width);

        // 🔴 Cập nhật bóng
        ball.update(1.0 / 60);
        ball.bounceOffWalls(width, height);
        ball.bounceOff(paddle);

        // 🧱 Kiểm tra va chạm gạch
        Brick hitBrick = null;
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && ball.bounceOff(brick)) {
                hitBrick = brick;
                brick.takeHit();

                // ✅ Chỉ cộng điểm khi gạch thực sự bị phá
                if (brick.isDestroyed() && !(brick instanceof UnbreakableBrick)) {
                    score += 10;
                    powerUpManager.spawnPowerUp(brick);
                }

                break;
            }
        }

        // 💥 Nếu là gạch nổ
        if (hitBrick instanceof ExplosiveBrick)
            explodeBrick((ExplosiveBrick) hitBrick);

        // ⚡ Cập nhật power-ups
        powerUpManager.update(ball, paddle, height);

        // 🧱 Xóa gạch đã phá
        bricks.removeIf(Brick::isDestroyed);

        // 🏆 Kiểm tra thắng
        boolean allUnbreakable = bricks.stream().allMatch(b -> b instanceof UnbreakableBrick);
        if (allUnbreakable) {
            gameWin = true;
            sound.play(6); // 🎵 Phát âm thanh thắng (tùy bạn map index)
            System.out.println("You Win!");
        }

        // 💔 Mất bóng
        if (ball.getY() > height) {
            lives--;
            if (lives <= 0) {
                gameOver = true;
                sound.play(5); // 🎵 Phát âm thanh thua
            } else {
                sound.play(12);
                ball.reset(width / 2, height / 2, 4, -4);
            }
        }
    }

    public void render(Graphics2D g) {
        // Nền
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, width, height, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
        }

        // Vẽ paddle, bóng, gạch
        paddle.render(g);
        ball.render(g);
        bricks.forEach(b -> b.render(g));
        powerUpManager.render(g);

        // Hiển thị điểm và mạng
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Lives: " + lives, width - 70, 20);

        // Game Over
        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.setColor(Color.RED);
            g.drawString("GAME OVER", width / 2 - 100, height / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.setColor(Color.WHITE);
            g.drawString("Press R to Restart", width / 2 - 80, height / 2 + 40);
            g.drawString("Press M for Menu", width / 2 - 80, height / 2 + 65);
        }

        // Thắng Game
        if (gameWin) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.setColor(Color.GREEN);
            g.drawString("YOU WIN!", width / 2 - 90, height / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.setColor(Color.WHITE);
            g.drawString("Press R to Play Again", width / 2 - 90, height / 2 + 40);
            g.drawString("Press M for Menu", width / 2 - 90, height / 2 + 65);
        }
    }

    public void onKeyPressed(int key) {
        if (key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_RIGHT) rightPressed = true;
        if (key == KeyEvent.VK_R && (gameOver || gameWin)) reset();
    }

    public void onKeyReleased(int key) {
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
    }

    private void explodeBrick(ExplosiveBrick center) {
        int explosionRange = 1; // phạm vi nổ 1 ô
        int bw = center.getWidth();
        int bh = center.getHeight();

        List<Brick> toDestroy = new ArrayList<>();

        for (Brick b : bricks) {
            if (b.isDestroyed() || b instanceof UnbreakableBrick) continue;

            int dx = Math.abs(b.getX() - center.getX()) / bw;
            int dy = Math.abs(b.getY() - center.getY()) / bh;

            if (dx <= explosionRange && dy <= explosionRange) {
                toDestroy.add(b);
            }
        }

        // Phá hủy các gạch trong vùng nổ
        for (Brick b : toDestroy) {
            b.takeHit();

            if (b instanceof ExplosiveBrick && b != center) {
                explodeBrick((ExplosiveBrick) b);
            }

            if (b.isDestroyed() && !(b instanceof UnbreakableBrick)) {
                score += 10;
            }
        }

        bricks.removeIf(Brick::isDestroyed);
        System.out.println("💥 Explosion destroyed " + toDestroy.size() + " bricks!");
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameWin() {
        return gameWin;
    }
}
