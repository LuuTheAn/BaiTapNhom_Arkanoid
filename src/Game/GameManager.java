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
 * Lớp {@code GameManager} là bộ điều khiển trung tâm của trò chơi Arkanoid.
 * <p>
 * Lớp này quản lý toàn bộ logic trò chơi, bao gồm:
 * <ul>
 * <li>Vòng lặp trò chơi (cập nhật logic {@link #update()} và vẽ đồ họa {@link #render(Graphics2D)}).</li>
 * <li>Trạng thái trò chơi (điểm số, mạng sống, thắng, thua, tạm dừng).</li>
 * <li>Quản lý các đối tượng trong game ({@link Paddle}, {@link Ball}, {@link Brick}).</li>
 * <li>Xử lý tiến trình qua các màn chơi và chuyển màn.</li>
 * <li>Quản lý vật phẩm (thông qua {@link PowerUpManager}).</li>
 * <li>Xử lý đầu vào (input) từ người dùng.</li>
 * </ul>
 */
public class GameManager {
    /** Chiều rộng và chiều cao của khu vực chơi game. */
    private int width, height;
    /** Đối tượng thanh đỡ của người chơi. */
    private Paddle paddle;
    /** Đối tượng quả bóng. */
    private Ball ball;
    /** Danh sách các viên gạch trong màn chơi hiện tại. */
    private List<Brick> bricks;
    /** Đối tượng quản lý các vật phẩm (power-up). */
    private PowerUpManager powerUpManager;

    /** Điểm số của màn chơi hiện tại. */
    private int score = 0;
    /** Số mạng sống còn lại của người chơi. */
    private int lives = 3;
    /** Tổng điểm tích lũy qua tất cả các màn chơi (cho một phiên chơi). */
    private int totalScore = 0;
    /** Cờ trạng thái: phím mũi tên trái đang được nhấn. */
    private boolean leftPressed = false;
    /** Cờ trạng thái: phím mũi tên phải đang được nhấn. */
    private boolean rightPressed = false;
    /** Cờ trạng thái: trò chơi đã kết thúc (thua). */
    private boolean gameOver = false;
    /** Cờ trạng thái: người chơi đã thắng (hoàn thành tất cả các màn). */
    private boolean gameWin = false;
    /** Cờ trạng thái: trò chơi đang tạm dừng. */
    private boolean paused = false;

    /** Hình ảnh nền của trò chơi. */
    private BufferedImage backgroundImage;
    /** Đối tượng quản lý âm thanh (Singleton). */
    private Sound sound;

    /** Màn chơi hiện tại. */
    private int currentLevel = 1;
    /** Tổng số màn chơi tối đa. */
    private static final int MAX_LEVEL = 5;

    // ⚡ Trạng thái chuyển màn
    /** Cờ trạng thái: vừa hoàn thành một màn chơi. */
    private boolean levelComplete = false;
    /** Mốc thời gian (ms) khi màn chơi hoàn thành. */
    private long levelCompleteTime = 0;
    /** Thời gian chờ (ms) trước khi tự động chuyển màn. */
    private final int LEVEL_DELAY_MS = 5000; // 5 giây chờ

    /** Callback (hàm) được gọi khi người chơi chọn quay về menu. */
    private Runnable onReturnToMenu;
    /** Đối tượng quản lý bảng xếp hạng. */
    private final LeaderboardManager leaderboardManager = new LeaderboardManager();
    /** Cờ đảm bảo điểm chỉ được lưu một lần mỗi phiên chơi. */
    private boolean scoreSaved = false;

    /**
     * Kiểm tra xem trò chơi đã kết thúc (thua) chưa.
     * @return true nếu đã thua, ngược lại false.
     */
    public boolean isGameOver() { return gameOver; }
    /**
     * Kiểm tra xem người chơi đã thắng trò chơi (hoàn thành tất cả các màn) chưa.
     * @return true nếu đã thắng, ngược lại false.
     */
    public boolean isGameWin() { return gameWin; }

    /**
     * Khởi tạo GameManager với kích thước màn hình cụ thể.
     * Tải hình nền, lấy thể hiện (instance) của Sound và reset trò chơi.
     *
     * @param width Chiều rộng của khu vực chơi game.
     * @param height Chiều cao của khu vực chơi game.
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

    /**
     * Reset toàn bộ trò chơi về trạng thái ban đầu (như mới khởi động).
     * Đặt lại tổng điểm (totalScore) về 0 và bắt đầu từ màn 1.
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
     * Bắt đầu một phiên chơi mới hoàn toàn.
     * Thường được gọi khi nhấn 'R' sau khi thắng hoặc thua.
     * Đặt lại tổng điểm (totalScore) về 0 và bắt đầu từ màn 1.
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
     * Đặt lại phiên chơi (session) hiện tại.
     * Được sử dụng khi người chơi quay lại menu hoặc chọn một màn chơi mới từ {@link LevelSelectPanel}.
     * Đặt lại tổng điểm (totalScore) về 0.
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
        // ❗Không loadLevel ở đây — để LevelSelectPanel gọi loadLevel(level) tương ứng
    }

    /**
     * Thiết lập một hàm callback (dạng {@link Runnable}) sẽ được gọi khi
     * người chơi chọn quay về menu.
     *
     * @param callback Hàm (Runnable) sẽ được thực thi.
     */
    public void setOnReturnToMenu(Runnable callback) {
        this.onReturnToMenu = callback;
    }

    /**
     * Tải và thiết lập một màn chơi cụ thể.
     * Phương thức này tạo ra các viên gạch, đặt lại vị trí bóng, thanh đỡ,
     * và reset điểm số (score) và mạng sống (lives) cho màn chơi đó.
     *
     * @param level Số thứ tự của màn chơi cần tải.
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

    /**
     * Tải lại (reset) màn chơi *hiện tại*.
     * Đây là một phương thức tiện ích gọi {@link #loadLevel(int)} với {@code currentLevel}.
     */
    public void reset() {
        loadLevel(currentLevel);
    }

    /**
     * Bật hoặc tắt trạng thái tạm dừng (pause) của trò chơi.
     * Phát âm thanh tạm dừng khi được gọi.
     */
    public void togglePause() {
        paused = !paused;
        sound.play(14); // Giả sử 14 là âm thanh pause
        System.out.println(paused ? "⏸ Game paused" : "▶ Game resumed");
    }

    /**
     * Thêm điểm vào điểm số của màn chơi hiện tại.
     *
     * @param points Số điểm cần thêm (thường là 10 khi phá gạch).
     */
    public void addScore(int points) {
        this.score += points;
    }

    /**
     * Kiểm tra xem trò chơi có đang tạm dừng hay không.
     *
     * @return true nếu game đang tạm dừng, ngược lại false.
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Vòng lặp cập nhật logic chính của trò chơi (game loop).
     * <p>
     * Phương thức này được gọi liên tục (thường là 60 lần/giây) để
     * xử lý di chuyển của người chơi, cập nhật vị trí bóng,
     * kiểm tra va chạm, xử lý vật phẩm, và kiểm tra các điều kiện thắng/thua/chuyển màn.
     * </p>
     */
    public void update() {
        if (paused || gameOver || gameWin) return; // Không cập nhật gì nếu đang tạm dừng/kết thúc

        // 🕒 Nếu vừa hoàn thành level, đợi trước khi chuyển
        if (levelComplete) {
            long elapsed = System.currentTimeMillis() - levelCompleteTime;
            if (elapsed > LEVEL_DELAY_MS) {
                goToNextLevel(); // Hết thời gian chờ, tự động chuyển
            }
            return; // Không cập nhật logic game khi đang chờ
        }

        // 🎮 Điều khiển paddle
        if (leftPressed) paddle.moveLeft();
        if (rightPressed) paddle.moveRight(width);

        // ⚽ Cập nhật bóng
        ball.update(1.0 / 60); // Giả sử 60 FPS
        ball.bounceOffWalls(width, height);
        ball.bounceOff(paddle);

        // 💥 Va chạm gạch
        Brick hitBrick = null;
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && ball.bounceOff(brick)) {
                hitBrick = brick;
                brick.takeHit(); // Gạch nhận sát thương

                if (brick.isDestroyed() && !(brick instanceof UnbreakableBrick)) {
                    addScore(10);
                    powerUpManager.spawnPowerUp(brick); // Tạo vật phẩm
                }
                break; // Chỉ xử lý 1 va chạm gạch mỗi khung hình
            }
        }

        // Xử lý nổ nếu gạch là loại ExplosiveBrick
        if (hitBrick != null && hitBrick.isDestroyed() && hitBrick instanceof ExplosiveBrick) {
            ((ExplosiveBrick) hitBrick).explode(this.bricks, this);
        }

        // Cập nhật vật phẩm (rơi, va chạm paddle)
        powerUpManager.update(ball, paddle, height);
        // Xóa gạch đã bị phá hủy
        bricks.removeIf(Brick::isDestroyed);

        // ⭐️ Kiểm tra điều kiện thắng màn
        // (Tất cả gạch còn lại đều là loại không thể phá hủy)
        boolean allUnbreakable = bricks.stream().allMatch(b -> b instanceof UnbreakableBrick);
        if (allUnbreakable && !levelComplete) {
            sound.play(6); // Âm thanh thắng màn
            ProgressManager.unlockNextLevel(currentLevel); // Mở khóa màn tiếp theo

            totalScore += score; // Cộng điểm màn này vào tổng điểm
            System.out.println("⭐ Level " + currentLevel + " hoàn thành! TotalScore = " + totalScore);

            levelComplete = true; // Bật cờ hoàn thành màn
            levelCompleteTime = System.currentTimeMillis(); // Bắt đầu đếm giờ chờ
        }

        // 💔 Kiểm tra bóng rơi ra ngoài
        if (ball.getY() > height) {
            lives--;
            if (lives <= 0) {
                gameOver = true;
                sound.play(5); // Âm thanh thua
                saveScoreToLeaderboardIfNeeded(); // Lưu điểm nếu thua
            } else {
                sound.play(12); // Âm thanh mất mạng
                ball.reset(width / 2, height / 2, 4, -4); // Reset bóng
            }
        }
    }

    /**
     * Xử lý logic chuyển sang màn chơi tiếp theo hoặc kết thúc game (khi thắng).
     * Được gọi sau khi thời gian chờ {@code LEVEL_DELAY_MS} kết thúc,
     * hoặc khi người chơi nhấn 'N'.
     */
    private void goToNextLevel() {
        levelComplete = false;

        if (currentLevel < MAX_LEVEL) {
            currentLevel++;
            loadLevel(currentLevel); // Tải màn tiếp theo
        } else {
            gameWin = true; // Thắng toàn bộ game
            sound.play(13); // Âm thanh thắng cuối cùng
            saveScoreToLeaderboardIfNeeded(); // Lưu điểm khi thắng
            System.out.println("🏆 YOU WIN ALL LEVELS! Final Score = " + totalScore);
        }
    }

    /**
     * Lưu tổng điểm ({@code totalScore}) vào bảng xếp hạng.
     * Phương thức này kiểm tra cờ {@code scoreSaved} để đảm bảo điểm
     * chỉ được lưu một lần duy nhất mỗi phiên chơi (session).
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
        scoreSaved = true; // Đánh dấu đã lưu
        System.out.println("💾 Saved TOTAL score: " + totalScore + " vào bảng xếp hạng!");
    }

    /**
     * Vòng lặp vẽ (render) chính.
     * <p>
     * Vẽ tất cả các đối tượng (nền, paddle, bóng, gạch, vật phẩm)
     * và giao diện người dùng (UI) như điểm số, mạng sống, và các
     * màn hình thông báo (Tạm dừng, Thắng, Thua, Hoàn thành màn).
     * </p>
     *
     * @param g Đối tượng {@link Graphics2D} để thực hiện vẽ.
     */
    public void render(Graphics2D g) {
        // Vẽ nền
        if (backgroundImage != null)
            g.drawImage(backgroundImage, 0, 0, width, height, null);
        else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
        }

        // Vẽ các đối tượng game
        paddle.render(g);
        ball.render(g);
        bricks.forEach(b -> b.render(g));
        powerUpManager.render(g);

        // Vẽ UI (Điểm số, Mạng sống)
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

        // Vẽ màn hình Hoàn thành màn (Level Complete)
        if (levelComplete) {
            drawCenteredText(g, "LEVEL " + currentLevel + " COMPLETE!", Color.GREEN, "Comic Sans MS", 48, height / 2 - 80);
            drawCenteredText(g, "Total Score: " + totalScore, Color.ORANGE, "Comic Sans MS", 28, height / 2 - 30);

            long elapsed = System.currentTimeMillis() - levelCompleteTime;
            int remaining = Math.max(0, 5 - (int) (elapsed / 1000));
            drawCenteredText(g, "Next level in: " + remaining + "s", Color.YELLOW, "Arial", 26, height / 2 + 10);

            drawCenteredText(g, "Press N to continue now", Color.WHITE, "Arial", 20, height / 2 + 50);
            drawCenteredText(g, "Press M to return to Menu", Color.WHITE, "Arial", 20, height / 2 + 75);
        }

        // Vẽ màn hình Tạm dừng (Paused)
        if (paused) {
            g.setColor(new Color(0, 0, 0, 150)); // Lớp phủ mờ
            g.fillRect(0, 0, width, height);
            drawCenteredText(g, "PAUSED", Color.YELLOW, "Arial", 48, height / 2 - 30);
            drawCenteredText(g, "Press C to continue", Color.WHITE, "Arial", 24, height / 2 + 20);
            drawCenteredText(g, "Press M to return to Menu", Color.WHITE, "Arial", 24, height / 2 + 50);
        }

        // Vẽ màn hình Thua (Game Over) hoặc Thắng (You Win)
        if (gameOver || gameWin) {
            String mainText = gameOver ? "GAME OVER" : "YOU WIN!";
            Color mainColor = gameOver ? Color.RED : new Color(0, 255, 100);

            drawCenteredText(g, mainText, mainColor, "Arial Black", 50, height / 2 - 60);
            drawCenteredText(g, "Total Score: " + totalScore, Color.ORANGE, "Comic Sans MS", 28, height / 2 - 15);
            drawCenteredText(g, "Press R to Restart", Color.WHITE, "Arial", 20, height / 2 + 30);
            drawCenteredText(g, "Press M for Menu", Color.WHITE, "Arial", 20, height / 2 + 55);
        }
    }

    /**
     * Phương thức tiện ích private để vẽ văn bản căn giữa màn hình
     * với hiệu ứng bóng mờ đơn giản (vẽ 2 lần).
     *
     * @param g         Đối tượng Graphics2D để vẽ.
     * @param text      Chuỗi văn bản cần vẽ.
     * @param color     Màu sắc của văn bản.
     * @param fontName  Tên font chữ.
     * @param fontSize  Kích thước font chữ.
     * @param y         Tọa độ y để vẽ văn bản.
     */
    private void drawCenteredText(Graphics2D g, String text, Color color, String fontName, int fontSize, int y) {
        Font font = new Font(fontName, Font.BOLD, fontSize);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;

        // Vẽ bóng (màu đen, lệch 2px)
        g.setColor(Color.BLACK);
        g.drawString(text, x + 2, y + 2);

        // Vẽ chữ chính
        g.setColor(color);
        g.drawString(text, x, y);
    }

    /**
     * Xử lý sự kiện khi phím được nhấn.
     * Cập nhật các cờ trạng thái ({@code leftPressed}, {@code rightPressed})
     * và xử lý các phím chức năng (Pause, Restart, Menu).
     *
     * @param key Mã phím (ví dụ: {@link KeyEvent#VK_LEFT}).
     */
    public void onKeyPressed(int key) {
        // Điều khiển di chuyển
        if (key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_RIGHT) rightPressed = true;

        // Tạm dừng
        if (key == KeyEvent.VK_P) togglePause();
        if (paused && key == KeyEvent.VK_C) togglePause(); // Tiếp tục

        // Chơi lại
        if (key == KeyEvent.VK_R && (gameOver || gameWin)) {
            System.out.println("🔁 Restart requested!");
            startNewGame();
        }

        // Chuyển màn nhanh
        if (levelComplete && key == KeyEvent.VK_N) goToNextLevel();

        // Quay về Menu (từ các trạng thái Paused, Win, Lose, LevelComplete)
        if ((paused || levelComplete || gameWin || gameOver) && key == KeyEvent.VK_M) {
            System.out.println("🏠 Quay lại menu...");
            saveScoreToLeaderboardIfNeeded(); // Lưu điểm trước khi thoát
            resetSession(); // Reset điểm cho phiên mới

            if (onReturnToMenu != null) onReturnToMenu.run(); // Gọi callback
            System.out.println("🏠 Trở về menu — totalScore đã reset = " + totalScore);
        }
    }

    /**
     * Xử lý sự kiện khi phím được nhả ra.
     * Cập nhật các cờ trạng thái ({@code leftPressed}, {@code rightPressed}).
     *
     * @param key Mã phím (ví dụ: {@link KeyEvent#VK_LEFT}).
     */
    public void onKeyReleased(int key) {
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
    }
}