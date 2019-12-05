package guis.transitions;

import static renderEngine.DisplayManager.FPS;

import guis.GuiInterface;

public class SlidingTransition extends Transition {

    private final static float DISTANCE = 0.25f;

    private SlidingDirection direction;
    //TODO: Trigger (powerpoint like : after component, same time as component, ...)

    public SlidingTransition(int length, SlidingDirection direction) {
        super(length);

        this.direction = direction;
    }

    public SlidingDirection getDirection() {
        return this.direction;
    }

    @Override
    public void showTransition(GuiInterface gui) {
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
    public boolean animate(GuiInterface gui) {
        switch (direction) {
            case RIGHT:
                if (gui.getX() < gui.getFinalX()) {
                    gui.setX(gui.getX() + DISTANCE / (length / 1000f) / FPS);

                    if (gui.getX() > gui.getFinalX())
                        gui.setX(gui.getFinalX());
                }
                return gui.getX() == gui.getFinalX(); // Animation terminée
            case LEFT:
                if (gui.getX() > gui.getFinalX()) {
                    gui.setX(gui.getX() - DISTANCE / (length / 1000f) / FPS);

                    if (gui.getX() < gui.getFinalX())
                        gui.setX(gui.getFinalX());
                }
                return gui.getX() == gui.getFinalX(); // Animation terminée
            case BOTTOM:
                if (gui.getY() > gui.getFinalY()) {
                    gui.setY(gui.getY() - DISTANCE / (length / 1000f) / FPS);

                    if (gui.getY() < gui.getFinalY())
                        gui.setY(gui.getFinalY());
                }
                return gui.getY() == gui.getFinalY(); // Animation terminée
            case TOP:
                if (gui.getY() < gui.getFinalY()) {
                    gui.setY(gui.getY() + DISTANCE / (length / 1000f) / FPS);

                    if (gui.getY() > gui.getFinalY())
                        gui.setY(gui.getFinalY());
                }
                return gui.getY() == gui.getFinalY(); // Animation terminée
        }
        return false;
    }
}
