package entity;

import sound.Sound;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * Lớp {@code UnbreakableBrick} đại diện cho loại gạch không thể phá hủy trong trò chơi Arkanoid.
 * <p>
 * Khi bị bóng va chạm, gạch này không mất máu và không biến mất.
 * Nó chỉ phát ra âm thanh "cứng" và giữ nguyên trạng thái.
 * </p>
 *
 * <p><b>Đặc điểm:</b></p>
 * <ul>
 *   <li>Máu ({@code hitPoints}) = {@link Integer#MAX_VALUE}</li>
 *   <li>Không bao giờ bị phá hủy ({@link #isDestroyed()} luôn trả về {@code false})</li>
 *   <li>Phát âm thanh riêng khi bị đánh trúng</li>
 *   <li>Hiển thị ảnh ngẫu nhiên từ bộ {@code unbreakable1.png}, {@code unbreakable2.png}</li>
 * </ul>
 *
 * @see Brick
 * @see StrongBrick
 * @see NormalBrick
 * @see ExplosiveBrick
 * @author An
 * @version 1.0
 */
public class UnbreakableBrick extends Brick {

    /** Danh sách ảnh texture cho loại gạch này */
    private static BufferedImage[] textures;

    /** Cờ đánh dấu đã load ảnh hay chưa (chỉ load một lần duy nhất) */
    private static boolean loaded = false;

    /** Âm thanh dùng chung trong game */
    private static final Sound sound = Sound.getInstance();

    /** Ảnh được chọn ngẫu nhiên cho từng viên gạch */
    private BufferedImage image;

    /**
     * Khởi tạo một {@code UnbreakableBrick} tại vị trí, kích thước xác định.
     * <p>
     * Mỗi viên gạch sẽ được gán ngẫu nhiên một ảnh texture trong bộ ảnh đã tải.
     * </p>
     *
     * @param x hoành độ của gạch
     * @param y tung độ của gạch
     * @param width chiều rộng gạch
     * @param height chiều cao gạch
     */
    public UnbreakableBrick(int x, int y, int width, int height) {
        super(x, y, width, height, Integer.MAX_VALUE);

        if (!loaded) loadTextures();

        if (textures != null && textures.length > 0) {
            int idx = new Random().nextInt(textures.length);
            image = textures[idx];
        }
    }

    /**
     * Tải toàn bộ ảnh texture của {@code UnbreakableBrick}.
     * <p>
     * Nếu không thể tải ảnh, sẽ chuyển sang chế độ hiển thị bằng màu mặc định.
     * </p>
     */
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

    /**
     * Gọi khi viên gạch bị bóng va chạm.
     * <p>
     * Gạch này không mất máu, chỉ phát âm thanh phản hồi để báo hiệu va chạm.
     * </p>
     */
    @Override
    public void takeHit() {
        sound.play(3);
        System.out.println("🧱 Unbreakable brick hit! No damage taken.");
    }

    /**
     * Kiểm tra gạch có bị phá hủy hay không.
     * <p>
     * Gạch không thể phá hủy luôn trả về {@code false}.
     * </p>
     *
     * @return luôn là {@code false}
     */
    @Override
    public boolean isDestroyed() {
        return false;
    }

    /**
     * Hiển thị viên gạch lên màn hình.
     * <p>
     * - Nếu ảnh đã tải: vẽ ảnh ngẫu nhiên. <br>
     * - Nếu lỗi ảnh: vẽ màu xám đậm làm mặc định.
     * </p>
     *
     * @param g đối tượng {@link Graphics2D} dùng để vẽ
     */
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
