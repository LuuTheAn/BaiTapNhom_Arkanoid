// 📁 Powerup/FastBallPowerUp.java
package Powerup;

import entity.Ball;
import entity.Paddle;
import java.awt.*;

public class FastBallPowerUp extends PowerUp {
    private final int speedIncrease = 3; // tăng tốc độ bóng
    private Ball ball;

    public FastBallPowerUp(int x, int y) {
        super(x, y, 20, 12);
        this.color = Color.RED;
    }

    // ta truyền ball từ ngoài vào để áp dụng hiệu ứng
    public void applyEffect(Paddle paddle, Ball ball) {
        if (ball != null) {
            this.ball = ball;
            ball.setSpeed(ball.getDx() * 1.5, ball.getDy() * 1.5); // tăng tốc 1.5x
            active = true;
        }
    }

    // không dùng trong trường hợp này
    @Override
    public void applyEffect(Paddle paddle) {}
}
