package entity;

import sound.Sound;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class StrongBrick extends Brick {
    private static BufferedImage fullHpImage;
    private static BufferedImage halfHpImage;
    private static Sound sound = new Sound(); // 🔊 Hệ thống âm thanh dùng chung

    public StrongBrick(int x, int y, int width, int height) {
        super(x, y, width, height, 2);

        try {
            if (fullHpImage == null)
                fullHpImage = ImageIO.read(getClass().getResource("/img/strong.png"));
            if (halfHpImage == null)
                halfHpImage = ImageIO.read(getClass().getResource("/img/strong_half.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void takeHit() {
        // 🔹 Phát âm thanh theo số máu còn lại
        if (hitPoints == 2) {
            sound.play(7); // âm "nứt" nhẹ (ví dụ unbreakable.wav)
        } else if (hitPoints == 1) {
            sound.play(8); // âm "vỡ" giống NormalBrick
        }

        super.takeHit(); // giảm máu
    }

    @Override
    public void render(Graphics2D g) {
        if (!isDestroyed()) {
            BufferedImage img = (hitPoints == 2 ? fullHpImage : halfHpImage);
            if (img != null) {
                g.drawImage(img, x, y, width, height, null);
            } else {
                g.setColor(hitPoints == 2 ? Color.GREEN : Color.YELLOW);
                g.fillRect(x, y, width, height);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, width, height);
            }
        }
    }
}