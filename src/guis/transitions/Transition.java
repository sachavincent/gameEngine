package guis.transitions;

import guis.Gui;

public abstract class Transition {

    // in milliseconds
    protected int length;

    private boolean done;

    public Transition(int length) {
        if (length < 100)
            return; //TODO: Exception

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

    public abstract void showTransition(Gui gui);

    public abstract boolean animate(Gui gui);
}