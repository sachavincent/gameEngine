package util;

public class TimeSystem {

    public static final int    TICK_RATE = 20;
    public static       int    elapsedTicks;
    private static      long   lastUpdateTime;
    public static       double timeSinceLastTick;

    private static int timeInTicks;

    private final int nbTicks;

    public TimeSystem(int nbTicks) {
        this.nbTicks = nbTicks;
    }

    public int getNbTicks() {
        return this.nbTicks;
    }

    public static int getElapsedTicks() {
        return elapsedTicks;
    }

    public static int getCurrentTime() {
        return timeInTicks;
    }

    public static void updateTimer() {
        long now = System.nanoTime();
        if (lastUpdateTime == 0L)
            lastUpdateTime = now;

        timeSinceLastTick += (now - lastUpdateTime) / (1000000000d / TICK_RATE);
        elapsedTicks += (int) timeSinceLastTick;
        lastUpdateTime = now;
        timeSinceLastTick -= elapsedTicks;
    }

    public static void nextTick() {
        timeInTicks++;
    }
}
