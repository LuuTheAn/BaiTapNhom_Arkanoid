package Game;

public class ProgressManager {
    private static int unlockedLevel = 1; // ✅ mặc định chỉ mở Level 1

    public static int getUnlockedLevel() {
        return unlockedLevel;
    }

    public static void unlockNextLevel(int currentLevel) {
        if (currentLevel >= unlockedLevel && unlockedLevel < 5) {
            unlockedLevel = currentLevel + 1;
            System.out.println("🎉 Level " + unlockedLevel + " đã được mở khóa!");
        }
    }
}
