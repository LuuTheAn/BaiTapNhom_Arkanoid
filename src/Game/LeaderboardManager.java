package Game;

import java.io.*;
import java.util.*;

public class LeaderboardManager {
    private static final String FILE_PATH = "leaderboard.dat";
    private static final int MAX_SCORES = 10;
    private final List<Integer> scores = new ArrayList<>();

    public LeaderboardManager() {
        loadScores();
    }

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

    private void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(scores);
            System.out.println("✅ Leaderboard saved (" + scores.size() + " scores)");
        } catch (IOException e) {
            System.err.println("⚠️ Lỗi khi lưu leaderboard: " + e.getMessage());
        }
    }

    public void addScore(int newScore) {
        scores.add(newScore);
        scores.sort(Collections.reverseOrder());
        if (scores.size() > MAX_SCORES)
            scores.subList(MAX_SCORES, scores.size()).clear();
        saveScores(); // 🔥 đảm bảo luôn ghi ra file
    }

    public List<Integer> getScores() {
        return new ArrayList<>(scores);
    }

    public void clearScores() {
        scores.clear();
        saveScores();
    }

    public void printLeaderboard() {
        System.out.println("===== 🏆 LEADERBOARD =====");
        for (int i = 0; i < scores.size(); i++) {
            System.out.println((i + 1) + ". " + scores.get(i));
        }
    }
}
