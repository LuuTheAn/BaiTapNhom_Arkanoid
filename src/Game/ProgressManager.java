package Game;

/**
 * Lớp {@code ProgressManager} quản lý tiến độ màn chơi của người chơi
 * trong trò chơi Arkanoid.
 * <p>
 * Nó theo dõi màn chơi nào hiện đã được mở khóa và cho phép mở khóa
 * màn chơi tiếp theo sau khi hoàn thành màn chơi hiện tại.
 * <p>
 * Lớp này sử dụng các trường (fields) và phương thức (methods) tĩnh (static)
 * để dữ liệu tiến độ có thể được chia sẻ toàn cục trên tất cả các màn hình trò chơi.
 */
public class ProgressManager {

    /**
     * Màn chơi cao nhất đã được mở khóa.
     * Giá trị mặc định là {@code 1}, có nghĩa là chỉ Màn 1
     * có sẵn khi bắt đầu.
     */
    private static int unlockedLevel = 1;

    /**
     * Trả về màn chơi cao nhất đã được mở khóa.
     *
     * @return số của màn chơi cao nhất đã được mở khóa
     */
    public static int getUnlockedLevel() {
        return unlockedLevel;
    }

    /**
     * Mở khóa màn chơi tiếp theo nếu màn chơi hiện tại đã hoàn thành
     * và chưa được mở khóa trước đó.
     * <p>
     * Phương thức này đảm bảo rằng cấp độ đã mở khóa không bao giờ vượt quá
     * tổng số màn chơi (trong trường hợp này là 5).
     *
     * @param currentLevel màn chơi hiện tại mà người chơi vừa hoàn thành
     */
    public static void unlockNextLevel(int currentLevel) {
        // Chỉ mở khóa level tiếp theo nếu người chơi vừa hoàn thành level cao nhất
        // và level cao nhất chưa phải là 5
        if (currentLevel >= unlockedLevel && unlockedLevel < 5) {
            unlockedLevel = currentLevel + 1;
            System.out.println("🎉 Level " + unlockedLevel + " đã được mở khóa!");
        }
    }
}