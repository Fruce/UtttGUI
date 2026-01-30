package board;

import javafx.scene.media.AudioClip;

public final class SoundFX {

    private SoundFX() {}
    
    public static void init() {
        warmUp(HOVER);
    }

    /* ================= CORE LOADER ================= */

    private static AudioClip load(String path, double volume) {
        var url = SoundFX.class.getResource(path);
        if (url == null) {
            System.err.println("[SoundFX] Missing sound: " + path);
            return null;
        }

        AudioClip clip = new AudioClip(url.toExternalForm());
        clip.setVolume(volume);
        return clip;
    }
    
    private static void warmUp(AudioClip clip) {
        if (clip == null) return;

        clip.play(0.0);  // silent play
    }

    /* ================= HOVER ================= */

    private static final AudioClip HOVER =
            load("/sound/hover.wav", 0.02);

    private static final long HOVER_COOLDOWN_NS = 80_000_000L; // 80 ms
    private static long lastHoverTimeNs = 0;

    public static void playHover() {
        if (HOVER == null) return;

        long now = System.nanoTime();
        if (now - lastHoverTimeNs < HOVER_COOLDOWN_NS) {
            return;
        }

        lastHoverTimeNs = now;
        HOVER.play();
    }

    /* ================= CLICK ================= */

    private static final AudioClip CLICK_X=
            load("/sound/click1.wav", 0.2);

    public static void playClick_X() {
        if (CLICK_X == null) return;

        CLICK_X.stop();
        CLICK_X.play();
    }
    
    private static final AudioClip CLICK_O=
            load("/sound/click2.wav", 0.2);

    public static void playClick_O() {
        if (CLICK_O == null) return;

        CLICK_O.stop();
        CLICK_O.play();
    }

    /* ================= LOCAL WIN ================= */

    private static final AudioClip WIN_LOCAL_X =
            load("/sound/winLocal1.wav", 0.12);

    public static void playWinLocal_X() {
        if (WIN_LOCAL_X == null) return;
        WIN_LOCAL_X.play();
    }
    
    private static final AudioClip WIN_LOCAL_O =
            load("/sound/winLocal2.wav", 0.12);

    public static void playWinLocal_O() {
        if (WIN_LOCAL_O == null) return;
        WIN_LOCAL_O.play();
    }
    
    /* ================= GLOBAL WIN ================= */

    private static final AudioClip WIN_GLOBAL =
            load("/sound/winGlobal.wav", 0.2);

    public static void playWinGlobal() {
        if (WIN_GLOBAL == null) return;
        WIN_GLOBAL.play();
    }
}

