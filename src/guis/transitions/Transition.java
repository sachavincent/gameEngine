package guis.transitions;

import guis.Gui;

public abstract class Transition {

    private double length;

    private boolean done;

    public Transition(double length) {
        this.length = length;

        this.done = false;
    }

    public double getLength() {
        return this.length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public boolean isDone() {
        return this.done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public abstract void showTransition(Gui gui);

    public abstract void animate(Gui gui);
}