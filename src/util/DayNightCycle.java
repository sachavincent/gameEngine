package util;

import display.DisplayManager;

public class DayNightCycle {

    public static final double NOON = Math.PI * 2.0;

    public static final int DAY_NIGHT_CYCLE_DURATION = 6000; // In seconds

    private static int nbFramesCycle;

    public static int getNbFramesCycle() {
        return nbFramesCycle;
    }

    public static void incrementCycleTime() {
        nbFramesCycle++;
        if (((double) nbFramesCycle / (double) DisplayManager.CURRENT_FPS) >= DAY_NIGHT_CYCLE_DURATION)
            nbFramesCycle = 0;
    }
}
