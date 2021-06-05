package guis.constraints;

import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;

public class PatternGlobalConstraint extends GuiGlobalConstraints {

    private final GuiConstraintsManager guiConstraintsManager;
    private final float                 itemWidth;
    private final float                 itemHeight;

    public PatternGlobalConstraint(int horizontal, int vertical, float distance) {
        this(horizontal, vertical, distance, distance);
    }

    public PatternGlobalConstraint(int horizontal, int vertical, float distanceX, float distanceY) {
        super(ConstraintsType.POSITION, Constraints.PATTERN, horizontal, vertical, distanceX, distanceY);

        assert horizontal > 0;
        assert vertical > 0;
        assert distanceX >= 0;
        assert distanceY >= 0;

        this.guiConstraintsManager = new GuiConstraintsManager();

        this.itemWidth = (1 - distanceX * (horizontal + 1)) / horizontal;
        this.itemHeight = (1 - distanceY * (vertical + 1)) / vertical;
    }

    @Override
    protected GuiConstraintsManager addElement() {
        Object[] arguments = getArguments();
        int maxHorizontalElements = (int) arguments[0];
        int maxVerticalElements = (int) arguments[1];

        if (this.nbElements >= maxHorizontalElements * maxVerticalElements)
            throw new IllegalArgumentException(
                    "Full space: " + " nbElements = " + this.nbElements + " >= " +
                            (maxHorizontalElements * maxVerticalElements));

        float distanceX = (float) arguments[2];
        float distanceY = arguments.length > 3 ? (float) arguments[3] : distanceX;

        this.guiConstraintsManager.setWidthConstraint(new RelativeConstraint(this.itemWidth, this.parent));
        this.guiConstraintsManager.setHeightConstraint(new RelativeConstraint(this.itemHeight, this.parent));

        int row = this.nbElements / maxHorizontalElements;
        int nbElementsOnRow = this.nbElements % maxHorizontalElements;

        float x;
        if (maxHorizontalElements == 1)
            x = this.parent.getX();
        else
            x = -1 + distanceX * 2 * (nbElementsOnRow + 1) +
                    this.itemWidth * maxHorizontalElements * 2 / (maxHorizontalElements - 1) * nbElementsOnRow;

        float y;
        if (maxVerticalElements > 1)
            y = -1 + (distanceY * 2 * (row + 1) +
                    this.itemHeight * maxVerticalElements * 2 / (maxVerticalElements - 1) * row);
        else
            y = this.parent.getY();

        this.guiConstraintsManager.setxConstraint(new RelativeConstraint((x + 1) / 2));
        this.guiConstraintsManager.setyConstraint(new RelativeConstraint((y + 1) / 2));

        this.nbElements++;

        return this.guiConstraintsManager;
    }
}
