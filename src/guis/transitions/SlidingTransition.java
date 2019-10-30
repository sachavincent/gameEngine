package guis.transitions;

import static renderEngine.DisplayManager.FPS;

import guis.Gui;

public class SlidingTransition extends Transition {

    private final static float DISTANCE = 0.25f;

    private SlidingDirection direction;

    public SlidingTransition(int length, SlidingDirection direction) {
        super(length);

        this.direction = direction;
    }

    public SlidingDirection getDirection() {
        return this.direction;
    }

    @Override
    public void showTransition(Gui gui) {
        switch (direction) {
            case RIGHT:
                gui.setFinalX(gui.getX());
                gui.setX(gui.getX() - DISTANCE);
                gui.setStartX(gui.getX());

                break;
            case LEFT:
                gui.setFinalX(gui.getX());
                gui.setX(gui.getX() + DISTANCE);
                gui.setStartX(gui.getX());
                break;
            case BOTTOM:
                gui.setFinalY(gui.getY());
                gui.setY(gui.getY() + DISTANCE);
                gui.setStartY(gui.getY());

                break;
            case TOP:
                gui.setFinalY(gui.getY());
                gui.setY(gui.getY() - DISTANCE);
                gui.setStartY(gui.getY());

                break;
        }
    }

    @Override
    public boolean animate(Gui gui) {
        switch (direction) {
            case RIGHT:
                //TODO
                break;
            case LEFT:
                if (gui.getX() > gui.getFinalX()) {
                    gui.setX(gui.getX() - DISTANCE / (length / 1000f) / FPS);

                    if (gui.getX() < gui.getFinalX())
                        gui.setX(gui.getFinalX());
                }
                return gui.getX() == gui.getFinalX(); // Animation terminée
            case BOTTOM:
                //TODO
                break;
            case TOP:
                //TODO
                break;
        }
        return false;
    }
}
