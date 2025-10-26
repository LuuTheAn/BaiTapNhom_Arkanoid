package Powerup;

import entity.Ball;
import entity.Paddle;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * FastBallPowerUp: khi paddle ăn power-up này thì GameManager sẽ gọi applyToBall(ball)
 * để tăng tốc Ball. Nhưng để thỏa hợp đồng với PowerUp, chúng ta implement
 * applyEffect(Paddle) (được gọi khi paddle ăn).
 */
public class FastBallPowerUp extends PowerUp {
    private final double speedMultiplier = 1.5;
    private final Color myColor = Color.RED;
    private BufferedImage image; // ảnh power-up



    public FastBallPowerUp(int x, int y) {
        super(x, y, 30, 30);
        this.color = myColor;

        try {
            // 🔹 Đảm bảo đường dẫn ảnh đúng
            image = ImageIO.read(getClass().getResourceAsStream("/img/redball.png"));

        } catch (IOException e) {
            System.out.println("❌ Không thể tải ảnh redball.png: " + e.getMessage());
        }
    }

    @Override
    public void applyEffect(Paddle paddle) {
        this.active = true;
    }

    // Hàm thực sự để áp dụng lên Ball — gọi từ GameManager khi biết Ball hiện tại
    public void applyToBall(Ball ball) {
        if (ball == null) return;

        int newDx = (int) Math.round(ball.getDx() * speedMultiplier);
        int newDy = (int) Math.round(ball.getDy() * speedMultiplier);
        if (newDx == 0) newDx = (ball.getDx() >= 0) ? 1 : -1;
        if (newDy == 0) newDy = (ball.getDy() >= 0) ? 1 : -1;

        ball.setDx(newDx);
        ball.setDy(newDy);
    }

    @Override
    public void render(Graphics2D g) {
        int size = Math.max(width, height); // ép vuông tuyệt đối
        if (image != null) {
            g.drawImage(image, x, y, size, size, null);
        } else {
            g.setColor(Color.RED);
            g.fillOval(x, y, size, size);
            g.setColor(Color.BLACK);
            g.drawOval(x, y, size, size);
        }
    }




}
