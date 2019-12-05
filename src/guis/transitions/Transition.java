package guis.transitions;

import guis.GuiInterface;

public abstract class Transition {

    // in milliseconds
    protected int length;

    private boolean done;

    Transition(int length) {
        if (length < 100)
            throw new IllegalArgumentException("Transitions cannot be shorter than 0.1s");

        this.length = length;

        this.done = false;
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

    public abstract void showTransition(GuiInterface gui);

    public abstract boolean animate(GuiInterface gui);
}