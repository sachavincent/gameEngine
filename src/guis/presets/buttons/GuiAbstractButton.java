package guis.presets.buttons;

import fontMeshCreator.Line;
import fontMeshCreator.Text;
import fontMeshCreator.TextMeshCreator;
import guis.Gui;
import guis.GuiInterface;
import guis.basics.GuiBasics;
import guis.basics.GuiShape;
import guis.basics.GuiText;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.PixelConstraint;
import guis.constraints.RelativeConstraint;
import guis.presets.Background;
import guis.presets.GuiPreset;
import java.awt.Color;
import java.util.List;
import util.math.Vector2f;

public abstract class GuiAbstractButton extends GuiPreset {

    private final static float FILTER_TRANSPARENCY = 0.2f;
    private final static float BUTTON_SHRINK_RATIO = 0.05f;

    GuiBasics filterLayout;
    GuiBasics buttonLayout;
    GuiBasics borderLayout;

    private GuiText text;

    private Gui tooltipGui;

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text,
            GuiConstraintsManager constraintsManager) {
        super(parent, constraintsManager);

        addBackgroundComponent(background);

        setupText(text);

        setupComponents();
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text, Text tooltipGui,
            GuiConstraintsManager constraintsManager) {
        super(parent, constraintsManager);

        addBackgroundComponent(background);

        setupText(text);
        setupTooltipText(tooltipGui);

        setupComponents();
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, GuiConstraintsManager constraintsManager) {
        super(parent, constraintsManager);

        addBackgroundComponent(background);
        setupComponents();
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        super(parent, constraintsManager);

        addBackgroundComponent(background);

        setupText(text);

        setupComponents();

        setCornerRadius(cornerRadius);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text, Text tooltipGui,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        super(parent, constraintsManager);

        addBackgroundComponent(background);

        setupText(text);
        setupTooltipText(tooltipGui);

        setupComponents();

        setCornerRadius(cornerRadius);
        this.tooltipGui.setCornerRadius(cornerRadius);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text, Text tooltipGui, int cornerRadius) {
        super(parent);

        addBackgroundComponent(background);

        setupText(text);
        setupTooltipText(tooltipGui);

        setupComponents();

        setCornerRadius(cornerRadius);
        this.tooltipGui.setCornerRadius(cornerRadius);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text, Text tooltipGui) {
        super(parent);

        addBackgroundComponent(background);

        setupText(text);
        setupTooltipText(tooltipGui);

        setupComponents();
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, GuiConstraintsManager constraintsManager,
            int cornerRadius) {
        super(parent, constraintsManager);

        addBackgroundComponent(background);
        setupComponents();

        setCornerRadius(cornerRadius);
    }

    private void setupComponents() {
        this.filterLayout.getTexture().setFinalAlpha(FILTER_TRANSPARENCY); // Filter on hover
        this.filterLayout.getTexture().setAlpha(0f); // Invisible by default

        setListeners();

        addBasic(this.buttonLayout);
        addBasic(this.filterLayout);
    }

    public void setupText(Text text) {
        if (text == null)
            return;

        if (this.text != null)
            removeBasic(this.text);

        this.text = new GuiText(this, text);

        addBasic(this.text);
    }

    public void setupTooltipText(Text text) {
        setTooltipGui(text);
    }

    protected abstract void addBackgroundComponent(Background<?> background);

    private void setListeners() {
        setOnPress(() -> {
            setClicked(true);
            System.out.println("Press");

            updateTexturesOnClick();
        });

        setOnRelease(() -> {
            setClicked(false);
            //System.out.println("Release");

            updateTexturesOnClick();
        });

        setOnHover(() -> {
//            System.out.println("Hover");
        });

        setOnLeave(() -> {
            System.out.println("Leave");
            filterLayout.getTexture().setAlpha(0f);

            filterLayout.updateTexturePosition();
            if (tooltipGui != null) {
                Gui.hideGui(tooltipGui);
            }
        });

        setOnEnter(() -> {
            System.out.println("Enter");
            filterLayout.getTexture().setAlpha(1f);

            filterLayout.updateTexturePosition();
            if (tooltipGui != null) {
                Gui.showGui(tooltipGui);
            }
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

    private void setTooltipGui(Text text) {
        if (text == null)
            return;

        List<Line> lines = text.getFont().getLoader().getLines(text);

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


        tooltipGui = new Gui(new Background<>(new Color(64, 64, 64, 100)));
        GuiConstraintsManager guiConstraintsManager = new GuiConstraintsManager();
        guiConstraintsManager.setxConstraint(new RelativeConstraint(0, this));
        guiConstraintsManager.setyConstraint(new RelativeConstraint(.1f, this));
        guiConstraintsManager.setHeightConstraint(new PixelConstraint(30));
        guiConstraintsManager.setWidthConstraint(new RelativeConstraint(.05f));
        tooltipGui.setConstraints(guiConstraintsManager);

        text.setLineMaxSize(tooltipGui.getWidth());
        text.setCentered(true);
        text.setPosition(new Vector2f(tooltipGui.getX() - tooltipGui.getWidth() + line.getLineLength(),
                -tooltipGui.getY() - textHeight / 2));
        GuiText guiText = new GuiText(tooltipGui, text);
        guiText.setDisplayed(false);
        tooltipGui.addComponent(guiText);
    }

    public GuiText getText() {
        return this.text;
    }

    public Gui getTooltipGui() {
        return this.tooltipGui;
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
