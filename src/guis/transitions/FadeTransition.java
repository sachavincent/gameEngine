package guis.transitions;

import static renderEngine.DisplayManager.FPS;

import guis.Gui;

public class FadeTransition extends Transition {

    public FadeTransition(int length) {
        super(length);
    }

    @Override
    public void showTransition(Gui gui) {
        gui.setAlphaToGui(0f);
    }

    @Override
    public boolean animate(Gui gui) {
        float futureAlpha = gui.getBackground().getAlpha() + 1 / (length / 1000f) / FPS;
        gui.setAlphaToGui(futureAlpha);

        return futureAlpha >= 1f;
    }
}
