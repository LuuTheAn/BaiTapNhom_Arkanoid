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
    private Sound sound;

    private int currentLevel = 1;
    private static final int MAX_LEVEL = 5;

    // ⚡ Thêm trạng thái chuyển màn
    private boolean levelComplete = false;
    private long levelCompleteTime = 0;
    private final int LEVEL_DELAY_MS = 5000; // 5 giây chờ

    private Runnable onReturnToMenu; // callback quay về menu

    public GameManager(int width, int height) {
        this.width = width;
        this.height = height;

        try {
            backgroundImage = ImageIO.read(getClass().getResource("/img/game_bg.jpg"));
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Không tìm thấy ảnh nền, dùng nền đen mặc định");
            backgroundImage = null;
        }

        sound = new Sound();
        reset();
    }

    public void setOnReturnToMenu(Runnable callback) {
        this.onReturnToMenu = callback;
    }

    public void loadLevel(int level) {
        this.currentLevel = level;
        this.bricks = BrickFactory.createLevel(level);
        this.paddle = new Paddle(width / 2 - 40, height - 40, 80, 15, 6);
        this.ball = new Ball(width / 2, height / 2, 12, 12, 4, -4);
        this.powerUpManager = new PowerUpManager();

        this.score = 0;
        this.lives = 3;
        this.gameOver = false;
        this.gameWin = false;
        this.levelComplete = false;

        System.out.println("🔹 Loaded Level: " + level);
    }

    public void reset() {
        loadLevel(currentLevel);
    }

    public void update() {
        if (gameOver || gameWin) return;

        // 🕒 Nếu vừa hoàn thành level
        if (levelComplete) {
            long elapsed = System.currentTimeMillis() - levelCompleteTime;
            if (elapsed > LEVEL_DELAY_MS) {
                goToNextLevel();
            }
            return;
        }

        // Paddle control
        if (leftPressed) paddle.moveLeft();
        if (rightPressed) paddle.moveRight(width);

        ball.update(1.0 / 60);
        ball.bounceOffWalls(width, height);
        ball.bounceOff(paddle);

        // Brick collision
        Brick hitBrick = null;
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && ball.bounceOff(brick)) {
                hitBrick = brick;
                brick.takeHit();

                if (brick.isDestroyed() && !(brick instanceof UnbreakableBrick)) {
                    score += 10;
                    powerUpManager.spawnPowerUp(brick);
                }
                break;
            }
        }

        if (hitBrick instanceof ExplosiveBrick)
            explodeBrick((ExplosiveBrick) hitBrick);

        powerUpManager.update(ball, paddle, height);
        bricks.removeIf(Brick::isDestroyed);

        // 🏆 Kiểm tra thắng level
        boolean allUnbreakable = bricks.stream().allMatch(b -> b instanceof UnbreakableBrick);
        if (allUnbreakable && !levelComplete) {
            sound.play(6);
            ProgressManager.unlockNextLevel(currentLevel);
            levelComplete = true;
            levelCompleteTime = System.currentTimeMillis();
            System.out.println("🎯 Level " + currentLevel + " hoàn thành!");
        }

        // 💔 Mất bóng
        if (ball.getY() > height) {
            lives--;
            if (lives <= 0) {
                gameOver = true;
                sound.play(5);
            } else {
                sound.play(12);
                ball.reset(width / 2, height / 2, 4, -4);
            }
        }
    }

    private void goToNextLevel() {
        levelComplete = false;

        if (currentLevel < MAX_LEVEL) {
            currentLevel++;
            loadLevel(currentLevel);
        } else {
            gameWin = true;
            sound.play(13);
            System.out.println("🏆 YOU WIN ALL LEVELS!");
        }
    }

    public void render(Graphics2D g) {
        if (backgroundImage != null)
            g.drawImage(backgroundImage, 0, 0, width, height, null);
        else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
        }

        paddle.render(g);
        ball.render(g);
        bricks.forEach(b -> b.render(g));
        powerUpManager.render(g);

        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Lives: " + lives, width - 70, 20);
        g.drawString("Level: " + currentLevel, width / 2 - 30, 20);

        // 🕒 Hiển thị hoàn thành màn
        if (levelComplete) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.setColor(Color.GREEN);
            g.drawString("LEVEL " + currentLevel + " COMPLETE!", width / 2 - 180, height / 2 - 50);

            // ⏱ Hiển thị đếm ngược
            long elapsed = System.currentTimeMillis() - levelCompleteTime;
            int remaining = Math.max(0, 5 - (int)(elapsed / 1000));
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString("Next level in: " + remaining + "s", width / 2 - 110, height / 2 + 10);

            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.setColor(Color.WHITE);
            g.drawString("Press N to continue now", width / 2 - 100, height / 2 + 45);
            g.drawString("Press M to return to Menu", width / 2 - 100, height / 2 + 70);
        }

        // Game over / Win text
        if (gameOver || gameWin) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.setColor(gameOver ? Color.RED : Color.GREEN);
            g.drawString(gameOver ? "GAME OVER" : "YOU WIN!", width / 2 - 100, height / 2);

            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.setColor(Color.WHITE);
            g.drawString("Press R to Restart", width / 2 - 80, height / 2 + 40);
            g.drawString("Press M for Menu", width / 2 - 80, height / 2 + 65);
        }
    }

    public void onKeyPressed(int key) {
        if (key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_RIGHT) rightPressed = true;

        if (key == KeyEvent.VK_R && (gameOver || gameWin)) reset();

        // 🟢 Qua màn sớm bằng phím N
        if (levelComplete && key == KeyEvent.VK_N) {
            goToNextLevel();
        }

        // 🟠 Quay lại menu ở bất kỳ trạng thái thắng / hoàn thành
        if ((levelComplete || gameWin || gameOver) && key == KeyEvent.VK_M) {
            if (onReturnToMenu != null) onReturnToMenu.run();
        }
    }

    public void onKeyReleased(int key) {
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
    }

    private void explodeBrick(ExplosiveBrick center) {
        int explosionRange = 1;
        int bw = center.getWidth();
        int bh = center.getHeight();
        List<Brick> toDestroy = new ArrayList<>();

        for (Brick b : bricks) {
            if (b.isDestroyed() || b instanceof UnbreakableBrick) continue;

            int dx = Math.abs(b.getX() - center.getX()) / bw;
            int dy = Math.abs(b.getY() - center.getY()) / bh;

            if (dx <= explosionRange && dy <= explosionRange)
                toDestroy.add(b);
        }

        for (Brick b : toDestroy) {
            b.takeHit();
            if (b instanceof ExplosiveBrick && b != center)
                explodeBrick((ExplosiveBrick) b);
            if (b.isDestroyed() && !(b instanceof UnbreakableBrick))
                score += 10;
        }

        bricks.removeIf(Brick::isDestroyed);
        System.out.println("💥 Explosion destroyed " + toDestroy.size() + " bricks!");
    }

    public boolean isGameOver() { return gameOver; }
    public boolean isGameWin() { return gameWin; }
}
