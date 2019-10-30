package guis.presets.buttons;

import fontMeshCreator.Line;
import fontMeshCreator.Text;
import fontMeshCreator.TextMeshCreator;
import guis.GuiInterface;
import guis.basics.GuiBasics;
import guis.basics.GuiText;
import guis.constraints.GuiConstraintsManager;
import guis.presets.GuiPreset;
import inputs.callbacks.ClickCallback;
import java.util.List;
import javax.naming.SizeLimitExceededException;
import util.vector.Vector2f;

abstract class GuiButton extends GuiPreset {

    private final static float FILTER_TRANSPARENCY = 0.2f;
    private final static float BUTTON_SHRINK_RATIO = 0.05f;

    GuiBasics filterLayout;
    GuiBasics buttonLayout;

    private GuiText text;


    GuiButton(GuiInterface parent, GuiConstraintsManager constraintsManager) {
        super(parent, constraintsManager);
    }

    void addBasics(Text text) {
        if (text != null) {
            try {
                setupText(text);
            } catch (SizeLimitExceededException | IllegalArgumentException e) {
                e.printStackTrace();
            }

            addBasic(this.text);
        }

        setListeners();

        filterLayout.getTexture().setFinalAlpha(FILTER_TRANSPARENCY); // Filter on hover
        filterLayout.getTexture().setAlpha(0f); // Invisible by default

        addBasic(buttonLayout);
        addBasic(filterLayout);
    }

    private void setListeners() {
        buttonLayout.setOnClick(() -> {
            setClicked(true);
            System.out.println("Click");
            buttonLayout.scale(1 - BUTTON_SHRINK_RATIO);

            buttonLayout.updateTexturePosition();
        });

        buttonLayout.setOnRelease(() -> {
            setClicked(false);
            System.out.println("Release");
            buttonLayout.resetScale();

            buttonLayout.updateTexturePosition();
        });

        buttonLayout.setOnHover(() -> {
//            System.out.println("Hover");
        });

        buttonLayout.setOnLeave(() -> {
            System.out.println("Leave");
            filterLayout.getTexture().setAlpha(0f);

            filterLayout.updateTexturePosition();
        });

        buttonLayout.setOnEnter(() -> {
            System.out.println("Enter");
            filterLayout.getTexture().setAlpha(1f);

            filterLayout.updateTexturePosition();
        });
    }

    private void setupText(Text text) throws SizeLimitExceededException, IllegalArgumentException {
        text.setLineMaxSize(buttonLayout.getWidth());
        text.setCentered(false);

        List<Line> lines = text.getFont().getLoader().getLines(text);

        if (lines.size() > 1)
            throw new SizeLimitExceededException("Content must fit in one line.");

        Line line = lines.get(0);

        if (line == null)
            throw new IllegalArgumentException("Invalid text.");

        double textHeight = TextMeshCreator.LINE_HEIGHT * text.getFontSize() * 2;

        if (textHeight > buttonLayout.getHeight() * 2)
            throw new SizeLimitExceededException("Font size too large.");

        text.setPosition(new Vector2f(buttonLayout.getX() - line.getLineLength(),
                buttonLayout.getY() - textHeight / 2));

        this.text = new GuiText(this, text);
    }

    @Override
    public void setOnClick(ClickCallback onClickCallback) {
        buttonLayout.setOnClick(onClickCallback);
    }

}
