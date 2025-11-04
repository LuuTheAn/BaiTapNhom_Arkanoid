package entity;

import sound.Sound;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * Lớp {@code NormalBrick} đại diện cho loại gạch thông thường trong game Arkanoid.
 * <p>
 * Gạch thường bị phá hủy sau một cú đánh duy nhất, có nhiều texture khác nhau
 * để tạo hiệu ứng ngẫu nhiên, và phát ra âm thanh khi bị vỡ.
 * </p>
 *
 * <p><b>Đặc điểm:</b></p>
 * <ul>
 *   <li>Chịu 1 lần va chạm (hitPoints = 1)</li>
 *   <li>Có 5 loại texture khác nhau để chọn ngẫu nhiên</li>
 *   <li>Phát âm thanh khi bị phá</li>
 * </ul>
 *
 * @see Brick
 * @see StrongBrick
 * @see ExplosiveBrick
 * @author An
 * @version 1.0
 */
public class NormalBrick extends Brick {

    /** Danh sách texture của gạch thường (được load một lần duy nhất) */
    private static BufferedImage[] textures;

    /** Biến cờ kiểm tra xem texture đã được load hay chưa */
    private static boolean loaded = false;

    /** Ảnh hiển thị cụ thể của viên gạch này */
    private BufferedImage image;

    /** Đối tượng âm thanh dùng chung trong toàn bộ game */
    private static final Sound sound = Sound.getInstance();

    /**
     * Khởi tạo một viên {@code NormalBrick} tại vị trí và kích thước chỉ định.
     *
     * @param x hoành độ của viên gạch
     * @param y tung độ của viên gạch
     * @param width chiều rộng
     * @param height chiều cao
     */
    public NormalBrick(int x, int y, int width, int height) {
        super(x, y, width, height, 1);

        // 🔹 Load ảnh một lần duy nhất
        if (!loaded) loadTextures();

        // 🔹 Gán ảnh ngẫu nhiên cho mỗi viên gạch
        if (textures != null && textures.length > 0) {
            image = textures[new Random().nextInt(textures.length)];
        }
    }

    /**
     * Nạp các texture của gạch thường từ thư mục tài nguyên.
     * <p>Nếu việc tải ảnh thất bại, hệ thống sẽ dùng màu mặc định.</p>
     */
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

    /**
     * Giảm điểm máu của viên gạch khi bị đánh trúng.
     * <p>Sau khi bị phá, gạch sẽ phát âm thanh hiệu ứng.</p>
     */
    @Override
    public void takeHit() {
        super.takeHit(); // giảm hitPoints
        sound.play(2);   // phát âm thanh vỡ gạch
    }

    /**
     * Vẽ viên gạch lên màn hình.
     * <p>Nếu ảnh chưa được tải, sẽ dùng hình chữ nhật màu cam làm mặc định.</p>
     *
     * @param g đối tượng {@link Graphics2D} dùng để vẽ
     */
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
