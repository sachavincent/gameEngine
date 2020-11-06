package guis.transitions;

import guis.GuiInterface;

public abstract class Transition {

    // in milliseconds
    protected int length;

    private boolean done;

    private final Trigger trigger;
    private       int     delay;

    protected boolean started = false;


    Transition(Trigger trigger, int length) {
        this(trigger, 0, length);
    }

    Transition(Transition transition) {
        this(transition.getTrigger(), transition.getDelay(), transition.getLength());
    }

    Transition(Trigger trigger, int delay, int length) {
        if (length < 100)
            throw new IllegalArgumentException("Transitions cannot be shorter than 0.1s");

        this.length = length;
        this.trigger = trigger;
        this.delay = delay;

        this.done = false;
    }

    public int getDelay() {
        return this.delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public Trigger getTrigger() {
        return this.trigger;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isDone() {
        return this.done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public abstract void startTransitionShow(GuiInterface gui);

    public abstract void startTransitionHide(GuiInterface gui);

    public abstract boolean animate(GuiInterface gui);

    public abstract Transition copy();

    public boolean isStarted() {
        return this.started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public enum Trigger {
        SHOW,
        HIDE
    }

    @Override
    public String toString() {
        return "Transition{" +
                "length=" + length +
                ", done=" + done +
                ", trigger=" + trigger +
                ", delay=" + delay +
                ", started=" + started +
                '}';
    }
}