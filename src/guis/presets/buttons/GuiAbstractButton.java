package guis.presets.buttons;

import fontMeshCreator.Text;
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
        setTooltipGui(tooltipGui);

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
        setTooltipGui(tooltipGui);

        setupComponents();

        setCornerRadius(cornerRadius);
        this.tooltipGui.setCornerRadius(cornerRadius);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text, Text tooltipGui, int cornerRadius) {
        super(parent);

        addBackgroundComponent(background);

        setupText(text);
        setTooltipGui(tooltipGui);

        setupComponents();

        setCornerRadius(cornerRadius);
        this.tooltipGui.setCornerRadius(cornerRadius);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text, Text tooltipGui) {
        super(parent);

        addBackgroundComponent(background);

        setupText(text);
        setTooltipGui(tooltipGui);

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
        setListeners();

        setDisplayedComponents(false);
    }

    public void setDisplayedComponents(boolean displayed) {
        if (this.filterLayout != null)
            this.filterLayout.setDisplayed(displayed);
        if (this.borderLayout != null)
            this.borderLayout.setDisplayed(displayed);
        this.buttonLayout.setDisplayed(displayed);
        if (this.text != null)
            this.text.setDisplayed(displayed);
        if (this.tooltipGui != null)
            this.tooltipGui.setDisplayed(displayed);
    }

    public void setupText(Text text) {
        if (text == null)
            return;

        this.text = new GuiText(this, text);
    }

    protected abstract void addBackgroundComponent(Background<?> background);

    private void setListeners() {
        setOnPress(() -> {
            setClicked(true);
            //  System.out.println("Press");

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
            //System.out.println("Leave");
            if (this.filterLayout != null) {
                filterLayout.getTexture().setAlpha(0f);

                filterLayout.updateTexturePosition();
            }
            if (tooltipGui != null) {
                Gui.hideGui(tooltipGui);
            }
        });

        setOnEnter(() -> {
            //  System.out.println("Enter");
            if (this.filterLayout != null) {
                filterLayout.getTexture().setAlpha(1f);

                filterLayout.updateTexturePosition();
            }
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
            if (this.filterLayout != null)
                this.filterLayout.scale(scale);

            if (this.borderLayout != null)
                this.borderLayout.scale(scale);
        } else {
            this.buttonLayout.resetScale();

            if (this.filterLayout != null)
                this.filterLayout.resetScale();

            if (this.borderLayout != null)
                this.borderLayout.resetScale();
        }

        this.buttonLayout.updateTexturePosition();

        if (this.filterLayout != null)
            this.filterLayout.updateTexturePosition();

        if (this.borderLayout != null)
            this.borderLayout.updateTexturePosition();
    }

    private void setTooltipGui(Text text) {
        if (text == null)
            return;

        tooltipGui = new Gui(new Background<>(new Color(64, 64, 64, 100)));
        GuiConstraintsManager guiConstraintsManager = new GuiConstraintsManager();
        guiConstraintsManager.setxConstraint(new RelativeConstraint(0, this));
        guiConstraintsManager.setyConstraint(new RelativeConstraint(.1f, this));
        guiConstraintsManager.setHeightConstraint(new PixelConstraint(30));
        guiConstraintsManager.setWidthConstraint(new RelativeConstraint(.05f));
        tooltipGui.setConstraints(guiConstraintsManager);

        tooltipGui.addComponent(tooltipGui.setupText(text));
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

    public void enableFilter() {
        setFilter();

        this.filterLayout.getTexture().setFinalAlpha(FILTER_TRANSPARENCY); // Filter on hover
        this.filterLayout.getTexture().setAlpha(0f); // Invisible by default
    }

    protected abstract void setFilter();

    void addBorderLayout(GuiShape guiShape) {
        if (guiShape == null)
            throw new IllegalArgumentException("Border missing");

        this.borderLayout = guiShape;
    }

    public enum ButtonType {
        RECTANGLE,
        CIRCULAR
    }
}
