package guis.transitions;

import static renderEngine.DisplayManager.FPS;

import guis.GuiInterface;

public class FadeTransition extends Transition {

    public FadeTransition(FadeTransition transition) {
        super(transition.getTrigger(), transition.getDelay(), transition.getLength());
    }

    public FadeTransition(Trigger trigger, int delay, int length) {
        super(trigger, delay, length);
    }

    public FadeTransition(Trigger trigger, int length) {
        super(trigger, length);
    }

    @Override
    public void startTransitionShow(GuiInterface gui) {
        gui.setAlpha(0f);

        gui.setDisplayed(true);
    }

    @Override
    public void startTransitionHide(GuiInterface gui) {
        gui.setAlpha(1f);

        gui.setDisplayed(false);
    }


    @Override
    public boolean animate(GuiInterface gui) {
        switch (getTrigger()) {
            case SHOW:
                float futureAlpha = gui.getTexture().getAlpha() + 1 / (length / 1000f) / FPS;
                gui.setAlpha(Float.min(futureAlpha, 1f));

                return futureAlpha == 1f;
            case HIDE:
                futureAlpha = gui.getTexture().getAlpha() - 1 / (length / 1000f) / FPS;
                gui.setAlpha(Float.max(futureAlpha, 0f));

                return futureAlpha <= 0;
        }
        return false;
    }

    @Override
    public Transition copy() {
        return new FadeTransition(this);
    }

    @Override
    public boolean equals(Object obj) {
        return getClass().equals(obj.getClass());
    }
}
