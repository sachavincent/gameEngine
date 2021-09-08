package guis.presets.buttons;

import fontMeshCreator.Text;
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
import guis.presets.GuiAbstractShapePreset;
import guis.presets.GuiClickablePreset;
import guis.presets.GuiPreset;
import inputs.ClickType;
import inputs.callbacks.EnterCallback;
import inputs.callbacks.LeaveCallback;
import inputs.callbacks.MousePressCallback;
import inputs.callbacks.MouseReleaseCallback;
import java.awt.Color;
import models.RawModel;

public abstract class GuiAbstractButton extends GuiPreset implements GuiAbstractShapePreset, GuiClickablePreset {

    private static final float FILTER_TRANSPARENCY = 0.2f;
    private static final float SCALE_FACTOR        = 0.05f;

    protected GuiBasics filterLayout;
    protected GuiShape  shape;

    private GuiText text;

    private GuiRectangle tooltipGui;
    private boolean      toggleType = false;

    private ButtonGroup          buttonGroup;
    private MousePressCallback   onMousePressCallback;
    private MouseReleaseCallback onMouseReleaseCallback;
    public  ClickType            clickType;

    private boolean releaseInsideNeeded = true; // If button needs to be released inside of this component

    GuiAbstractButton(GuiInterface parent, Background<?> background, Color borderColor, Text text,
            GuiConstraintsManager constraintsManager) {
        this(parent, background, borderColor, constraintsManager);

        setupText(text);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Color borderColor, Text text, Text tooltipGui,
            GuiConstraintsManager constraintsManager) {
        this(parent, background, borderColor, text, constraintsManager);

        setTooltipGui(tooltipGui);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Color borderColor,
            GuiConstraintsManager constraintsManager) {
        super(parent, Background.NO_BACKGROUND, constraintsManager);

        this.shape = createShape(background, borderColor);
        setupComponents();

        this.clickType = ClickType.NONE;

        setOnClose(() -> {
            if (this.clickType != ClickType.NONE && this.buttonGroup.getMaxButtons() == 1) {
                setClickType(ClickType.NONE);
                resetButton();
            }
        });
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Color borderColor, Text text) {
        this(parent, background, borderColor, text, (GuiConstraintsManager) null);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Color borderColor, Text text,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        this(parent, background, borderColor, text, constraintsManager);

        setCornerRadius(cornerRadius);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Color borderColor, Text text, Text tooltipGui,
            GuiConstraintsManager constraintsManager, int cornerRadius) {
        this(parent, background, borderColor, text, constraintsManager, cornerRadius);

        if (tooltipGui != null) {
            setTooltipGui(tooltipGui);
            this.tooltipGui.setCornerRadius(cornerRadius);
        }
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Color borderColor, Text text, Text tooltipGui,
            int cornerRadius) {
        this(parent, background, borderColor, text, tooltipGui, null, cornerRadius);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Color borderColor, Text text, Text tooltipGui) {
        this(parent, background, borderColor, text, tooltipGui, null);
    }

    GuiAbstractButton(GuiInterface parent, Background<?> background, Color borderColor,
            GuiConstraintsManager constraintsManager,
            int cornerRadius) {
        this(parent, background, borderColor, null, null, constraintsManager, cornerRadius);
    }

    private void setupComponents() {
        setListeners();

        setButtonGroup(new ButtonGroup(1));

        setDisplayedComponents(false);
    }

    public void setDisplayedComponents(boolean displayed) {
        this.setReleaseInsideNeeded(false);

        if (this.filterLayout != null)
            this.filterLayout.setDisplayed(displayed);
        if (this.text != null)
            this.text.setDisplayed(displayed);
        if (this.tooltipGui != null)
            this.tooltipGui.setDisplayed(displayed);
    }

    public void setupText(Text text) {
        if (text == null)
            return;

        this.text = new GuiText(this, text.copy());
    }

    @Override
    public void setX(float x) {
        super.setX(x);

        if (this.text != null)
            this.text.setX(getX());
        if (this.shape != null)
            this.shape.setX(getX());
    }

    @Override
    public void setY(float y) {
        super.setY(y);

        if (this.text != null)
            this.text.setY(y);
        if (this.shape != null)
            this.shape.setY(getY());
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);

        if (this.text != null)
            this.text.setWidth(width);
        if (this.shape != null)
            this.shape.setWidth(width);
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);

        if (this.text != null)
            this.text.setHeight(height);
        if (this.shape != null)
            this.shape.setHeight(height);
    }

    @Override
    public void setOnMousePress(MousePressCallback onMousePressCallback) {
        this.onMousePressCallback = button -> {
            if (!this.toggleType || !isClicked())
                setClickType(ClickType.getClickTypeFromButton(button));
            else if (this.toggleType && button == this.clickType.getButton() && this.buttonGroup.getMaxButtons() == 1)
                setClickType(ClickType.NONE);

            updateTexturePosition();
            return onMousePressCallback.onPress(button);
        };
    }

    @Override
    public void setOnMouseRelease(MouseReleaseCallback onMouseReleaseCallback) {
        this.onMouseReleaseCallback = button -> {
            if (!this.toggleType)
                setClickType(ClickType.NONE);

            updateTexturePosition();
            return onMouseReleaseCallback.onRelease(button);
        };
    }

    @Override
    public void setOnLeave(LeaveCallback onLeaveCallback) {
        LeaveCallback basicLeaveCallback = () -> {
            //System.out.println("Leave");
            resetButton();
            onLeaveCallback.onLeave();
        };

        super.setOnLeave(basicLeaveCallback);
    }

    private void resetButton() {
        if (this.clickType == ClickType.NONE || !this.toggleType) {
            setFilterTransparency(0);
            if (this.tooltipGui != null) {
                this.tooltipGui.setDisplayed(false);
            }
        }
    }

    @Override
    public boolean onMouseRelease(int button) {
        if (this.onMouseReleaseCallback == null)
            return false;

        return this.onMouseReleaseCallback.onRelease(button);
    }

    /**
     * Click + release within component
     */
    @Override
    public boolean onMousePress(int button) {
        if (this.onMousePressCallback == null)
            return false;

        return this.onMousePressCallback.onPress(button);
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
            if (this.clickType == ClickType.NONE || !this.toggleType) {
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
    public boolean isClicked() {
        return this.clickType != ClickType.NONE;
    }

    @Override
    public ClickType getClickType() {
        return this.clickType;
    }

    @Override
    public void setClickType(ClickType clickType) {
        if (clickType != ClickType.NONE)
            this.buttonGroup.setButtonClicked(clickType, ID);
        else
            this.clickType = ClickType.NONE;

        if (!this.toggleType && this.filterLayout != null) {
            if (clickType != ClickType.NONE)
                this.filterLayout.scale(1 - SCALE_FACTOR);
            else
                reset();
        }
    }

    private void setListeners() {
        setOnEnter(() -> {
        });
        setOnLeave(() -> {
        });
        setOnMouseRelease(button -> {
            return false;
        });
        setOnMousePress(button -> {
            return false;
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
//        GuiText guiText = Gui.setupText(this.tooltipGui, text);
        GuiText guiText = new GuiText(this.tooltipGui, text);
        guiText.setDisplayed(false);
        this.tooltipGui.addComponent(guiText);
        this.tooltipGui.setDisplayedByDefault(false);
    }

    @Override
    public RawModel getTemplate() {
        return this.shape.getTemplate();
    }

    public GuiText getText() {
        return this.text;
    }

    public GuiRectangle getTooltipGui() {
        return this.tooltipGui;
    }

    public void enableFilter() {
        setFilter();
        this.filterLayout.getTexture().setFinalAlpha(FILTER_TRANSPARENCY); // Filter on hover
        setFilterTransparency(0f);
    }

    public GuiBasics getFilter() {
        return this.filterLayout;
    }

    protected abstract void setFilter();

    @Override
    public final void reset() {
        if (this.filterLayout != null && !this.toggleType) {
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

    @Override
    public boolean isReleaseInsideNeeded() {
        return this.releaseInsideNeeded;
    }

    @Override
    public void setReleaseInsideNeeded(boolean releaseInsideNeeded) {
        this.releaseInsideNeeded = releaseInsideNeeded;
    }

    @Override
    public GuiShape getShape() {
        return this.shape;
    }
}
