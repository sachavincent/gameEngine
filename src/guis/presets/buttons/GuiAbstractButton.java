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
import inputs.callbacks.EnterCallback;
import inputs.callbacks.LeaveCallback;
import inputs.callbacks.PressCallback;
import inputs.callbacks.ReleaseCallback;
import java.awt.Color;

public abstract class GuiAbstractButton extends GuiPreset {

    private final static float FILTER_TRANSPARENCY = 0.2f;
    private final static float BUTTON_SHRINK_RATIO = 0.05f;

    GuiBasics filterLayout;
    //    GuiBasics buttonLayout;
    GuiBasics borderLayout;

    private GuiText text;

    private Gui tooltipGui;

    private boolean toggleType = false;

    private ButtonGroup buttonGroup;

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text,
            GuiConstraintsManager constraintsManager) {
        super(parent, background, constraintsManager);

//        addBackgroundComponent(background);

        setupText(text);

        setupComponents();
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text, Text tooltipGui,
            GuiConstraintsManager constraintsManager) {
        super(parent, background, constraintsManager);

//        addBackgroundComponent(background);

        setupText(text);
        setTooltipGui(tooltipGui);

        setupComponents();
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, GuiConstraintsManager constraintsManager) {
        super(parent, background, constraintsManager);

//        addBackgroundComponent(background);
        setupComponents();
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        super(parent, background, constraintsManager);

//        addBackgroundComponent(background);

        setupText(text);

        setupComponents();

        setCornerRadius(cornerRadius);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text, Text tooltipGui,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        super(parent, background, constraintsManager);

//        addBackgroundComponent(background);

        setupText(text);
        setTooltipGui(tooltipGui);

        setupComponents();

        setCornerRadius(cornerRadius);
        this.tooltipGui.setCornerRadius(cornerRadius);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text, Text tooltipGui, int cornerRadius) {
        super(parent, background);

//        addBackgroundComponent(background);

        setupText(text);
        setTooltipGui(tooltipGui);

        setupComponents();

        setCornerRadius(cornerRadius);

        this.tooltipGui.setCornerRadius(cornerRadius);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text, Text tooltipGui) {
        super(parent, background);

//        addBackgroundComponent(background);

        setupText(text);
        setTooltipGui(tooltipGui);

        setupComponents();
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, GuiConstraintsManager constraintsManager,
            int cornerRadius) {
        super(parent, background, constraintsManager);

//        addBackgroundComponent(background);
        setupComponents();

        setCornerRadius(cornerRadius);
    }

    private void setupComponents() {
        setListeners();

        this.buttonGroup = new ButtonGroup(1);
        this.buttonGroup.addButton(this);

        setDisplayedComponents(false);
    }

    public void setDisplayedComponents(boolean displayed) {
        if (this.filterLayout != null)
            this.filterLayout.setDisplayed(displayed);
        if (this.borderLayout != null)
            this.borderLayout.setDisplayed(displayed);
//        if (this.buttonLayout != null)
//            this.buttonLayout.setDisplayed(displayed);
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

//    protected abstract void addBackgroundComponent(Background<?> background);

    @Override
    public void setOnPress(PressCallback onPressCallback) {
        PressCallback basicPressCallback = () -> {
            if (!toggleType || !isClicked())
                setClicked(true);
            else if (toggleType && isClicked() && buttonGroup.getMaxButtons() == 1)
                setClicked(false);
            //  System.out.println("Press");

            updateTexturesOnClick();
            onPressCallback.onPress();
        };

        super.setOnPress(basicPressCallback);
    }

    @Override
    public void setOnRelease(ReleaseCallback onReleaseCallback) {
        ReleaseCallback basicReleaseCallback = () -> {
            if (!toggleType)
                setClicked(false);
            //  System.out.println("Press");

            updateTexturesOnClick();
            onReleaseCallback.onRelease();
        };

        super.setOnRelease(basicReleaseCallback);
    }

    @Override
    public void setOnLeave(LeaveCallback onLeaveCallback) {
        LeaveCallback basicLeaveCallback = () -> {
            //System.out.println("Leave");
            if (!isClicked() || !toggleType) {
                setFilterTransparency(0);
                if (tooltipGui != null) {
                    Gui.hideGui(tooltipGui);
                }
            }
            onLeaveCallback.onLeave();
        };

        super.setOnLeave(basicLeaveCallback);
    }

    public void setFilterTransparency(float alpha) {
        if (this.filterLayout == null)
            return;

        this.filterLayout.getTexture().setAlpha(alpha);
        this.filterLayout.updateTexturePosition();
    }

    @Override
    public void setOnEnter(EnterCallback onEnterCallback) {
        EnterCallback basicEnterCallback = () -> {
            //  System.out.println("Enter");
            if (!isClicked() || !toggleType) {
                setFilterTransparency(1);

                if (tooltipGui != null) {
                    Gui.showGui(tooltipGui);
                }
            }
            onEnterCallback.onEnter();
        };

        super.setOnEnter(basicEnterCallback);
    }

    @Override
    public void setClicked(boolean clicked) {
        if (clicked)
            this.buttonGroup.setButtonClicked(ID);
        else
            this.clicked = false;
    }

    private void setListeners() {
        setOnEnter(() -> {
        });
        setOnLeave(() -> {
        });
        setOnRelease(() -> {
        });
        setOnPress(() -> {
        });
    }

    private void updateTexturesOnClick() {
        super.updateTexturePosition();

        if (!toggleType) {
            if (this.isClicked()) {
                float scale = 1 - BUTTON_SHRINK_RATIO;
//            this.buttonLayout.scale(scale);
                if (this.filterLayout != null)
                    this.filterLayout.scale(scale);

                if (this.borderLayout != null)
                    this.borderLayout.scale(scale);
            } else {
//            this.buttonLayout.resetScale();

                if (this.filterLayout != null)
                    this.filterLayout.resetScale();

                if (this.borderLayout != null)
                    this.borderLayout.resetScale();
            }
        }
//        this.buttonLayout.updateTexturePosition();

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
        setFilterTransparency(0f);
    }

    public GuiBasics getFilter() {
        return this.filterLayout;
    }

    protected abstract void setFilter();

    void addBorderLayout(GuiShape guiShape) {
        if (guiShape == null)
            throw new IllegalArgumentException("Border missing");

        this.borderLayout = guiShape;
    }

    public void setToggleType(boolean toggleType) {
        this.toggleType = toggleType;
    }

    public boolean isToggleType() {
        return this.toggleType;
    }

    public void setButtonGroup(ButtonGroup buttonGroup) {
        this.buttonGroup = buttonGroup;
        this.buttonGroup.addButton(this);
    }

    public enum ButtonType {
        RECTANGLE,
        CIRCULAR
    }
}
