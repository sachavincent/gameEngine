package guis.transitions;

import static renderEngine.DisplayManager.FPS;

import guis.Gui;
import util.Timer;

public class SliderTransition extends Transition {

    private final static float DISTANCE = 0.25f;

    private Slider direction;

    public SliderTransition(int length, Slider direction) {
        super(length);

        this.direction = direction;
    }

    public Slider getDirection() {
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
            default:
                //TODO: exception
                break;
        }
    }

    @Override
    public void animate(Gui gui) {
        switch (direction) {
            case RIGHT:

                break;
            case LEFT:
                if (gui.getX() > gui.getFinalX()) {
                    gui.setX(gui.getX() - DISTANCE / (length / 1000f) / FPS);

                    if (gui.getX() < gui.getFinalX())
                        gui.setX(gui.getFinalX());
                }

                if (gui.getX() == gui.getFinalX()) { // Animation terminée
                    setDone(true);
                }
                break;
            case BOTTOM:

                break;
            case TOP:

                break;
            default:
                //TODO: exception
                break;
        }
    }
}
