package util;

public class TimeSystem {

    public static final  int   TICK_RATE = 20;
    private final static float STEP      = 1000.0f / TICK_RATE;

    public static int   elapsedTicks;
    public static float nbTicksSinceLastUpdate;

    private static long  lastUpdateTime;
    private static int   timeInTicks;
    private static float partialTicks;

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

    public static void resetTimer() {
        lastUpdateTime = getTimeMillis();
        elapsedTicks = 0;
        nbTicksSinceLastUpdate = 0;
        partialTicks = 0;
    }

    public static long getTimeMillis() {
        return System.nanoTime() / 1000000L;
    }

    public static int getCurrentTimeTicks() {
        return timeInTicks;
    }

    public static void updateTimer() {
        long now = getTimeMillis();
        nbTicksSinceLastUpdate = (float) (now - lastUpdateTime) / STEP;
        lastUpdateTime = now;
        partialTicks += nbTicksSinceLastUpdate;
        elapsedTicks = (int) partialTicks;
        partialTicks -= (float) elapsedTicks;
    }

    public static void nextTick() {
        timeInTicks++;
    }
}
