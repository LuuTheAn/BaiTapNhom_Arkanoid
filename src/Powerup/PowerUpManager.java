package Powerup;

import entity.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PowerUpManager {
    private final List<PowerUp> activePowerUps = new ArrayList<>();

    // Thời gian kết thúc hiệu ứng
    private long expandEndTime = 0;
    private long fastBallEndTime = 0;

    // Sinh power-up ngẫu nhiên khi phá gạch
    public void spawnPowerUp(Brick brick) {
        double chance = Math.random();

        int x = brick.x + brick.width / 2;
        int y = brick.y + brick.height / 2;

        if (chance < 0.15) {
            activePowerUps.add(new ExpandPaddlePowerUp(x, y));
        } else if (chance < 0.25) {
            activePowerUps.add(new FastBallPowerUp(x, y));
        }
        // sau này bạn có thể thêm else if (chance < 0.30) { new ShieldPowerUp(x,y); }
    }

    // Cập nhật toàn bộ power-ups và hiệu ứng
    public void update(Ball ball, Paddle paddle, int screenHeight) {
        List<PowerUp> collected = new ArrayList<>();

        for (PowerUp p : activePowerUps) {
            p.update();

            // Khi paddle chạm power-up
            if (p.getBounds().intersects(paddle.getBounds())) {
                p.applyEffect(paddle);

                if (p instanceof FastBallPowerUp) {
                    // Nếu đang có hiệu lực -> chỉ reset thời gian
                    if (ball.isFastBallActive()) {
                        fastBallEndTime = System.currentTimeMillis() + 5000;
                    } else {
                        ball.activateFastBall(); // kích hoạt lần đầu
                        fastBallEndTime = System.currentTimeMillis() + 5000;
                    }
                }

                if (p instanceof ExpandPaddlePowerUp) {
                    if (paddle.isExpanded()) {
                        // Nếu đang mở rộng rồi -> chỉ reset lại thời gian
                        expandEndTime = System.currentTimeMillis() + 5000;
                    } else {
                        // Chưa mở rộng -> kích hoạt lần đầu
                        p.applyEffect(paddle);
                        expandEndTime = System.currentTimeMillis() + 5000;
                    }
                }
                collected.add(p);
            }
        }
        // Xóa power-ups đã ăn hoặc rơi khỏi màn hình
        activePowerUps.removeAll(collected);
        activePowerUps.removeIf(p -> p.getY() > screenHeight);

        // Kiểm tra hết hiệu ứng mở rộng paddle
        // Kiểm tra hết hiệu ứng mở rộng paddle
        if (expandEndTime > 0 && System.currentTimeMillis() > expandEndTime) {
            paddle.setWidth(80);          // trở lại kích thước gốc
            paddle.setExpanded(false);    // đánh dấu hết mở rộng
            expandEndTime = 0;
        }


        // Kiểm tra hết hiệu ứng fast ball
        if (fastBallEndTime > 0 && System.currentTimeMillis() > fastBallEndTime) {
            ball.resetSpeed(); // khôi phục tốc độ ban đầu
            fastBallEndTime = 0;
        }
    }

    // Vẽ tất cả power-ups
    public void render(Graphics2D g) {
        for (PowerUp p : activePowerUps) {
            p.render(g);
        }
    }
}
