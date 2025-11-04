package Powerup;

import entity.*;
import sound.Sound;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp {@code PowerUpManager} chịu trách nhiệm tạo, cập nhật và vẽ
 * tất cả các đối tượng {@link PowerUp} đang hoạt động trong trò chơi.
 * <p>
 * Lớp này xử lý:
 * <ul>
 * <li>Tạo ra các vật phẩm mới khi gạch bị phá hủy</li>
 * <li>Kiểm tra va chạm giữa {@link Paddle} (thanh đỡ) và các vật phẩm</li>
 * <li>Kích hoạt các hiệu ứng tạm thời (ví dụ: mở rộng thanh đỡ, tăng tốc bóng)</li>
 * <li>Theo dõi thời lượng của mỗi hiệu ứng vật phẩm đang hoạt động</li>
 * </ul>
 * </p>
 *
 * <p>
 * Ví dụ sử dụng:
 * <pre>
 * PowerUpManager powerUpManager = new PowerUpManager();
 * powerUpManager.spawnPowerUp(brick); // Tạo vật phẩm tại vị trí viên gạch
 * powerUpManager.update(ball, paddle, screenHeight); // Cập nhật logic
 * powerUpManager.render(graphics2D); // Vẽ vật phẩm
 * </pre>
 * </p>
 *
 * @see Powerup.PowerUp
 * @see Powerup.ExpandPaddlePowerUp
 * @see Powerup.FastBallPowerUp
 * @see entity.Ball
 * @see entity.Paddle
 */
public class PowerUpManager {

    /** Danh sách các vật phẩm hiện đang hoạt động (đang rơi). */
    private final List<PowerUp> activePowerUps = new ArrayList<>();

    /** Thể hiện (instance) Sound dùng chung để phát âm thanh vật phẩm. */
    private static final Sound sound = Sound.getInstance();

    /** Mốc thời gian (ms) khi hiệu ứng mở rộng thanh đỡ kết thúc. */
    private long expandEndTime = 0;

    /** Mốc thời gian (ms) khi hiệu ứng bóng nhanh kết thúc. */
    private long fastBallEndTime = 0;

    /**
     * Tạo ngẫu nhiên một vật phẩm tại vị trí của viên gạch bị phá hủy.
     * <p>
     * Chỉ một số viên gạch nhất định mới tạo ra vật phẩm dựa trên xác suất:
     * <ul>
     * <li>Xác suất {@code < 0.15} → {@link ExpandPaddlePowerUp} (Mở rộng thanh đỡ)</li>
     * <li>Xác suất {@code < 0.25} → {@link FastBallPowerUp} (Bóng nhanh)</li>
     * </ul>
     * </p>
     *
     * @param brick đối tượng {@link Brick} vừa bị phá hủy
     */
    public void spawnPowerUp(Brick brick) {
        double chance = Math.random();

        // Lấy vị trí trung tâm của viên gạch
        int x = brick.getX() + brick.getWidth() / 2;
        int y = brick.getY() + brick.getHeight() / 2;

        if (chance < 0.15) {
            activePowerUps.add(new ExpandPaddlePowerUp(x, y));
        } else if (chance < 0.25) {
            activePowerUps.add(new FastBallPowerUp(x, y));
        }
        // Bạn có thể thêm các hiệu ứng khác sau này, ví dụ: new ShieldPowerUp(x, y);
    }

    /**
     * Cập nhật tất cả các vật phẩm đang hoạt động và áp dụng hiệu ứng khi được thu thập.
     * <p>
     * Phương thức này thực hiện:
     * <ul>
     * <li>Di chuyển từng vật phẩm đang hoạt động xuống dưới</li>
     * <li>Phát hiện va chạm với {@link Paddle} (thanh đỡ)</li>
     * <li>Áp dụng hiệu ứng của vật phẩm và phát âm thanh tương ứng</li>
     * <li>Xử lý thời gian và việc hết hạn của các hiệu ứng tạm thời</li>
     * </ul>
     * </p>
     *
     * @param ball          đối tượng {@link Ball} bị ảnh hưởng bởi một số vật phẩm (ví dụ: bóng nhanh)
     * @param paddle        đối tượng {@link Paddle} do người chơi điều khiển
     * @param screenHeight  chiều cao của màn hình, dùng để loại bỏ các vật phẩm rơi ra ngoài màn hình
     */
    public void update(Ball ball, Paddle paddle, int screenHeight) {
        List<PowerUp> collected = new ArrayList<>();

        for (PowerUp p : activePowerUps) {
            p.update(); // Cập nhật vị trí (rơi xuống)

            // Khi thanh đỡ thu thập vật phẩm
            if (p.getBounds().intersects(paddle.getBounds())) {
                p.applyEffect(paddle);

                // --- Hiệu ứng Bóng nhanh ---
                if (p instanceof FastBallPowerUp) {
                    sound.play(Sound.FX_FAST); // Sử dụng hằng số nếu có
                    // Nếu hiệu ứng đang hoạt động, làm mới (reset) thời gian
                    if (ball.isFastBallActive()) {
                        fastBallEndTime = System.currentTimeMillis() + 5000; // 5 giây
                    } else {
                        // Kích hoạt lần đầu
                        ball.activateFastBall();
                        fastBallEndTime = System.currentTimeMillis() + 5000;
                    }
                }

                // --- Hiệu ứng Mở rộng thanh đỡ ---
                if (p instanceof ExpandPaddlePowerUp) {
                    sound.play(Sound.FX_EXPAND); // Sử dụng hằng số nếu có
                    // Nếu đã mở rộng, làm mới thời gian
                    if (paddle.isExpanded()) {
                        expandEndTime = System.currentTimeMillis() + 5000;
                    } else {
                        // Kích hoạt lần đầu
                        p.applyEffect(paddle); // (applyEffect đã được gọi ở trên, nhưng gọi lại để chắc chắn)
                        expandEndTime = System.currentTimeMillis() + 5000;
                    }
                }

                collected.add(p); // Thêm vào danh sách chờ xóa
            }
        }

        // Loại bỏ các vật phẩm đã thu thập
        activePowerUps.removeAll(collected);
        // Loại bỏ các vật phẩm rơi ra khỏi màn hình
        activePowerUps.removeIf(p -> p.getY() > screenHeight);

        // --- Kiểm tra hiệu ứng Mở rộng đã hết hạn ---
        if (expandEndTime > 0 && System.currentTimeMillis() > expandEndTime) {
            paddle.setWidth(80);          // Khôi phục kích thước gốc
            paddle.setExpanded(false);
            expandEndTime = 0;            // Đặt lại đồng hồ đếm
        }

        // --- Kiểm tra hiệu ứng Bóng nhanh đã hết hạn ---
        if (fastBallEndTime > 0 && System.currentTimeMillis() > fastBallEndTime) {
            ball.resetSpeed();            // Khôi phục tốc độ gốc
            fastBallEndTime = 0;          // Đặt lại đồng hồ đếm
        }
    }

    /**
     * Vẽ (render) tất cả các vật phẩm đang hoạt động lên màn hình.
     *
     * @param g đối tượng {@link Graphics2D} dùng để vẽ
     */
    public void render(Graphics2D g) {
        for (PowerUp p : activePowerUps) {
            p.render(g);
        }
    }
}