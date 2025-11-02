package entity;

import sound.Sound;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class NormalBrick extends Brick {
    private static BufferedImage[] textures; // ✅ nhiều ảnh khác nhau
    private static boolean loaded = false;
    private BufferedImage image;
    private static final Sound sound = Sound.getInstance(); // ✅ chỉ load 1 lần âm thanh

    public NormalBrick(int x, int y, int width, int height) {
        super(x, y, width, height, 1);

        // 🔹 Load ảnh một lần duy nhất
        if (!loaded) loadTextures();

        // 🔹 Gán ảnh ngẫu nhiên cho mỗi viên gạch
        if (textures != null && textures.length > 0) {
            image = textures[new Random().nextInt(textures.length)];
        }
    }

    private void loadTextures() {
        try {
            textures = new BufferedImage[]{
                    ImageIO.read(getClass().getResource("/img/normal1.png")),
                    ImageIO.read(getClass().getResource("/img/normal2.png")),
                    ImageIO.read(getClass().getResource("/img/normal3.png")),
                    ImageIO.read(getClass().getResource("/img/normal4.png")),
                    ImageIO.read(getClass().getResource("/img/normal5.png"))
            };
            loaded = true;
            System.out.println("✅ NormalBrick textures loaded!");
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("⚠️ Không thể tải ảnh NormalBrick, dùng màu mặc định.");
            textures = null;
        }
    }

    @Override
    public void takeHit() {
        super.takeHit(); // ✅ giảm máu (hitPoints--)
        sound.play(2);   // 🔊 phát âm thanh vỡ gạch (ID 2 = break.wav)
    }

    @Override
    public void render(Graphics2D g) {
        if (!isDestroyed()) {
            if (image != null) {
                g.drawImage(image, x, y, width, height, null);
            } else {
                g.setColor(Color.ORANGE);
                g.fillRect(x, y, width, height);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, width, height);
            }
        }
    }
}
