package entity;

import sound.Sound;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Lớp {@code StrongBrick} đại diện cho loại gạch có độ bền cao trong trò chơi Arkanoid.
 * <p>
 * Gạch này cần bị đánh trúng hai lần mới vỡ hoàn toàn.
 * Mỗi lần bị đánh trúng, hình ảnh và âm thanh sẽ thay đổi để thể hiện mức độ hư hại.
 * </p>
 *
 * <p><b>Đặc điểm:</b></p>
 * <ul>
 *   <li>Máu ban đầu ({@code hitPoints}) = 2</li>
 *   <li>Phát âm thanh khác nhau khi bị nứt và khi vỡ</li>
 *   <li>Hiển thị bằng 2 hình ảnh: {@code strong.png} và {@code strong_half.png}</li>
 * </ul>
 *
 * @see Brick
 * @see NormalBrick
 * @see ExplosiveBrick
 * @author An
 * @version 1.0
 */
public class StrongBrick extends Brick {

    /** Hình ảnh khi gạch còn nguyên vẹn (2 máu) */
    private static BufferedImage fullHpImage;

    /** Hình ảnh khi gạch bị nứt (1 máu) */
    private static BufferedImage halfHpImage;

    /** Hệ thống âm thanh dùng chung */
    private static final Sound sound = Sound.getInstance();

    /**
     * Khởi tạo một {@code StrongBrick} tại vị trí, kích thước được chỉ định.
     * <p>
     * Nếu hình ảnh chưa được tải, phương thức sẽ tải chúng từ thư mục <code>/img</code>.
     * </p>
     *
     * @param x hoành độ của gạch
     * @param y tung độ của gạch
     * @param width chiều rộng gạch
     * @param height chiều cao gạch
     */
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

    /**
     * Gọi khi gạch bị bóng đập trúng.
     * <p>
     * Giảm {@code hitPoints} đi 1 và phát âm thanh tương ứng với mức độ hư hại.
     * </p>
     */
    @Override
    public void takeHit() {
        if (hitPoints == 2) {
            sound.play(7); // âm "nứt" nhẹ
        } else if (hitPoints == 1) {
            sound.play(8); // âm "vỡ"
        }
        super.takeHit();
    }

    /**
     * Hiển thị gạch lên màn hình.
     * <p>
     * - Nếu còn 2 máu: vẽ ảnh {@code strong.png} (hoặc màu xanh lá). <br>
     * - Nếu còn 1 máu: vẽ ảnh {@code strong_half.png} (hoặc màu vàng). <br>
     * - Nếu bị phá hủy: không vẽ gì.
     * </p>
     *
     * @param g đối tượng {@link Graphics2D} dùng để vẽ
     */
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
