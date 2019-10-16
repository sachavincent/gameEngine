package guis.transitions;

import guis.Gui;

public class FadeTransition extends Transition {

    public FadeTransition(double length) {
        super(length);
    }

    @Override
    public void showTransition(Gui gui) {
        gui.setAlphaToGui(0f);
    }

    @Override
    public void animate(Gui gui) {
        gui.setAlphaToGui(Math.abs(gui.getStartX() - gui.getX()) * gui.getStartX() / (gui.getStartX() - gui.getFinalX())); //TODO W/ time
    }
}
