package entity;

import sound.Sound;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

// 1. SỬA IMPORT: Đổi 'GamePanel' thành 'GameManager'
import Game.GameManager;

public class ExplosiveBrick extends Brick {
    private static BufferedImage[] textures;
    private static boolean loaded = false;

    private static final Sound sound = new Sound();
    private BufferedImage image;

    public ExplosiveBrick(int x, int y, int width, int height) {
        super(x, y, width, height, 1);
        if (!loaded) loadTextures();
        if (textures != null && textures.length > 0) {
            int idx = new Random().nextInt(textures.length);
            image = textures[idx];
        }
    }

    private void loadTextures() {
        // ... (code tải ảnh của bạn giữ nguyên) ...
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
        hitPoints = 0;
        sound.play(4);
    }

    @Override
    public void render(Graphics2D g) {
        // ... (code render của bạn giữ nguyên) ...
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

    // 2. SỬA CHỮ 'GamePanel' thành 'GameManager'
    public void explode(List<Brick> allBricks, GameManager gameManager) {
        int explosionRange = 1;
        int bw = this.getWidth();
        int bh = this.getHeight();
        List<Brick> toDestroy = new ArrayList<>();

        for (Brick b : allBricks) {
            if (b.isDestroyed() || b instanceof UnbreakableBrick) continue;

            int dx = Math.abs(b.getX() - this.getX()) / bw;
            int dy = Math.abs(b.getY() - this.getY()) / bh;

            if (dx <= explosionRange && dy <= explosionRange) {
                toDestroy.add(b);
            }
        }

        for (Brick b : toDestroy) {
            if (b.isDestroyed()) continue;

            b.takeHit();

            // 3. SỬA LUÔN Ở ĐÂY
            if (b instanceof ExplosiveBrick && b != this) {
                ((ExplosiveBrick) b).explode(allBricks, gameManager);
            }

            // 4. VÀ Ở ĐÂY
            if (b.isDestroyed() && !(b instanceof UnbreakableBrick)) {
                gameManager.addScore(10);
            }
        }
    }
}