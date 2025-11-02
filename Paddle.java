package entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Paddle extends MovableObject {
    private int speed;
    private BufferedImage image; // 🔹 thêm ảnh paddle

    public Paddle(int x, int y, int width, int height, int speed) {
        super(x, y, width, height, 0, 0);
        this.speed = speed * 2;

        try {
            // 🔹 Đường dẫn tuyệt đối hoặc tương đối
            image = ImageIO.read(getClass().getResourceAsStream("/img/pipe_ngan.png"));

            System.out.println("✅ Paddle image loaded successfully!");
        } catch (IOException e) {
            System.out.println("❌ Paddle image NOT loaded: " + e.getMessage());
            image = null;
        }
    }

    public void moveLeft() {
        x -= speed;
        if (x < 0) x = 0;
    }

    public void moveRight(int panelWidth) {
        x += speed;
        if (x + width > panelWidth) x = panelWidth - width;
    }

    @Override
    public void render(Graphics2D g) {
        if (image != null) {
            // 🔹 Vẽ ảnh paddle
            g.drawImage(image, x, y, width, height, null);
        } else {
            // 🔹 Nếu ảnh lỗi, vẽ tạm hình chữ nhật để tránh crash
            g.setColor(Color.GREEN);
            g.fillRect(x, y, width, height);
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    private boolean expanded = false;

    // Getter/Setter
    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
