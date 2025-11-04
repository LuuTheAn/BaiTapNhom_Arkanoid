package entity;

import sound.Sound;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

// 🔹 Import GameManager (đã sửa đúng)
import Game.GameManager;

/**
 * Lớp {@code ExplosiveBrick} đại diện cho viên gạch nổ trong trò chơi.
 * Khi bị phá hủy, viên gạch này sẽ gây nổ và phá hủy các viên gạch xung quanh trong phạm vi nhất định.
 * Nó cũng có thể kích hoạt hiệu ứng nổ dây chuyền nếu các viên lân cận là ExplosiveBrick khác.
 *
 * <p>Ảnh đại diện được tải từ thư mục <code>/img/explosive1.png</code> và <code>/img/explosive2.png</code>.
 * Nếu không tải được, viên gạch sẽ hiển thị bằng màu đỏ.</p>
 *
 * @author An
 * @version 1.0
 */
public class ExplosiveBrick extends Brick {

    /** Bộ ảnh texture của gạch nổ */
    private static BufferedImage[] textures;

    /** Cờ đánh dấu đã tải ảnh chưa (để tránh load lại nhiều lần) */
    private static boolean loaded = false;

    /** Âm thanh được phát khi gạch nổ */
    private static final Sound sound = Sound.getInstance();

    /** Ảnh được chọn ngẫu nhiên cho viên gạch này */
    private BufferedImage image;

    /**
     * Khởi tạo một viên gạch nổ tại vị trí (x, y).
     *
     * @param x hoành độ của viên gạch
     * @param y tung độ của viên gạch
     * @param width chiều rộng của viên gạch
     * @param height chiều cao của viên gạch
     */
    public ExplosiveBrick(int x, int y, int width, int height) {
        super(x, y, width, height, 1);
        if (!loaded) loadTextures();
        if (textures != null && textures.length > 0) {
            int idx = new Random().nextInt(textures.length);
            image = textures[idx];
        }
    }

    /**
     * Tải ảnh texture cho gạch nổ (chỉ thực hiện một lần duy nhất).
     * Nếu không tải được, viên gạch sẽ hiển thị bằng màu mặc định (đỏ).
     */
    private static void loadTextures() {
        if (loaded) return;
        try {
            textures = new BufferedImage[]{
                    ImageIO.read(ExplosiveBrick.class.getResource("/img/explosive1.png")),
                    ImageIO.read(ExplosiveBrick.class.getResource("/img/explosive2.png"))
            };
            loaded = true;
            System.out.println("✅ ExplosiveBrick textures loaded!");
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("⚠️ Không thể tải ảnh ExplosiveBrick, dùng màu mặc định.");
            textures = null;
        }
    }

    /**
     * Gọi khi viên gạch bị đánh trúng.
     * Đặt máu về 0 và phát âm thanh nổ.
     */
    @Override
    public void takeHit() {
        if (isDestroyed()) return; // tránh xử lý lại
        hitPoints = 0;
        sound.play(4);
    }

    /**
     * Vẽ viên gạch lên màn hình.
     * Nếu ảnh texture có sẵn, sử dụng ảnh; nếu không, vẽ hình chữ nhật màu đỏ.
     *
     * @param g đối tượng {@link Graphics2D} để vẽ
     */
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

    /**
     * Gây nổ, phá hủy các viên gạch xung quanh trong phạm vi xác định.
     * Nếu gặp viên gạch nổ khác, sẽ gây nổ dây chuyền.
     *
     * @param allBricks danh sách tất cả các viên gạch trong màn chơi
     * @param gameManager đối tượng {@link GameManager} để cập nhật điểm
     */
    public void explode(List<Brick> allBricks, GameManager gameManager) {
        int explosionRange = 1; // phạm vi nổ (1 ô xung quanh)
        int bw = this.getWidth();
        int bh = this.getHeight();
        List<Brick> toDestroy = new ArrayList<>();

        // Tìm các viên nằm trong vùng nổ
        for (Brick b : allBricks) {
            if (b.isDestroyed() || b instanceof UnbreakableBrick) continue;

            int dx = Math.abs(b.getX() - this.getX()) / bw;
            int dy = Math.abs(b.getY() - this.getY()) / bh;

            if (dx <= explosionRange && dy <= explosionRange) {
                toDestroy.add(b);
            }
        }

        // Gây nổ và cộng điểm
        for (Brick b : toDestroy) {
            if (b.isDestroyed()) continue;
            b.takeHit();

            // Nổ dây chuyền
            if (b instanceof ExplosiveBrick && b != this) {
                ((ExplosiveBrick) b).explode(allBricks, gameManager);
            }

            // Cộng điểm nếu viên gạch bị phá
            if (b.isDestroyed() && !(b instanceof UnbreakableBrick)) {
                gameManager.addScore(10);
            }
        }
    }
}
