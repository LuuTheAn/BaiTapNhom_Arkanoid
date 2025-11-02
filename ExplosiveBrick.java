package entity;

import sound.Sound;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class ExplosiveBrick extends Brick {
    private static BufferedImage[] textures; // ✅ danh sách ảnh
    private static boolean loaded = false;

    private static final Sound sound = new Sound();
    private BufferedImage image; // ảnh riêng của viên này

    public ExplosiveBrick(int x, int y, int width, int height) {
        super(x, y, width, height, 1);

        if (!loaded) loadTextures();

        // 🔹 random 1 ảnh trong danh sách
        if (textures != null && textures.length > 0) {
            int idx = new Random().nextInt(textures.length);
            image = textures[idx];
        }
    }

    private void loadTextures() {
        try {
            textures = new BufferedImage[]{
                    ImageIO.read(getClass().getResource("/img/explosive1.png")),
                    ImageIO.read(getClass().getResource("/img/explosive2.png"))
            };
            loaded = true;
            System.out.println("✅ ExplosiveBrick textures loaded!");
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("⚠️ Không thể tải ảnh ExplosiveBrick, dùng màu mặc định.");
            textures = null;
        }
    }

    @Override
    public void takeHit() {
        hitPoints = 0; // 💥 nổ ngay lập tức
        sound.play(4); // âm thanh nổ
    }

    @Override
    public void render(Graphics2D g) {
        if (!isDestroyed()) {
            if (image != null) {
                g.drawImage(image, x, y, width, height, null);
            } else {
                g.setColor(Color.RED);
                g.fillRect(x, y, width, height);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, width, height);
            }
        }
    }
}
