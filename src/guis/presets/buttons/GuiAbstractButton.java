package guis.presets.buttons;

import fontMeshCreator.Text;
import guis.Gui;
import guis.GuiInterface;
import guis.basics.GuiBasics;
import guis.basics.GuiRectangle;
import guis.basics.GuiShape;
import guis.basics.GuiText;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.PixelConstraint;
import guis.constraints.RelativeConstraint;
import guis.constraints.Side;
import guis.constraints.StickyConstraint;
import guis.presets.Background;
import guis.presets.GuiPreset;
import inputs.callbacks.EnterCallback;
import inputs.callbacks.LeaveCallback;
import inputs.callbacks.PressCallback;
import inputs.callbacks.ReleaseCallback;
import java.awt.Color;
import models.RawModel;

public abstract class GuiAbstractButton extends GuiPreset {

    private final static float FILTER_TRANSPARENCY = 0.2f;
    private final static float SCALE_FACTOR        = 0.05f;

    GuiBasics filterLayout;
    GuiShape  buttonShape;
    GuiBasics borderLayout;

    private GuiText text;

    private GuiRectangle tooltipGui;

    private boolean toggleType = false;

    private ButtonGroup     buttonGroup;
    private PressCallback   onPressCallback;
    private ReleaseCallback onReleaseCallback;

    private boolean releaseInsideNeeded = true; // If button needs to be released inside of this component

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text,
            GuiConstraintsManager constraintsManager) {
        this(parent, background, constraintsManager);

        setupText(text);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text, Text tooltipGui,
            GuiConstraintsManager constraintsManager) {
        this(parent, background, text, constraintsManager);

        setTooltipGui(tooltipGui);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, GuiConstraintsManager constraintsManager) {
        super(parent, Background.NO_BACKGROUND, constraintsManager);

        setButtonShape(background);
        setupComponents();
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text) {
        this(parent, background, text, (GuiConstraintsManager) null);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        this(parent, background, text, constraintsManager);

        setCornerRadius(cornerRadius);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text, Text tooltipGui,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        this(parent, background, text, constraintsManager, cornerRadius);

        if (tooltipGui != null) {
            setTooltipGui(tooltipGui);
            this.tooltipGui.setCornerRadius(cornerRadius);
        }
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text, Text tooltipGui, int cornerRadius) {
        this(parent, background, text, tooltipGui, null, cornerRadius);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Text text, Text tooltipGui) {
        this(parent, background, text, tooltipGui, null);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, GuiConstraintsManager constraintsManager,
            int cornerRadius) {
        this(parent, background, null, null, constraintsManager, cornerRadius);
    }

    private void setupComponents() {
        setListeners();

        this.buttonGroup = new ButtonGroup(1);
        this.buttonGroup.addButton(this);

        setDisplayedComponents(false);
    }

    protected abstract void setButtonShape(Background<?> background);

    public void setDisplayedComponents(boolean displayed) {
        if (this.filterLayout != null)
            this.filterLayout.setDisplayed(displayed);
        if (this.borderLayout != null)
            this.borderLayout.setDisplayed(displayed);
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

    public void setOnPress(PressCallback onPressCallback) {
        this.onPressCallback = () -> {
            if (!this.toggleType || !isClicked())
                setClicked(true);
            else if (this.toggleType && isClicked() && this.buttonGroup.getMaxButtons() == 1)
                setClicked(false);

            updateTexturePosition();
            onPressCallback.onPress();
        };
    }

    public void setOnRelease(ReleaseCallback onReleaseCallback) {
        this.onReleaseCallback = () -> {
            if (!this.toggleType)
                setClicked(false);

            updateTexturePosition();
            onReleaseCallback.onRelease();
        };
    }

    @Override
    public void setOnLeave(LeaveCallback onLeaveCallback) {
        LeaveCallback basicLeaveCallback = () -> {
            //System.out.println("Leave");
            if (!isClicked() || !this.toggleType) {
                setFilterTransparency(0);
                if (this.tooltipGui != null) {
                    this.tooltipGui.setDisplayed(false);
                }
            }
            onLeaveCallback.onLeave();
        };

        super.setOnLeave(basicLeaveCallback);
    }

    public void onRelease() {
        if (this.onReleaseCallback == null)
            return;

        this.onReleaseCallback.onRelease();
    }

    /**
     * Click + release within component
     */
    public void onPress() {
        if (this.onPressCallback == null)
            return;

        this.onPressCallback.onPress();
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
            if (!isClicked() || !this.toggleType) {
                setFilterTransparency(1);

                if (this.tooltipGui != null) {
                    this.tooltipGui.setDisplayed(true);
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

        if (!this.toggleType && this.filterLayout != null) {
            if (clicked)
                this.filterLayout.scale(1 - SCALE_FACTOR);
            else
                resetFilter();
        }
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

    private void setTooltipGui(Text text) {
        if (text == null)
            return;

        this.tooltipGui = new GuiRectangle(this, new Background<>(new Color(64, 64, 64, 100)));
        this.tooltipGui.setConstraints(new GuiConstraintsManager.Builder()
                .setWidthConstraint(new RelativeConstraint(1))
                .setHeightConstraint(new PixelConstraint(30))
                .setxConstraint(new RelativeConstraint(0))
                .setyConstraint(new StickyConstraint(Side.TOP, 0.05f)).create());
        GuiText guiText = Gui.setupText(this.tooltipGui, text);
        guiText.setDisplayed(false);
        this.tooltipGui.addComponent(guiText);
        this.tooltipGui.setDisplayedByDefault(false);
    }

    @Override
    public RawModel getTemplate() {
        return this.buttonShape.getTemplate();
    }

    public GuiText getText() {
        return this.text;
    }

    public GuiRectangle getTooltipGui() {
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

    public void resetFilter() {
        if (this.filterLayout != null) {
            this.filterLayout.scale(1 / (1 - SCALE_FACTOR));
        }
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

    public boolean isReleaseInsideNeeded() {
        return this.releaseInsideNeeded;
    }

    public void setReleaseInsideNeeded(boolean releaseInsideNeeded) {
        this.releaseInsideNeeded = releaseInsideNeeded;
    }

    public enum ButtonType {
        RECTANGLE,
        CIRCULAR,
        TRIANGLE
    }

    @Override
    public void setDisplayed(boolean displayed) {
        super.setDisplayed(displayed);

        if (!displayed && this.clicked)
            setClicked(false);
    }

    public GuiShape getButtonShape() {
        return this.buttonShape;
    }
}
