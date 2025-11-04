package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Lớp tiện ích {@code BrickFactory} chịu trách nhiệm tạo danh sách các viên gạch
 * cho từng cấp độ (level) trong trò chơi Arkanoid.
 * <p>
 * Cung cấp nhiều kiểu sắp xếp gạch khác nhau theo {@code level}, hoặc tạo ngẫu nhiên
 * theo tỉ lệ loại gạch:
 * <ul>
 *     <li>60% {@link NormalBrick}</li>
 *     <li>30% {@link StrongBrick}</li>
 *     <li>9.9% {@link ExplosiveBrick}</li>
 *     <li>0.1% {@link UnbreakableBrick}</li>
 * </ul>
 *
 * <p>Các phương thức chính:</p>
 * <ul>
 *     <li>{@link #createDefaultBricks()} – tạo level mặc định</li>
 *     <li>{@link #createLevel(int)} – tạo bricks theo cấp độ</li>
 * </ul>
 *
 * @author Lưu
 * @version 1.0
 */
public class BrickFactory {

    /** Đối tượng {@link Random} dùng để sinh xác suất ngẫu nhiên khi tạo gạch. */
    private static final Random random = new Random();

    /**
     * Tạo danh sách gạch mặc định cho trò chơi (mặc định là Level 1).
     *
     * @return Danh sách {@link Brick} cho Level 1
     */
    public static List<Brick> createDefaultBricks() {
        return createLevel(1);
    }

    /**
     * Tạo ngẫu nhiên một viên gạch theo tỉ lệ:
     * <ul>
     *     <li>60% NormalBrick</li>
     *     <li>30% StrongBrick</li>
     *     <li>9.9% ExplosiveBrick</li>
     *     <li>0.1% UnbreakableBrick</li>
     * </ul>
     *
     * @param x      Tọa độ X của gạch
     * @param y      Tọa độ Y của gạch
     * @param width  Chiều rộng của gạch
     * @param height Chiều cao của gạch
     * @return Một đối tượng {@link Brick} được chọn ngẫu nhiên theo tỉ lệ
     */
    private static Brick createRandomBrick(int x, int y, int width, int height) {
        double chance = random.nextDouble() * 100;

        if (chance < 60.0) {
            return new NormalBrick(x, y, width, height);
        } else if (chance < 90.0) {
            return new StrongBrick(x, y, width, height);
        } else if (chance < 99.9) {
            return new ExplosiveBrick(x, y, width, height);
        } else {
            return new UnbreakableBrick(x, y, width, height);
        }
    }

    /**
     * Tạo danh sách các viên gạch theo cấp độ trò chơi.
     * <p>
     * Mỗi {@code level} có bố cục gạch riêng biệt:
     * <ul>
     *     <li>Level 1 – Gạch cơ bản, xen kẽ loại</li>
     *     <li>Level 2 – Hình tam giác (kim tự tháp)</li>
     *     <li>Level 3 – Khung viền hình chữ nhật rỗng</li>
     *     <li>Level 4 – Ngẫu nhiên theo tỉ lệ</li>
     *     <li>Level 5 – Dạng zigzag kết hợp ngẫu nhiên</li>
     *     <li>Khác – Ngẫu nhiên toàn bộ</li>
     * </ul>
     *
     * @param level Cấp độ trò chơi cần tạo
     * @return Danh sách {@link Brick} tương ứng với cấp độ
     */
    public static List<Brick> createLevel(int level) {
        List<Brick> bricks = new ArrayList<>();
        int brickWidth = 40;
        int brickHeight = 20;
        int startY = 50;

        switch (level) {

            // 🧱 LEVEL 1 – Cơ bản, xen kẽ các loại gạch
            case 1 -> {
                for (int row = 0; row < 7; row++) {
                    for (int col = 0; col < 20; col++) {
                        int x = col * brickWidth;
                        int y = startY + row * brickHeight;

                        if (row == 0) {
                            bricks.add(new UnbreakableBrick(x, y, brickWidth, brickHeight));
                        } else if (row == 2 || row == 4) {
                            bricks.add(new StrongBrick(x, y, brickWidth, brickHeight));
                        } else if (row == 3) {
                            bricks.add(new ExplosiveBrick(x, y, brickWidth, brickHeight));
                        } else {
                            bricks.add(new NormalBrick(x, y, brickWidth, brickHeight));
                        }
                    }
                }
            }

            // 🔥 LEVEL 2 – Hình tam giác (kim tự tháp)
            case 2 -> {
                int rows = 10;
                for (int row = 0; row < rows; row++) {
                    int bricksInRow = rows - row;
                    int offsetX = (20 - bricksInRow) * (brickWidth / 2);

                    for (int col = 0; col < bricksInRow; col++) {
                        int x = offsetX + col * brickWidth;
                        int y = startY + row * brickHeight;

                        if (row % 3 == 0)
                            bricks.add(new StrongBrick(x, y, brickWidth, brickHeight));
                        else if (row % 4 == 0)
                            bricks.add(new ExplosiveBrick(x, y, brickWidth, brickHeight));
                        else
                            bricks.add(new NormalBrick(x, y, brickWidth, brickHeight));
                    }
                }
            }

            // 💣 LEVEL 3 – Hình chữ nhật rỗng (viền ngoài)
            case 3 -> {
                int rows = 10;
                int cols = 20;
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        int x = col * brickWidth;
                        int y = startY + row * brickHeight;

                        boolean isBorder = (row == 0 || col == 0 || col == cols - 1);
                        if (isBorder) {
                            bricks.add(new UnbreakableBrick(x, y, brickWidth, brickHeight));
                        } else if ((row + col) % 4 == 0) {
                            bricks.add(new ExplosiveBrick(x, y, brickWidth, brickHeight));
                        } else {
                            bricks.add(new NormalBrick(x, y, brickWidth, brickHeight));
                        }
                    }
                }
            }

            // 🌈 LEVEL 4 – Random theo tỉ lệ
            case 4 -> {
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 20; col++) {
                        int x = col * brickWidth;
                        int y = startY + row * brickHeight;
                        bricks.add(createRandomBrick(x, y, brickWidth, brickHeight));
                    }
                }
            }

            // 🔹 LEVEL 5 – Zigzag + random
            case 5 -> {
                for (int row = 0; row < 8; row++) {
                    int offsetX = (row % 2 == 0) ? 0 : brickWidth / 2;
                    for (int col = 0; col < 20; col++) {
                        int x = offsetX + col * brickWidth;
                        int y = startY + row * brickHeight;
                        bricks.add(createRandomBrick(x, y, brickWidth, brickHeight));
                    }
                }
            }

            // 🔸 LEVEL khác → random toàn bộ
            default -> {
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 20; col++) {
                        int x = col * brickWidth;
                        int y = startY + row * brickHeight;
                        bricks.add(createRandomBrick(x, y, brickWidth, brickHeight));
                    }
                }
            }
        }

        return bricks;
    }
}
