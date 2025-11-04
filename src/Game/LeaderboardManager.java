package Game;

import java.io.*;
import java.util.*;

/**
 * {@code LeaderboardManager} quản lý bảng xếp hạng (leaderboard) của trò chơi.
 * <p>
 * Lớp này chịu trách nhiệm:
 * <ul>
 *   <li>Lưu và tải danh sách điểm cao nhất từ tệp {@code leaderboard.dat}</li>
 *   <li>Thêm điểm mới và tự động sắp xếp theo thứ tự giảm dần</li>
 *   <li>Giới hạn tối đa số điểm được lưu là {@value #MAX_SCORES}</li>
 *   <li>Cung cấp phương thức để in, xóa hoặc lấy danh sách điểm</li>
 * </ul>
 * <p>
 * Dữ liệu được lưu bằng cơ chế tuần tự hóa đối tượng ({@link ObjectOutputStream}).
 */
public class LeaderboardManager {

    /** Đường dẫn file lưu bảng điểm. */
    private static final String FILE_PATH = "leaderboard.dat";

    /** Số lượng điểm tối đa được lưu trong bảng. */
    private static final int MAX_SCORES = 10;

    /** Danh sách điểm hiện tại trong bộ nhớ. */
    private final List<Integer> scores = new ArrayList<>();

    /**
     * Khởi tạo một đối tượng {@code LeaderboardManager} và tự động tải dữ liệu từ file.
     */
    public LeaderboardManager() {
        loadScores();
    }

    /**
     * Tải danh sách điểm từ file {@link #FILE_PATH}.
     * <p>
     * Nếu file không tồn tại hoặc lỗi khi đọc, danh sách sẽ để trống.
     */
    @SuppressWarnings("unchecked")
    private void loadScores() {
        scores.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                List<?> loaded = (List<?>) obj;
                for (Object o : loaded) {
                    if (o instanceof Integer) scores.add((Integer) o);
                }
            }
            System.out.println("📂 Loaded leaderboard (" + scores.size() + " entries)");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("⚠️ Lỗi khi tải leaderboard: " + e.getMessage());
        }
    }

    /**
     * Lưu danh sách điểm hiện tại ra file {@link #FILE_PATH}.
     */
    private void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(scores);
            System.out.println("✅ Leaderboard saved (" + scores.size() + " scores)");
        } catch (IOException e) {
            System.err.println("⚠️ Lỗi khi lưu leaderboard: " + e.getMessage());
        }
    }

    /**
     * Thêm một điểm mới vào bảng xếp hạng.
     * <p>
     * Sau khi thêm, danh sách sẽ được sắp xếp giảm dần
     * và chỉ giữ lại {@value #MAX_SCORES} điểm cao nhất.
     *
     * @param newScore điểm mới đạt được
     */
    public void addScore(int newScore) {
        scores.add(newScore);
        scores.sort(Collections.reverseOrder());
        if (scores.size() > MAX_SCORES)
            scores.subList(MAX_SCORES, scores.size()).clear();
        saveScores(); // 🔥 luôn ghi ra file để đảm bảo cập nhật
    }

    /**
     * Lấy danh sách điểm hiện tại (bản sao, không ảnh hưởng đến dữ liệu gốc).
     *
     * @return danh sách điểm cao, được sắp xếp giảm dần
     */
    public List<Integer> getScores() {
        return new ArrayList<>(scores);
    }

    /**
     * Xóa toàn bộ điểm trong bảng xếp hạng và cập nhật file.
     */
    public void clearScores() {
        scores.clear();
        saveScores();
    }

    /**
     * In bảng xếp hạng ra console.
     * <p>
     * Dạng hiển thị ví dụ:
     * <pre>
     * ===== 🏆 LEADERBOARD =====
     * 1. 5000
     * 2. 4300
     * 3. 3800
     * </pre>
     */
    public void printLeaderboard() {
        System.out.println("===== 🏆 LEADERBOARD =====");
        for (int i = 0; i < scores.size(); i++) {
            System.out.println((i + 1) + ". " + scores.get(i));
        }
    }
}
