package guis.transitions;

import guis.GuiInterface;

public class SlidingTransition extends Transition {

    private final static float DISTANCE = 0.25f;

    private final SlidingDirection direction;

    public SlidingTransition(SlidingTransition transition) {
        super(transition.getTrigger(), transition.getDelay(), transition.getLength());

        this.direction = transition.getDirection();
    }

    public SlidingTransition(Trigger trigger, int delay, int length, SlidingDirection direction) {
        super(trigger, delay, length);

        this.direction = direction;
    }

    public SlidingTransition(Trigger trigger, int length, SlidingDirection direction) {
        super(trigger, length);

        this.direction = direction;
    }

    public SlidingDirection getDirection() {
        return this.direction;
    }

    @Override
    public void startTransitionShow(GuiInterface gui) {
        if (gui.isDisplayed())
            return;

        if (started)
            throw new IllegalStateException("Transition should not be started at this point");

        switch (direction) {
//            case RIGHT:
//                gui.setFinalX(gui.getX());
//                gui.setX(gui.getX() - DISTANCE);
//                gui.setStartX(gui.getX());
//
//                break;
//            case LEFT:
//                gui.setFinalX(gui.getX());
//                gui.setX(gui.getX() + DISTANCE);
//                gui.setStartX(gui.getX());
//
//                break;
//            case BOTTOM:
//                gui.setFinalY(gui.getY());
//                gui.setY(gui.getY() + DISTANCE);
//                gui.setStartY(gui.getY());
//
//                break;
//            case TOP:
//                gui.setFinalY(gui.getY());
//                gui.setY(gui.getY() - DISTANCE);
//                gui.setStartY(gui.getY());
//
//                break;
        }

        started = true;

        gui.setDisplayed(true);
    }

    @Override
    public void startTransitionHide(GuiInterface gui) {
        if (!gui.isDisplayed())
            return;

        if(started)
            throw new IllegalStateException("Transition should not be started at this point");

        switch (direction) {
//            case RIGHT:
//                gui.setFinalX(gui.getX() + DISTANCE);
//
//                break;
//            case LEFT:
//                gui.setFinalX(gui.getX() - DISTANCE);
//
//                break;
//            case BOTTOM:
//                gui.setFinalY(gui.getY() - DISTANCE);
//
//                break;
//            case TOP:
//                gui.setFinalY(gui.getY() + DISTANCE);
//                break;
        }

        started = true;
        gui.setDisplayed(false);
    }

    @Override
    public boolean animate(GuiInterface gui) {
        if (!started)
            return false;

        switch (direction) {
//            case RIGHT:
//                gui.setX(Float.min(gui.getFinalX(), gui.getX() + DISTANCE / (length / 1000f) / FRAMERATE_LIMIT));
//
//                return gui.getX() == gui.getFinalX(); // Animation terminée
//            case LEFT:
//                gui.setX(Float.max(gui.getFinalX(), gui.getX() - DISTANCE / (length / 1000f) / FRAMERATE_LIMIT));
//
//                return gui.getX() == gui.getFinalX(); // Animation terminée
//            case BOTTOM:
//                gui.setY(Float.max(gui.getFinalY(), gui.getY() - DISTANCE / (length / 1000f) / FRAMERATE_LIMIT));
//
//                gui.setY(gui.getFinalY());
//                return gui.getY() == gui.getFinalY(); // Animation terminée
//            case TOP:
//                gui.setY(Float.min(gui.getFinalY(), gui.getY() + DISTANCE / (length / 1000f) / FRAMERATE_LIMIT));
//
//                return gui.getY() == gui.getFinalY(); // Animation terminée
        }

        return false;
    }

    @Override
    public Transition copy() {
        return new SlidingTransition(this);
    }

    @Override
    public boolean equals(Object obj) {
        return getClass().equals(obj.getClass()) && getTrigger().equals(((Transition) obj).getTrigger());
    }

    @Override
    public String toString() {
        return "SlidingTransition{" +
                "direction=" + direction +
                "} ";
    }
}
