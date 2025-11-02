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

    // SỬA 1: Đã có 'score' ở đây, không cần khai báo lại
    private int score = 0, lives = 3;
    private int totalScore = 0;   // tổng điểm cả game
    private boolean leftPressed = false, rightPressed = false;
    private boolean gameOver = false;
    private boolean gameWin = false;
    private boolean paused = false; // ⏸️ trạng thái tạm dừng

    private BufferedImage backgroundImage;
    private Sound sound;

    private int currentLevel = 1;
    private static final int MAX_LEVEL = 5;

    // ⚡ Trạng thái chuyển màn
    private boolean levelComplete = false;
    private long levelCompleteTime = 0;
    private final int LEVEL_DELAY_MS = 5000; // 5 giây chờ

    private Runnable onReturnToMenu; // callback quay về menu
    private final LeaderboardManager leaderboardManager = new LeaderboardManager();

    private boolean scoreSaved = false; // chỉ lưu điểm 1 lần mỗi ván

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

    // 🎮 Reset toàn bộ game (chơi lại từ đầu)
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

    // 🔁 Bắt đầu một game mới hoàn toàn (khi ấn R sau khi win hoặc thua)
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


    // 🎯 Reset session chơi mới (khi quay lại menu hoặc chọn level mới)
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



    public void setOnReturnToMenu(Runnable callback) {
        this.onReturnToMenu = callback;
    }

    // 🔹 Load 1 level cụ thể
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

    public void reset() {
        loadLevel(currentLevel);
    }

    // ⚙️ Toggle tạm dừng
    public void togglePause() {
        paused = !paused;
        sound.play(14);
        System.out.println(paused ? "⏸ Game paused" : "▶ Game resumed");
    }

    // SỬA 2: Thêm hàm 'addScore' (lấy từ code bị lạc của bạn)
    public void addScore(int points) {
        this.score += points;
    }

    public boolean isPaused() {
        return paused;
    }

    // 🔄 Vòng update logic
    public void update() {
        if (paused || gameOver || gameWin) return;

        // 🕒 Nếu vừa hoàn thành level
        if (levelComplete) {
            long elapsed = System.currentTimeMillis() - levelCompleteTime;
            if (elapsed > LEVEL_DELAY_MS) {
                goToNextLevel();
            }
            return;
        }

        // 🎮 Điều khiển paddle
        if (leftPressed) paddle.moveLeft();
        if (rightPressed) paddle.moveRight(width);

        // ⚽ Cập nhật bóng
        ball.update(1.0 / 60);
        ball.bounceOffWalls(width, height);
        ball.bounceOff(paddle);

        // 💥 Va chạm gạch
        Brick hitBrick = null;
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && ball.bounceOff(brick)) {
                hitBrick = brick;
                brick.takeHit();

                if (brick.isDestroyed() && !(brick instanceof UnbreakableBrick)) {
                    addScore(10);
                    powerUpManager.spawnPowerUp(brick);
                }
                break; // chỉ xử lý 1 viên gạch mỗi frame
            }
        }

        // 💣 Nếu là ExplosiveBrick thì gọi explode() trong class của nó
        if (hitBrick != null && hitBrick.isDestroyed() && hitBrick instanceof ExplosiveBrick) {
            ((ExplosiveBrick) hitBrick).explode(this.bricks, this);
        }

        // ⚡ Cập nhật power-up
        powerUpManager.update(ball, paddle, height);

        // 🧹 Xóa gạch đã phá hủy
        bricks.removeIf(Brick::isDestroyed);

        // 🏆 Kiểm tra thắng level
        boolean allUnbreakable = bricks.stream().allMatch(b -> b instanceof UnbreakableBrick);
        if (allUnbreakable && !levelComplete) {
            sound.play(6);
            ProgressManager.unlockNextLevel(currentLevel);

            totalScore += score;
            System.out.println("⭐ Level " + currentLevel + " hoàn thành! TotalScore = " + totalScore);

            levelComplete = true;
            levelCompleteTime = System.currentTimeMillis();
        }

        // 💔 Mất bóng
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

    // ▶ Sang level kế tiếp
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

    // 🏆 Lưu điểm vào leaderboard (chỉ 1 lần mỗi phiên)
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

    // 🎨 Render
    public void render(Graphics2D g) {
        // 🌄 Vẽ nền
        if (backgroundImage != null)
            g.drawImage(backgroundImage, 0, 0, width, height, null);
        else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
        }

        // 🧱 Vẽ đối tượng
        paddle.render(g);
        ball.render(g);
        bricks.forEach(b -> b.render(g));
        powerUpManager.render(g);

        // 🎨 HUD góc
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

        // 🌟 Hiệu ứng khi hoàn thành level
        if (levelComplete) {
            drawCenteredText(g, "LEVEL " + currentLevel + " COMPLETE!", Color.GREEN, "Comic Sans MS", 48, height / 2 - 80);
            drawCenteredText(g, "Total Score: " + totalScore, Color.ORANGE, "Comic Sans MS", 28, height / 2 - 30);

            long elapsed = System.currentTimeMillis() - levelCompleteTime;
            int remaining = Math.max(0, 5 - (int) (elapsed / 1000));
            drawCenteredText(g, "Next level in: " + remaining + "s", Color.YELLOW, "Arial", 26, height / 2 + 10);

            drawCenteredText(g, "Press N to continue now", Color.WHITE, "Arial", 20, height / 2 + 50);
            drawCenteredText(g, "Press M to return to Menu", Color.WHITE, "Arial", 20, height / 2 + 75);
        }

        // ⏸ Khi pause
        if (paused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, width, height);

            drawCenteredText(g, "PAUSED", Color.YELLOW, "Arial", 48, height / 2 - 30);
            drawCenteredText(g, "Press C to continue", Color.WHITE, "Arial", 24, height / 2 + 20);
            drawCenteredText(g, "Press M to return to Menu", Color.WHITE, "Arial", 24, height / 2 + 50);
        }

        // 💀 Game Over / 🎉 You Win
        if (gameOver || gameWin) {
            String mainText = gameOver ? "GAME OVER" : "YOU WIN!";
            Color mainColor = gameOver ? Color.RED : new Color(0, 255, 100);

            drawCenteredText(g, mainText, mainColor, "Arial Black", 50, height / 2 - 60);
            drawCenteredText(g, "Total Score: " + totalScore, Color.ORANGE, "Comic Sans MS", 28, height / 2 - 15);
            drawCenteredText(g, "Press R to Restart", Color.WHITE, "Arial", 20, height / 2 + 30);
            drawCenteredText(g, "Press M for Menu", Color.WHITE, "Arial", 20, height / 2 + 55);
        }
    }

    private void drawCenteredText(Graphics2D g, String text, Color color, String fontName, int fontSize, int y) {
        Font font = new Font(fontName, Font.BOLD, fontSize);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;

        // Tạo bóng mờ nhẹ
        g.setColor(Color.BLACK);
        g.drawString(text, x + 2, y + 2);

        // Chữ chính
        g.setColor(color);
        g.drawString(text, x, y);
    }

    // ⌨️ Xử lý phím
    public void onKeyPressed(int key) {
        if (key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_RIGHT) rightPressed = true;

        if (key == KeyEvent.VK_P) togglePause();
        if (paused && key == KeyEvent.VK_C) togglePause();

        // 🔁 Restart game khi thắng hoặc thua
        if (key == KeyEvent.VK_R && (gameOver || gameWin)) {
            System.out.println("🔁 Restart requested!");
            startNewGame();
        }

        // ▶ Next level thủ công
        if (levelComplete && key == KeyEvent.VK_N) goToNextLevel();

        // 🏠 Trở về menu
        if ((paused || levelComplete || gameWin || gameOver) && key == KeyEvent.VK_M) {
            System.out.println("🏠 Quay lại menu...");
            saveScoreToLeaderboardIfNeeded();

            // ✅ Reset session sạch trước khi về menu
            resetSession();

            if (onReturnToMenu != null) onReturnToMenu.run();
            System.out.println("🏠 Trở về menu — totalScore đã reset = " + totalScore);
        }
    }

    public void onKeyReleased(int key) {
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
    }

    public boolean isGameOver() { return gameOver; }
    public boolean isGameWin() { return gameWin; }

} // <-- Dấu ngoặc kết thúc lớp GameManager. Mọi code bị lạc bên ngoài đã bị xóa.