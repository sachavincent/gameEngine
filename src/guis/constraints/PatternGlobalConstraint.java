package guis.constraints;

import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;

public class PatternGlobalConstraint extends GuiGlobalConstraints {

    public PatternGlobalConstraint(int horizontal, int vertical, float distance) {
        super(ConstraintsType.POSITION, Constraints.PATTERN, horizontal, vertical, distance);

        assert horizontal > 0;
        assert vertical > 0;
        assert distance >= 0;
    }

    @Override
    protected GuiConstraintsManager addElement() {
        Object[] arguments = getArguments();
        int maxHorizontalElements = (int) arguments[0];
        int maxVerticalElements = (int) arguments[1];

        if (nbElements >= maxHorizontalElements * maxVerticalElements)
            throw new IllegalArgumentException(
                    "Full space: " + " nbElements = " + nbElements + " >= " + (maxHorizontalElements * maxVerticalElements));

        float distance = (float) arguments[2];

        GuiConstraintsManager guiConstraintsManager = new GuiConstraintsManager();

        float itemWidth = (1 - distance * (maxHorizontalElements + 1)) / maxHorizontalElements;
        float itemHeight = (1 - distance * (maxVerticalElements + 1)) / maxVerticalElements;
        guiConstraintsManager.setWidthConstraint(new RelativeConstraint(itemWidth, parent));
        guiConstraintsManager.setHeightConstraint(new RelativeConstraint(itemHeight, parent));

        int row = nbElements / maxHorizontalElements;
        int nbElementsOnRow = nbElements % maxHorizontalElements;
        float x = -1 + distance * 2 * (nbElementsOnRow + 1) + itemWidth * 2.5f * nbElementsOnRow;
        float y = -1 + (distance * 2 * (row + 1) + itemHeight * 3 * row);
        guiConstraintsManager.setxConstraint(new RelativeConstraint(x, parent));
        guiConstraintsManager.setyConstraint(new RelativeConstraint(y, parent));

        nbElements++;

        return guiConstraintsManager;
    }
}