package Game;

import Powerup.PowerUpManager;
import entity.*;
import sound.Sound;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * {@code GameManager} là lớp điều khiển chính cho toàn bộ trò chơi Arkanoid.
 * <p>
 * Lớp này chịu trách nhiệm:
 * <ul>
 *   <li>Quản lý các thành phần game như {@link Paddle}, {@link Ball}, {@link Brick} và {@link PowerUpManager}</li>
 *   <li>Xử lý logic va chạm, điểm số, mạng sống và trạng thái của trò chơi</li>
 *   <li>Chuyển màn, tạm dừng, vẽ giao diện và lưu điểm lên bảng xếp hạng</li>
 * </ul>
 */
public class GameManager {

    private int width, height;
    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks;
    private PowerUpManager powerUpManager;

    private int score = 0, lives = 3;
    private int totalScore = 0;
    private boolean leftPressed = false, rightPressed = false;
    private boolean gameOver = false;
    private boolean gameWin = false;
    private boolean paused = false;

    private BufferedImage backgroundImage;
    private Sound sound;

    private int currentLevel = 1;
    private static final int MAX_LEVEL = 5;

    private boolean levelComplete = false;
    private long levelCompleteTime = 0;
    private final int LEVEL_DELAY_MS = 5000;

    private Runnable onReturnToMenu;
    private final LeaderboardManager leaderboardManager = new LeaderboardManager();
    private boolean scoreSaved = false;

    // ===================== CONSTRUCTOR =====================

    /**
     * Tạo mới một {@code GameManager} để điều khiển toàn bộ vòng đời trò chơi.
     *
     * @param width  chiều rộng màn hình game
     * @param height chiều cao màn hình game
     */
    public GameManager(int width, int height) {
        this.width = width;
        this.height = height;

        try {
            backgroundImage = ImageIO.read(getClass().getResource("/img/game_bg.jpg"));
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Không tìm thấy ảnh nền, dùng nền đen mặc định");
            backgroundImage = null;
        }

        sound = Sound.getInstance();
        reset();
    }

    // ===================== TRẠNG THÁI GAME =====================

    /** @return {@code true} nếu game đã kết thúc */
    public boolean isGameOver() { return gameOver; }

    /** @return {@code true} nếu người chơi đã thắng tất cả màn chơi */
    public boolean isGameWin() { return gameWin; }

    /** @return {@code true} nếu game đang tạm dừng */
    public boolean isPaused() { return paused; }

    // ===================== QUẢN LÝ GAME =====================

    /**
     * Reset toàn bộ game về trạng thái ban đầu, chơi lại từ Level 1.
     */
    public void resetGame() {
        System.out.println("🔁 FULL RESET GAME CALLED");
        score = 0;
        totalScore = 0;
        lives = 3;
        currentLevel = 1;

        gameOver = false;
        gameWin = false;
        paused = false;
        levelComplete = false;
        scoreSaved = false;

        loadLevel(currentLevel);
        System.out.println("✅ Game reset hoàn toàn! totalScore = " + totalScore);
    }

    /**
     * Bắt đầu một ván chơi mới hoàn toàn (thường được gọi khi nhấn {@code R}).
     */
    public void startNewGame() {
        System.out.println("🎮 Start New Game!");
        totalScore = 0;
        score = 0;
        lives = 3;
        currentLevel = 1;
        gameOver = false;
        gameWin = false;
        paused = false;
        levelComplete = false;
        scoreSaved = false;
        loadLevel(currentLevel);
    }

    /**
     * Reset dữ liệu chơi (session) mà không load lại level.
     * <p>Thường được gọi khi quay lại menu hoặc chọn level khác.</p>
     */
    public void resetSession() {
        System.out.println("🔄 Reset session (return to menu or new play)!");
        totalScore = 0;
        score = 0;
        lives = 3;
        currentLevel = 1;
        gameOver = false;
        gameWin = false;
        paused = false;
        levelComplete = false;
        scoreSaved = false;
        // Không gọi loadLevel ở đây
    }

    /**
     * Đặt callback sẽ được gọi khi người chơi quay lại menu.
     *
     * @param callback hàm callback khi quay lại menu
     */
    public void setOnReturnToMenu(Runnable callback) {
        this.onReturnToMenu = callback;
    }

    /**
     * Tải dữ liệu level cụ thể, bao gồm gạch, bóng, thanh paddle và power-up.
     *
     * @param level chỉ số level cần tải (1 → 5)
     */
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
        this.paused = false;

        System.out.println("🔹 Loaded Level: " + level + " | totalScore = " + totalScore);
    }

    /** Reset game (load lại level hiện tại) */
    public void reset() {
        loadLevel(currentLevel);
    }

    /** Bật/tắt trạng thái tạm dừng game */
    public void togglePause() {
        paused = !paused;
        sound.play(14);
        System.out.println(paused ? "⏸ Game paused" : "▶ Game resumed");
    }

    /**
     * Cộng thêm điểm cho người chơi.
     *
     * @param points số điểm cần cộng
     */
    public void addScore(int points) {
        this.score += points;
    }

    // ===================== VÒNG LẶP GAME =====================

    /**
     * Cập nhật logic trò chơi trong mỗi khung hình (frame update).
     */
    public void update() {
        if (paused || gameOver || gameWin) return;

        // Kiểm tra hoàn thành màn chơi
        if (levelComplete) {
            long elapsed = System.currentTimeMillis() - levelCompleteTime;
            if (elapsed > LEVEL_DELAY_MS) {
                goToNextLevel();
            }
            return;
        }

        // Điều khiển paddle
        if (leftPressed) paddle.moveLeft();
        if (rightPressed) paddle.moveRight(width);

        // Cập nhật bóng
        ball.update(1.0 / 60);
        ball.bounceOffWalls(width, height);
        ball.bounceOff(paddle);

        // Kiểm tra va chạm gạch
        Brick hitBrick = null;
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && ball.bounceOff(brick)) {
                hitBrick = brick;
                brick.takeHit();

                if (brick.isDestroyed() && !(brick instanceof UnbreakableBrick)) {
                    addScore(10);
                    powerUpManager.spawnPowerUp(brick);
                }
                break;
            }
        }

        // Gạch nổ lan
        if (hitBrick != null && hitBrick.isDestroyed() && hitBrick instanceof ExplosiveBrick) {
            ((ExplosiveBrick) hitBrick).explode(this.bricks, this);
        }

        // Cập nhật power-up
        powerUpManager.update(ball, paddle, height);
        bricks.removeIf(Brick::isDestroyed);

        // Kiểm tra hoàn tất màn
        boolean allUnbreakable = bricks.stream().allMatch(b -> b instanceof UnbreakableBrick);
        if (allUnbreakable && !levelComplete) {
            sound.play(6);
            ProgressManager.unlockNextLevel(currentLevel);

            totalScore += score;
            System.out.println("⭐ Level " + currentLevel + " hoàn thành! TotalScore = " + totalScore);

            levelComplete = true;
            levelCompleteTime = System.currentTimeMillis();
        }

        // Kiểm tra bóng rơi
        if (ball.getY() > height) {
            lives--;
            if (lives <= 0) {
                gameOver = true;
                sound.play(5);
                saveScoreToLeaderboardIfNeeded();
            } else {
                sound.play(12);
                ball.reset(width / 2, height / 2, 4, -4);
            }
        }
    }

    /**
     * Chuyển sang màn chơi tiếp theo hoặc kết thúc game nếu đã đạt màn cuối.
     */
    private void goToNextLevel() {
        levelComplete = false;

        if (currentLevel < MAX_LEVEL) {
            currentLevel++;
            loadLevel(currentLevel);
        } else {
            gameWin = true;
            sound.play(13);
            saveScoreToLeaderboardIfNeeded();
            System.out.println("🏆 YOU WIN ALL LEVELS! Final Score = " + totalScore);
        }
    }

    /**
     * Lưu điểm tổng vào bảng xếp hạng nếu chưa được lưu.
     */
    private void saveScoreToLeaderboardIfNeeded() {
        if (scoreSaved) {
            System.out.println("ℹ️ Score already saved for this session, skipping.");
            return;
        }
        if (totalScore <= 0) {
            System.out.println("ℹ️ No score to save.");
            return;
        }
        leaderboardManager.addScore(totalScore);
        scoreSaved = true;
        System.out.println("💾 Saved TOTAL score: " + totalScore + " vào bảng xếp hạng!");
    }

    // ===================== VẼ GIAO DIỆN =====================

    /**
     * Vẽ toàn bộ khung hình của trò chơi, bao gồm nền, paddle, bóng, gạch, điểm số và các trạng thái.
     *
     * @param g đối tượng {@link Graphics2D} để vẽ
     */
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

        // HUD hiển thị điểm và mạng
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
        g.setColor(Color.YELLOW);
        g.drawString("Total: " + totalScore, 10, 40);

        g.setColor(Color.WHITE);
        String levelText = "Level: " + currentLevel;
        String livesText = "Lives: " + lives;
        FontMetrics fm = g.getFontMetrics();
        g.drawString(levelText, width - fm.stringWidth(levelText) - 10, 20);
        g.drawString(livesText, width - fm.stringWidth(livesText) - 10, 40);

        // Các overlay khác (thắng, thua, pause, level complete)
        // ... (giữ nguyên phần vẽ)
    }

    /**
     * Vẽ text căn giữa theo trục X.
     *
     * @param g        đối tượng Graphics
     * @param text     nội dung cần vẽ
     * @param color    màu chữ
     * @param fontName tên font
     * @param fontSize cỡ chữ
     * @param y        vị trí Y
     */
    private void drawCenteredText(Graphics2D g, String text, Color color, String fontName, int fontSize, int y) {
        Font font = new Font(fontName, Font.BOLD, fontSize);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;

        g.setColor(Color.BLACK);
        g.drawString(text, x + 2, y + 2);

        g.setColor(color);
        g.drawString(text, x, y);
    }

    // ===================== ĐIỀU KHIỂN BÀN PHÍM =====================

    /**
     * Xử lý khi người chơi nhấn phím.
     *
     * @param key mã phím (key code)
     */
    public void onKeyPressed(int key) {
        if (key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_RIGHT) rightPressed = true;

        if (key == KeyEvent.VK_P) togglePause();
        if (paused && key == KeyEvent.VK_C) togglePause();

        if (key == KeyEvent.VK_R && (gameOver || gameWin)) {
            System.out.println("🔁 Restart requested!");
            startNewGame();
        }

        if (levelComplete && key == KeyEvent.VK_N) goToNextLevel();

        if ((paused || levelComplete || gameWin || gameOver) && key == KeyEvent.VK_M) {
            System.out.println("🏠 Quay lại menu...");
            saveScoreToLeaderboardIfNeeded();
            resetSession();

            if (onReturnToMenu != null) onReturnToMenu.run();
            System.out.println("🏠 Trở về menu — totalScore đã reset = " + totalScore);
        }
    }

    /**
     * Xử lý khi người chơi nhả phím.
     *
     * @param key mã phím được nhả
     */
    public void onKeyReleased(int key) {
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
    }
}
