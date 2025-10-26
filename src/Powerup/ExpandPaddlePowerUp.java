package Powerup;

import entity.Paddle;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ExpandPaddlePowerUp extends PowerUp {
    private final int expandAmount = 50;
    private BufferedImage image; // ảnh power-up



    public ExpandPaddlePowerUp(int x, int y) {
        super(x, y, 30, 30);

        this.width = this.height = 30;

        try {
            // 🔹 Đảm bảo đường dẫn đúng
            image = ImageIO.read(getClass().getResourceAsStream("/img/greenball.png"));

        } catch (IOException e) {
            System.out.println("❌ Không thể tải ảnh greenball.png: " + e.getMessage());
        }
    }

    @Override
    public void applyEffect(Paddle paddle) {
        if (!paddle.isExpanded()) {
            paddle.setWidth(paddle.getWidth() * 2);  // nhân đôi chiều rộng
            paddle.setExpanded(true);
        }
    }

    @Override
    public void render(Graphics2D g) {
        if (image != null) {
            // Giữ đúng tỉ lệ vuông để không méo hình
            int size = Math.max(width, height);

            // Căn giữa khi phóng to (để không lệch)
            int drawX = x - (size - width) / 2;
            int drawY = y - (size - height) / 2;

            g.drawImage(image, drawX, drawY, size, size, null);
        } else {
            // fallback nếu không load được ảnh
            g.setColor(Color.GREEN);
            int size = Math.max(width, height);
            int drawX = x - (size - width) / 2;
            int drawY = y - (size - height) / 2;
            g.fillOval(drawX, drawY, size, size);
            g.setColor(Color.BLACK);
            g.drawOval(drawX, drawY, size, size);
        }
    }



}
