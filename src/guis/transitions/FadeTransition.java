package guis.transitions;

import static renderEngine.DisplayManager.FPS;

import guis.GuiInterface;

public class FadeTransition extends Transition {

    public FadeTransition(int length) {
        super(length);
    }

    @Override
    public void showTransition(GuiInterface gui) {
        gui.setAlpha(0f);
    }

    @Override
    public boolean animate(GuiInterface gui) {
        float futureAlpha = gui.getTexture().getAlpha() + 1 / (length / 1000f) / FPS;
        gui.setAlpha(futureAlpha);

        return futureAlpha >= 1f;
    }
}
