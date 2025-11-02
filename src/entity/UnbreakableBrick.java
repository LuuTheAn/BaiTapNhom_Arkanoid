package entity;

import sound.Sound;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class UnbreakableBrick extends Brick {
    private static BufferedImage[] textures; // ✅ Danh sách ảnh có thể dùng
    private static boolean loaded = false;
    private static final Sound sound = Sound.getInstance();
    private BufferedImage image; // Ảnh riêng cho viên này

    public UnbreakableBrick(int x, int y, int width, int height) {
        super(x, y, width, height, Integer.MAX_VALUE);

        // 🔹 Load ảnh chỉ 1 lần cho toàn bộ game
        if (!loaded) loadTextures();

        // 🔹 Chọn ngẫu nhiên 1 ảnh
        if (textures != null && textures.length > 0) {
            int idx = new Random().nextInt(textures.length);
            image = textures[idx];
        }
    }

    private void loadTextures() {
        try {
            textures = new BufferedImage[]{
                    ImageIO.read(getClass().getResource("/img/unbreakable1.png")),
                    ImageIO.read(getClass().getResource("/img/unbreakable2.png"))
            };
            loaded = true;
            System.out.println("✅ UnbreakableBrick textures loaded!");
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("⚠️ Không thể tải ảnh UnbreakableBrick, dùng màu mặc định.");
            textures = null;
        }
    }

    @Override
    public void takeHit() {
        // 🔊 Phát âm thanh "cứng" khi bị đánh trúng
        sound.play(3);
        System.out.println("🧱 Unbreakable brick hit! No damage taken.");
    }

    @Override
    public boolean isDestroyed() {
        return false; // Không bao giờ vỡ
    }

    @Override
    public void render(Graphics2D g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x, y, width, height);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, height);
        }
    }
}
