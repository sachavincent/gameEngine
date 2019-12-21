package guis.presets.buttons;

import fontMeshCreator.Line;
import fontMeshCreator.Text;
import fontMeshCreator.TextMeshCreator;
import guis.GuiInterface;
import guis.basics.GuiBasics;
import guis.basics.GuiShape;
import guis.basics.GuiText;
import guis.constraints.GuiConstraintsManager;
import guis.presets.GuiBackground;
import guis.presets.GuiPreset;
import java.awt.Color;
import java.util.List;
import javax.naming.SizeLimitExceededException;
import util.vector.Vector2f;

public abstract class GuiAbstractButton extends GuiPreset {

    private final static float FILTER_TRANSPARENCY = 0.2f;
    private final static float BUTTON_SHRINK_RATIO = 0.05f;

    GuiBasics filterLayout;
    GuiBasics buttonLayout;
    GuiBasics borderLayout;

    private GuiText text;

    GuiAbstractButton(GuiInterface parent, GuiBackground<?> background, Text text,
            GuiConstraintsManager constraintsManager) {
        super(parent, constraintsManager);

        addBackgroundComponent(background);
        setupText(text);
        setupComponents();
    }

    GuiAbstractButton(GuiInterface parent, GuiBackground<?> background, GuiConstraintsManager constraintsManager) {
        super(parent, constraintsManager);

        addBackgroundComponent(background);
        setupComponents();
    }

    private void setupComponents() {
        this.filterLayout.getTexture().setFinalAlpha(FILTER_TRANSPARENCY); // Filter on hover
        this.filterLayout.getTexture().setAlpha(0f); // Invisible by default

        setListeners();

        if (this.text != null)
            addBasic(this.text);

        addBasic(this.buttonLayout);
        addBasic(this.filterLayout);
    }

    public void setupText(Text text) {
        if (this.text != null)
            removeBasic(this.text);

        setText(text);

        addBasic(this.text);
    }

    protected abstract void addBackgroundComponent(GuiBackground<?> background);

    private void setListeners() {
        setOnClick(() -> {
            setClicked(true);
            System.out.println("Click");

            updateTexturesOnClick();
        });

        setOnRelease(() -> {
            setClicked(false);
            System.out.println("Release");

            updateTexturesOnClick();
        });

        setOnPress(() -> {
            setClicked(false);
            System.out.println("Press");

            updateTexturesOnClick();
        });

        setOnHover(() -> {
//            System.out.println("Hover");
        });

        setOnLeave(() -> {
            System.out.println("Leave");
            filterLayout.getTexture().setAlpha(0f);

            filterLayout.updateTexturePosition();
        });

        setOnEnter(() -> {
            System.out.println("Enter");
            filterLayout.getTexture().setAlpha(1f);

            filterLayout.updateTexturePosition();
        });
    }

    private void updateTexturesOnClick() {
        super.updateTexturePosition();

        if (this.isClicked()) {
            float scale = 1 - BUTTON_SHRINK_RATIO;
            this.buttonLayout.scale(scale);
            this.filterLayout.scale(scale);

            if (this.borderLayout != null)
                this.borderLayout.scale(scale);
        } else {
            this.buttonLayout.resetScale();
            this.filterLayout.resetScale();

            if (this.borderLayout != null)
                this.borderLayout.resetScale();
        }

        this.buttonLayout.updateTexturePosition();
        this.filterLayout.updateTexturePosition();

        if (this.borderLayout != null)
            this.borderLayout.updateTexturePosition();
    }

    private void setText(Text text) {
        if (text == null)
            return;

        text.setLineMaxSize(buttonLayout.getWidth());
        text.setCentered(false);

        List<Line> lines = text.getFont().getLoader().getLines(text);

        if (lines.size() > 1) {
            try {
                throw new SizeLimitExceededException("Content must fit in one line.");
            } catch (SizeLimitExceededException e) {
                e.printStackTrace();
                return;
            }
        }

        Line line = lines.get(0);

        if (line == null) {
            try {
                throw new IllegalArgumentException("Invalid text.");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();

                return;
            }
        }

        double textHeight = TextMeshCreator.LINE_HEIGHT * text.getFontSize() * 2;

        if (textHeight > buttonLayout.getHeight() * 2) {
            try {
                throw new SizeLimitExceededException("Font size too large.");
            } catch (SizeLimitExceededException e) {
                e.printStackTrace();

                return;
            }
        }

        text.setPosition(new Vector2f(buttonLayout.getX() - line.getLineLength(),
                -buttonLayout.getY() - textHeight / 2));

        this.text = new GuiText(this, text);
    }


    public void setBorder(float r, float g, float b) {
        setBorder(new Color(r, g, b));
    }

    public void setBorder(int r, int g, int b) {
        setBorder(new Color(r, g, b));
    }

    public abstract void setBorder(Color color);

    public void addBorderLayout(GuiShape guiShape) {
        if (guiShape == null)
            throw new IllegalArgumentException("Border missing");

        this.borderLayout = guiShape;

        addBasic(this.borderLayout);
    }

    public enum ButtonType {
        RECTANGLE,
        CIRCULAR
    }
}