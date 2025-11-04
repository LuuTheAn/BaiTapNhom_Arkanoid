package sound;

import javax.sound.sampled.*;
import java.net.URL;

/**
 * Lớp Sound quản lý việc tải và phát tất cả các hiệu ứng âm thanh và nhạc nền.
 * <p>
 * Lớp này được triển khai theo mẫu Singleton để đảm bảo chỉ có một thể hiện duy nhất
 * quản lý tài nguyên âm thanh, tránh việc tải lại các file âm thanh nhiều lần.
 * <p>
 * Các chỉ số âm thanh (sound index) được định nghĩa sẵn:
 * <ul>
 * <li>0: Nhạc nền (nen.wav)</li>
 * <li>1: Tiếng click (click.wav)</li>
 * <li>2: Tiếng vỡ gạch (break.wav)</li>
 * <li>3: Tiếng gạch không thể vỡ (unbreakable.wav)</li>
 * <li>4: Tiếng nổ (exploding.wav)</li>
 * <li>5: Âm thanh thua (lose.wav)</li>
 * <li>6: Âm thanh thắng (win.wav)</li>
 * <li>7s: Vật phẩm "strong1" (strong1.wav)</li>
 * <li>8: Vật phẩm "strong2" (strong2.wav)</li>
 * <li>9: Vật phẩm "fast" (fast.wav)</li>
 * <li>10: Vật phẩm "expand" (expand.wav)</li>
 * <li>11: Tiếng bóng chạm paddle 2 (paddle2.wav)</li>
 * <li>12: Tiếng bóng văng ra ngoài (ballout.wav)</li>
 * <li>13: Âm thanh thắng cuối cùng (finalwin.wav)</li>
 * <li>14: Tạm dừng (pause.wav)</li>
 * </ul>
 *
 * @author [Tên của bạn]
 * @version 1.0
 */
public class Sound {
    /**
     * Thể hiện (instance) duy nhất của lớp Sound (Singleton).
     */
    private static Sound instance;

    /**
     * Mảng lưu trữ các đối tượng Clip đã được tải trước.
     */
    private final Clip[] clips = new Clip[15];

    // Các hằng số định danh cho âm thanh - giúp mã dễ đọc hơn
    public static final int MUSIC_BACKGROUND = 0;
    public static final int FX_CLICK = 1;
    public static final int FX_BREAK = 2;
    public static final int FX_UNBREAKABLE = 3;
    public static final int FX_EXPLODING = 4;
    public static final int MUSIC_LOSE = 5;
    public static final int MUSIC_WIN = 6;
    public static final int FX_STRONG1 = 7;
    public static final int FX_STRONG2 = 8;
    public static final int FX_FAST = 9;
    public static final int FX_EXPAND = 10;
    public static final int FX_PADDLE = 11;
    public static final int FX_BALL_OUT = 12;
    public static final int MUSIC_FINAL_WIN = 13;
    public static final int FX_PAUSE = 14;


    /**
     * Constructor private để thực thi mẫu Singleton.
     * Tải trước tất cả các tệp âm thanh từ thư mục resources/sound vào mảng clips.
     * <p>
     * Cũng thực hiện "làm nóng" (warm-up) mixer bằng cách phát và dừng ngay
     * clip âm thanh 'click' để giảm độ trễ khi phát âm thanh đầu tiên.
     */
    private Sound() {
        try {
            URL[] soundURLs = new URL[15];
            soundURLs[MUSIC_BACKGROUND] = getClass().getResource("/sound/nen.wav");
            soundURLs[FX_CLICK] = getClass().getResource("/sound/click.wav");
            soundURLs[FX_BREAK] = getClass().getResource("/sound/break.wav");
            soundURLs[FX_UNBREAKABLE] = getClass().getResource("/sound/unbreakable.wav");
            soundURLs[FX_EXPLODING] = getClass().getResource("/sound/exploding.wav");
            soundURLs[MUSIC_LOSE] = getClass().getResource("/sound/lose.wav");
            soundURLs[MUSIC_WIN] = getClass().getResource("/sound/win.wav");
            soundURLs[FX_STRONG1] = getClass().getResource("/sound/strong1.wav");
            soundURLs[FX_STRONG2] = getClass().getResource("/sound/strong2.wav");
            soundURLs[FX_FAST] = getClass().getResource("/sound/fast.wav");
            soundURLs[FX_EXPAND] = getClass().getResource("/sound/expand.wav");
            soundURLs[FX_PADDLE] = getClass().getResource("/sound/paddle2.wav");
            soundURLs[FX_BALL_OUT] = getClass().getResource("/sound/ballout.wav");
            soundURLs[MUSIC_FINAL_WIN] = getClass().getResource("/sound/finalwin.wav");
            soundURLs[FX_PAUSE] = getClass().getResource("/sound/pause.wav");

            // 🔹 Load toàn bộ âm thanh
            for (int i = 0; i < soundURLs.length; i++) {
                if (soundURLs[i] != null) {
                    AudioInputStream ais = AudioSystem.getAudioInputStream(soundURLs[i]);
                    clips[i] = AudioSystem.getClip();
                    clips[i].open(ais);
                }
            }

            // 🔹 Làm nóng mixer
            if (clips[FX_CLICK] != null) {
                clips[FX_CLICK].setFramePosition(0);
                clips[FX_CLICK].start();
                clips[FX_CLICK].stop();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cung cấp quyền truy cập vào thể hiện Singleton của lớp Sound.
     * Tạo một thể hiện mới nếu nó chưa tồn tại (thread-safe).
     *
     * @return Thể hiện (instance) duy nhất của lớp Sound.
     */
    public static synchronized Sound getInstance() {
        if (instance == null) {
            instance = new Sound();
        }
        return instance;
    }

    /**
     * Phát âm thanh tại một chỉ số cụ thể một lần.
     * Nếu clip đang chạy, nó sẽ bị dừng và bắt đầu lại từ đầu.
     *
     * @param i Chỉ số của clip âm thanh cần phát (sử dụng các hằng số, ví dụ: {@code Sound.FX_CLICK}).
     */
    public void play(int i) {
        if (clips[i] == null) return;
        clips[i].stop();
        clips[i].setFramePosition(0);
        clips[i].start();
    }

    /**
     * Phát một âm thanh và lặp lại liên tục (ví dụ: nhạc nền).
     *
     * @param i Chỉ số của clip âm thanh cần lặp (ví dụ: {@code Sound.MUSIC_BACKGROUND}).
     */
    public void loop(int i) {
        if (clips[i] == null) return;
        clips[i].loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * Dừng phát một âm thanh đang chạy.
     *
     * @param i Chỉ số của clip âm thanh cần dừng.
     */
    public void stop(int i) {
        if (clips[i] == null) return;
        clips[i].stop();
    }

    /**
     * Điều chỉnh âm lượng cho một clip cụ thể.
     *
     * @param i      Chỉ số của clip âm thanh cần điều chỉnh.
     * @param volume Mức âm lượng tuyến tính (linear), từ 0.0 (tắt tiếng) đến 1.0 (âm lượng đầy đủ).
     */
    public void setVolume(int i, float volume) {
        if (clips[i] == null) return;
        try {
            FloatControl gainControl = (FloatControl) clips[i].getControl(FloatControl.Type.MASTER_GAIN);
            // Chuyển đổi âm lượng tuyến tính (0.0-1.0) sang decibel (dB)
            float dB = (float) (Math.log10(volume) * 20);
            gainControl.setValue(dB);
        } catch (IllegalArgumentException e) {
            // Xảy ra khi volume = 0 (log(0) là vô cực)
            // Đặt ở mức âm lượng nhỏ nhất có thể
            FloatControl gainControl = (FloatControl) clips[i].getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(gainControl.getMinimum());
        } catch (Exception e) {
            System.out.println("⚠️ Không thể chỉnh âm lượng clip " + i);
            e.printStackTrace();
        }
    }
}