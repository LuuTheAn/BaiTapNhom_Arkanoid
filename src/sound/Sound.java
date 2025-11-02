package sound;

import javax.sound.sampled.*;
import java.net.URL;

public class Sound {
    private static Sound instance;  // 🔹 Singleton instance
    private final Clip[] clips = new Clip[15];

    // 🔒 Constructor private để không cho tạo bên ngoài
    private Sound() {
        try {
            URL[] soundURLs = new URL[15];
            soundURLs[0] = getClass().getResource("/sound/nen.wav");
            soundURLs[1] = getClass().getResource("/sound/click.wav");
            soundURLs[2] = getClass().getResource("/sound/break.wav");
            soundURLs[3] = getClass().getResource("/sound/unbreakable.wav");
            soundURLs[4] = getClass().getResource("/sound/exploding.wav");
            soundURLs[5] = getClass().getResource("/sound/lose.wav");
            soundURLs[6] = getClass().getResource("/sound/win.wav");
            soundURLs[7] = getClass().getResource("/sound/strong1.wav");
            soundURLs[8] = getClass().getResource("/sound/strong2.wav");
            soundURLs[9] = getClass().getResource("/sound/fast.wav");
            soundURLs[10] = getClass().getResource("/sound/expand.wav");
            soundURLs[11] = getClass().getResource("/sound/paddle2.wav");
            soundURLs[12] = getClass().getResource("/sound/ballout.wav");
            soundURLs[13] = getClass().getResource("/sound/finalwin.wav");
            soundURLs[14] = getClass().getResource("/sound/pause.wav");

            // 🔹 Load toàn bộ âm thanh
            for (int i = 0; i < soundURLs.length; i++) {
                if (soundURLs[i] != null) {
                    AudioInputStream ais = AudioSystem.getAudioInputStream(soundURLs[i]);
                    clips[i] = AudioSystem.getClip();
                    clips[i].open(ais);
                }
            }

            // 🔹 Làm nóng mixer
            if (clips[1] != null) {
                clips[1].setFramePosition(0);
                clips[1].start();
                clips[1].stop();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🧠 Phương thức truy cập instance duy nhất
    public static synchronized Sound getInstance() {
        if (instance == null) {
            instance = new Sound();
        }
        return instance;
    }

    public void play(int i) {
        if (clips[i] == null) return;
        clips[i].stop();
        clips[i].setFramePosition(0);
        clips[i].start();
    }

    public void loop(int i) {
        if (clips[i] == null) return;
        clips[i].loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop(int i) {
        if (clips[i] == null) return;
        clips[i].stop();
    }

    public void setVolume(int i, float volume) {
        if (clips[i] == null) return;
        try {
            FloatControl gainControl = (FloatControl) clips[i].getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log10(volume) * 20);
            gainControl.setValue(dB);
        } catch (Exception e) {
            System.out.println("⚠️ Không thể chỉnh âm lượng clip " + i);
        }
    }
}
