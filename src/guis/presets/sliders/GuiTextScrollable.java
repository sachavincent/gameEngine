package guis.presets.sliders;

import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.basics.GuiText;
import guis.constraints.CenterConstraint;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.constraints.SideConstraint;
import util.Utils;
import util.math.Maths;

public class GuiTextScrollable extends GuiText {

    private final GuiVerticalSlider scrollBar;
    private final double            minScrollbarSize;

    private double scrollingSpeed;

    private boolean autoScrolling;
    private float   maxScrollingValue;

    private int nbLines;

    public GuiTextScrollable(GuiInterface parent, Text text, ScrollBar scrollBar) {
        this(parent, text, null, scrollBar);
    }

    public GuiTextScrollable(GuiInterface parent, Text text, GuiConstraintsManager guiConstraintsManager,
            ScrollBar scrollBar) {
        super(parent, text, guiConstraintsManager);
        GuiConstraints dimensionConstraint = scrollBar.getDimensionConstraint();
        dimensionConstraint.setRelativeTo(this);
        this.minScrollbarSize = scrollBar.getMinSize();
        Interval interval = new Interval(0, 0, 1, 1);
        this.maxScrollingValue = 1;
        this.scrollBar = new GuiVerticalSlider(this, interval, Utils.setAlphaColor(scrollBar.getColor(), 100),
                scrollBar.getColor(),
                new GuiConstraintsManager.Builder()
                        .setWidthConstraint(dimensionConstraint)
                        .setHeightConstraint(new RelativeConstraint(.95f))
                        .setyConstraint(new CenterConstraint())
                        .setxConstraint(new SideConstraint(scrollBar.getSide(), 0))
                        .create());
        this.scrollBar.setCursorHeight(new RelativeConstraint(1));
        this.scrollBar.disableSliding();
        this.scrollBar.setOnValueChanged(value -> {
            this.text.setyOffset(-(int) (value));
        });
        setOnScroll((xOffset, yOffset) -> {
            if (this.scrollBar.isSlidingEnabled())
                this.scrollBar.setValue((float) (this.scrollBar.getValue() + yOffset * this.scrollingSpeed));
        });
    }

    @Override
    public boolean update() {
        if (this.scrollBar != null) {
            this.maxScrollingValue = 1 +
                    (float) (this.text.getNumberOfLines() - (getHeight() * 2 / this.text.getLineTextHeight()));
            this.scrollBar.setInterval(new Interval(this.scrollBar.getValue(), 0, this.maxScrollingValue, 1));
            if (this.autoScrolling && this.scrollBar.isSlidingEnabled() && this.nbLines != this.text.getNumberOfLines())
                this.scrollBar.setValue(-this.maxScrollingValue);
        }
        if (this.text != null)
            this.nbLines = this.text.getNumberOfLines();
        return super.update();
    }

    public void setText(Text text) {
        if (text == null)
            return;

        text.setMaxLineLength(getWidth());

        double textHeight = text.getTotalTextHeight();

        float parentHeight = getHeight() * 2;
        if (textHeight > parentHeight) {
            enableScrolling();
            float percentage = (float) ((textHeight - parentHeight) / parentHeight) / 1.6f;
            float returnValue = 1 - percentage;
            this.scrollBar.setCursorHeight(
                    new RelativeConstraint(
                            (float) Maths.clamp(returnValue, this.minScrollbarSize / 100f, 1)));
        }

        //TODO: Handle horizontal scrolling

        this.text = text;

        update();
    }

    public double getScrollingSpeed() {
        return this.scrollingSpeed;
    }

    public void setScrollingSpeed(double scrollingSpeed) {
        this.scrollingSpeed = scrollingSpeed;
    }

    private void enableScrolling() {
        this.scrollBar.enableSliding();
        this.scrollBar.setDisplayed(true);
    }

    public void setAutoScrolling(boolean autoScrolling) {
        this.autoScrolling = autoScrolling;
    }

    public boolean isAutoScrollingEnabled() {
        return this.autoScrolling;
    }

    @Override
    public void render() {
        super.render();
    }
}
