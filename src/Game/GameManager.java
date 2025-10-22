package Game;

import entity.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private int width, height;
    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks;
    private int score = 0, lives = 3;
    private boolean leftPressed = false, rightPressed = false;
    private boolean gameOver = false;
    private boolean gameWin = false; // ✅ Thêm trạng thái thắng game

    public GameManager(int width, int height) {
        this.width = width;
        this.height = height;
        reset();
    }

    public void reset() {
        paddle = new Paddle(width / 2 - 40, height - 40, 80, 15, 6);
        ball = new Ball(width / 2, height / 2, 12, 12, 4, -4);
        bricks = new ArrayList<>();

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 8; col++) {
                int x = 20 + col * 45;
                int y = 50 + row * 25;

                if (row == 0) {
                    // Hàng trên cùng: gạch không thể phá
                    bricks.add(new UnbreakableBrick(x, y, 40, 20));
                } else if (row == 1) {
                    // Hàng thứ 2: gạch mạnh
                    bricks.add(new StrongBrick(x, y, 40, 20));
                } else if (row == 4) {
                    // 💣 Hàng thứ 5 : gạch nổ
                    bricks.add(new ExplosiveBrick(x, y, 40, 20));
                } else {
                    // Các hàng còn lại: gạch thường
                    bricks.add(new NormalBrick(x, y, 40, 20));
                }
            }
        }


        score = 0;
        lives = 3;
        gameOver = false;
        gameWin = false; // ✅ reset trạng thái thắng
    }

    public void update() {
        if (gameOver || gameWin) return; // ✅ Dừng cập nhật nếu đã thắng hoặc thua

        if (leftPressed) paddle.moveLeft();
        if (rightPressed) paddle.moveRight(width);

        ball.move();
        ball.bounceOffWalls(width, height);

        // Va chạm với paddle
        if (ball.bounceOff(paddle)) {
            System.out.println("Ball hit paddle: " + ball.x + "," + ball.y);
        }

        // Va chạm với gạch
        Brick hitBrick = null;

        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && ball.bounceOff(brick)) {
                hitBrick = brick;
                brick.takeHit();

                if (!(brick instanceof UnbreakableBrick)) {
                    score += 10;
                }

                break;
            }
        }

// ✅ Nếu chạm phải ExplosiveBrick → nổ
        if (hitBrick instanceof ExplosiveBrick) {
            explodeBrick((ExplosiveBrick) hitBrick);
        }



        // Xóa gạch đã bị phá
        bricks.removeIf(Brick::isDestroyed);

        // ✅ Kiểm tra thắng (hết gạch)
        boolean allUnbreakable = true;
        for (Brick brick : bricks) {
            if (!(brick instanceof UnbreakableBrick)) {
                allUnbreakable = false;
                break;
            }
        }

        if (allUnbreakable) {
            gameWin = true;
            System.out.println("You Win!");
        }


        // Kiểm tra bóng rơi khỏi màn hình
        if (ball.y > height) {
            lives--;
            if (lives <= 0) {
                gameOver = true;
            } else {
                ball.reset(width / 2, height / 2, 4, -4);
            }
        }

        // Debug
        System.out.println("Ball: " + ball.x + "," + ball.y + " Paddle: " + paddle.x + "," + paddle.y);
    }

    public void render(Graphics2D g) {
        // Nền
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        // Vẽ paddle, bóng, gạch
        paddle.render(g);
        ball.render(g);
        for (Brick brick : bricks) brick.render(g);

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
        if (key == KeyEvent.VK_R && (gameOver || gameWin)) reset(); // ✅ Cho phép reset cả khi thắng
    }

    public void onKeyReleased(int key) {
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
    }
    // 💥 Xử lý gạch nổ
    private void explodeBrick(ExplosiveBrick center) {
        int explosionRange = 1; // phạm vi nổ 1 ô
        int bw = center.width;
        int bh = center.height;

        List<Brick> toDestroy = new ArrayList<>();

        for (Brick b : bricks) {
            if (b.isDestroyed() || b instanceof UnbreakableBrick) continue;

            // Tính khoảng cách theo lưới (mỗi viên gạch cách nhau theo width/height)
            int dx = Math.abs(b.x - center.x) / bw;
            int dy = Math.abs(b.y - center.y) / bh;

            if (dx <= explosionRange && dy <= explosionRange) {
                toDestroy.add(b);
            }
        }

        // Phá hủy các gạch trong vùng nổ
        for (Brick b : toDestroy) {
            b.takeHit();

            // Nếu là gạch nổ khác → nổ dây chuyền
            if (b instanceof ExplosiveBrick && b != center) {
                explodeBrick((ExplosiveBrick) b);
            }

            if (b.isDestroyed() && !(b instanceof UnbreakableBrick)) {
                score += 10;
            }
        }

        // Xóa gạch đã bị phá
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

