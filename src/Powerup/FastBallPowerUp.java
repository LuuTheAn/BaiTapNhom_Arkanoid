package Powerup;

import entity.Ball;
import entity.Paddle;
import java.awt.*;

/**
 * FastBallPowerUp: khi paddle ăn power-up này thì GameManager sẽ gọi applyToBall(ball)
 * để tăng tốc Ball. Nhưng để thỏa hợp đồng với PowerUp, chúng ta implement
 * applyEffect(Paddle) (được gọi khi paddle ăn).
 */
public class FastBallPowerUp extends PowerUp {
    private final double speedMultiplier = 1.5;
    private final Color myColor = Color.RED;

    public FastBallPowerUp(int x, int y) {
        super(x, y, 20, 12);
        this.color = myColor;
    }

    // Bắt buộc override để thỏa abstract method trong PowerUp
    @Override
    public void applyEffect(Paddle paddle) {
        // đánh dấu đã được kích hoạt; thực tế tăng tốc bóng sẽ do GameManager làm
        // (GameManager có tham chiếu tới Ball). Giữ để interface nhất quán.
        this.active = true;
        // không thao tác trực tiếp với paddle ở đây
    }

    // Hàm thực sự để áp dụng lên Ball — gọi từ GameManager khi biết Ball hiện tại
    public void applyToBall(Ball ball) {
        if (ball == null) return;

        // preserve sign (để dx/dy vẫn có hướng đúng)
        int newDx = (int) Math.round(ball.getDx() * speedMultiplier);
        int newDy = (int) Math.round(ball.getDy() * speedMultiplier);

        // Nếu dx/dy có giá trị 0 (không nên) thì đảm bảo tốc độ tối thiểu
        if (newDx == 0) newDx = (ball.getDx() >= 0) ? 1 : -1;
        if (newDy == 0) newDy = (ball.getDy() >= 0) ? 1 : -1;

        ball.setDx(newDx);
        ball.setDy(newDy);
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(myColor);
        g.fillOval(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawOval(x, y, width, height);
    }
}
