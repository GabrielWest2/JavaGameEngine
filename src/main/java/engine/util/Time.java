package engine.util;

public class Time {
    private static long lastFrame;
    private static float dt;

    public Time() {
        lastFrame = System.currentTimeMillis();
    }

    public static void updateTime() {
        dt = (System.currentTimeMillis() - lastFrame) / 1000f;
        lastFrame = System.currentTimeMillis();
    }

    public static float getDeltaTime() {
        return dt;
    }
}
