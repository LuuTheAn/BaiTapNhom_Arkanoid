package Game;

import entity.*;
import Powerup.*; // ✅ import tất cả power-up
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private int width, height;
    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks;
    private List<PowerUp> powerUps = new ArrayList<>(); // ✅ Danh sách Power-Up
    private int score = 0, lives = 3;
    private boolean leftPressed = false, rightPressed = false;
    private boolean gameOver = false;
    private boolean gameWin = false;

    // ⏱ Thời điểm kết thúc hiệu ứng
    private long expandEndTime = 0;
    private long fastBallEndTime = 0;

    public GameManager(int width, int height) {
        this.width = width;
        this.height = height;
        reset();
    }

    public void reset() {
        paddle = new Paddle(width / 2 - 40, height - 40, 80, 15, 6);
        ball = new Ball(width / 2, height / 2, 12, 12, 4, -4);
        bricks = new ArrayList<>();
        powerUps.clear();

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 8; col++) {
                int x = 20 + col * 45;
                int y = 50 + row * 25;

                if (row == 0) {
                    bricks.add(new UnbreakableBrick(x, y, 40, 20));
                } else if (row == 1) {
                    bricks.add(new StrongBrick(x, y, 40, 20));
                } else if (row == 3) {
                    bricks.add(new ExplosiveBrick(x, y, 40, 20));
                } else {
                    bricks.add(new NormalBrick(x, y, 40, 20));
                }
            }
        }

        score = 0;
        lives = 3;
        gameOver = false;
        gameWin = false;
        expandEndTime = 0;
        fastBallEndTime = 0;
    }

    public void update() {
        if (gameOver || gameWin) return;

        if (leftPressed) paddle.moveLeft();
        if (rightPressed) paddle.moveRight(width);

        ball.move();
        ball.bounceOffWalls(width, height);
        ball.bounceOff(paddle);

        // Va chạm gạch
        Brick hitBrick = null;
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && ball.bounceOff(brick)) {
                hitBrick = brick;
                brick.takeHit();

                // ✅ Chỉ khi gạch vỡ thì mới cộng điểm và tạo PowerUp
                if (brick.isDestroyed() && !(brick instanceof UnbreakableBrick)) {
                    score += 10;

                    // 🔹 Ngẫu nhiên tạo Power-Up (20%)
                    if (Math.random() < 0.2) {
                        double r = Math.random();
                        PowerUp newPowerUp;

                        if (r < 0.5) {
                            newPowerUp = new ExpandPaddlePowerUp(
                                    brick.x + brick.width / 2,
                                    brick.y + brick.height / 2
                            );
                        } else {
                            newPowerUp = new FastBallPowerUp(
                                    brick.x + brick.width / 2,
                                    brick.y + brick.height / 2
                            );
                        }

                        powerUps.add(newPowerUp);
                    }
                }
                break;
            }

        }

        // 💥 Gạch nổ
        if (hitBrick instanceof ExplosiveBrick) {
            explodeBrick((ExplosiveBrick) hitBrick);
        }

        // ✅ Cập nhật power-ups
        List<PowerUp> collected = new ArrayList<>();
        for (PowerUp p : powerUps) {
            p.update();
            if (p.getBounds().intersects(paddle.getBounds())) {
                p.applyEffect(paddle);

                // ⏱ Lưu thời gian hiệu ứng theo loại
                if (p instanceof ExpandPaddlePowerUp) {
                    expandEndTime = System.currentTimeMillis() + 5000;
                } else if (p instanceof FastBallPowerUp) {
                    fastBallEndTime = System.currentTimeMillis() + 5000;
                    // tăng tốc bóng ngay lập tức
                    ball.setSpeed(ball.getDx() * 1.5, ball.getDy() * 1.5);
                }

                collected.add(p);
            }
        }

        // Xóa power-up đã ăn hoặc rơi khỏi màn
        powerUps.removeAll(collected);
        powerUps.removeIf(p -> p.getY() > height);

        // ⏳ Hết hiệu ứng thì reset về bình thường
        if (expandEndTime > 0 && System.currentTimeMillis() > expandEndTime) {
            paddle.setWidth(80);
            expandEndTime = 0;
        }

        if (fastBallEndTime > 0 && System.currentTimeMillis() > fastBallEndTime) {
            // giảm tốc về ban đầu
            double currentSpeedX = ball.getDx();
            double currentSpeedY = ball.getDy();
            ball.setSpeed(currentSpeedX / 1.5, currentSpeedY / 1.5);
            fastBallEndTime = 0;
        }

        // Xóa gạch đã bị phá
        bricks.removeIf(Brick::isDestroyed);

        // Kiểm tra thắng
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

        // Mất bóng
        if (ball.y > height) {
            lives--;
            if (lives <= 0) {
                gameOver = true;
            } else {
                ball.reset(width / 2, height / 2, 4, -4);
            }
        }
    }

    public void render(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        paddle.render(g);
        ball.render(g);
        for (Brick brick : bricks) brick.render(g);
        for (PowerUp p : powerUps) p.render(g);

        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Lives: " + lives, width - 70, 20);

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.setColor(Color.RED);
            g.drawString("GAME OVER", width / 2 - 100, height / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.setColor(Color.WHITE);
            g.drawString("Press R to Restart", width / 2 - 80, height / 2 + 40);
        }

        if (gameWin) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.setColor(Color.GREEN);
            g.drawString("YOU WIN!", width / 2 - 90, height / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.setColor(Color.WHITE);
            g.drawString("Press R to Play Again", width / 2 - 90, height / 2 + 40);
        }
    }

    public void onKeyPressed(int key) {
        if (key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_RIGHT) rightPressed = true;
        if (key == KeyEvent.VK_R && (gameOver || gameWin)) reset();
    }

    public boolean isGameOver() { return gameOver; }
    public boolean isGameWin() { return gameWin; }

    public void onKeyReleased(int key) {
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
    }

    // 💥 Xử lý gạch nổ
    private void explodeBrick(ExplosiveBrick center) {
        int explosionRange = 1;
        int bw = center.width;
        int bh = center.height;

        List<Brick> toDestroy = new ArrayList<>();

        for (Brick b : bricks) {
            if (b.isDestroyed() || b instanceof UnbreakableBrick) continue;

            int dx = Math.abs(b.x - center.x) / bw;
            int dy = Math.abs(b.y - center.y) / bh;

            if (dx <= explosionRange && dy <= explosionRange) {
                toDestroy.add(b);
            }
        }

        for (Brick b : toDestroy) {
            b.takeHit();

            if (b instanceof ExplosiveBrick && b != center) {
                explodeBrick((ExplosiveBrick) b);
            }

            if (b.isDestroyed() && !(b instanceof UnbreakableBrick)) {
                score += 10;

                // 🔹 Khi nổ có thể rơi power-up
                if (Math.random() < 0.1) {
                    double r = Math.random();
                    PowerUp newPowerUp = (r < 0.5)
                            ? new ExpandPaddlePowerUp(b.x + b.width / 2, b.y + b.height / 2)
                            : new FastBallPowerUp(b.x + b.width / 2, b.y + b.height / 2);
                    powerUps.add(newPowerUp);
                }
            }
        }

        bricks.removeIf(Brick::isDestroyed);
        System.out.println("💥 Explosion destroyed " + toDestroy.size() + " bricks!");
    }
}
