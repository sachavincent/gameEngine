package util;

import events.Event;
import guis.GuiInterface;
import guis.transitions.Transition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class Timer {

    public final static Map<GuiInterface, List<TimerTask>> TASKS = new HashMap<>();
    public final static java.util.Timer                    TIMER = new java.util.Timer();

    public final static List<TimerTask> MISC_TASKS = new ArrayList<>();

    public static double getTime() {
        return (double) System.nanoTime() / (double) 1000000000L;
    }

    public static TimerTask scheduleTransition(Transition transition, GuiInterface guiInterface) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                switch (transition.getTrigger()) {
                    case HIDE:
                        transition.startTransitionHide(guiInterface);
                        break;
                    case SHOW:
                        transition.startTransitionShow(guiInterface);
                        break;
                }

                guiInterface.updateTexturePosition();
            }
        };
        TIMER.schedule(task, transition.getDelay());

        if (!TASKS.containsKey(guiInterface))
            TASKS.put(guiInterface, new ArrayList<>());

        TASKS.get(guiInterface).add(task);

        return task;
    }

    public static void scheduleEvent(Event event) {
        TIMER.schedule(event, event.getDelay());
        MISC_TASKS.add(event);
    }

    public static void removeMisc(Event event) {
        MISC_TASKS.remove(event);
    }

    public static void cancelTask(TimerTask task) {
        task.cancel();
    }
}
