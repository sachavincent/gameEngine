package guis.constraints;

import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;
import java.util.Arrays;
import java.util.stream.Stream;
import renderEngine.DisplayManager;

public class RatioedPatternGlobalConstraint extends GuiGlobalConstraints {

    private float xPercentageTaken;
    private float yPercentageTaken;

    /**
     * @param percentages x and y list
     * if any < 0 then Aspect ratio constraint used
     */
    public RatioedPatternGlobalConstraint(int horizontal, int vertical, float distanceX, float distanceY,
            Number... percentages) {
        super(ConstraintsType.POSITION, Constraints.RATIOED_PATTERN,
                Stream.concat(Arrays.stream(new Object[]{horizontal, vertical, distanceX, distanceY}),
                        Arrays.stream(percentages)).toArray());

        assert horizontal > 0;
        assert vertical > 0;
        assert distanceX >= 0;
        assert distanceY >= 0;
        assert percentages.length % 2 == 0;
        assert percentages.length / (horizontal * vertical) == 2;
    }

    @Override
    protected GuiConstraintsManager addElement() {
        Object[] arguments = getArguments();
        int maxHorizontalElements = (int) arguments[0];
        int maxVerticalElements = (int) arguments[1];

        if (nbElements >= maxHorizontalElements * maxVerticalElements)
            throw new IllegalArgumentException(
                    "Full space: " + " nbElements = " + nbElements + " >= " +
                            (maxHorizontalElements * maxVerticalElements));

        float distanceX = (float) arguments[2];
        float distanceY = (float) arguments[3];

        GuiConstraintsManager guiConstraintsManager = new GuiConstraintsManager();

//        float itemWidth = (1 - distance * (maxHorizontalElements + 1)) / maxHorizontalElements;
//        float itemHeight = (1 - distance * (maxVerticalElements + 1)) / maxVerticalElements;
        float xPercentage = (float) arguments[nbElements * 2 + 4] / 100;
        float yPercentage = (float) arguments[nbElements * 2 + 5] / 100;

        float itemSizeWidth = getItemSize(maxHorizontalElements, distanceX, xPercentage < 0 ? 1 : xPercentage);
        float itemSizeHeight = getItemSize(maxVerticalElements, distanceY, yPercentage < 0 ? 1 : yPercentage);

        float lostWidth = 0;
        float lostHeight = 0;
        float itemHeight;
        float itemWidth;
        if (xPercentage < 0) { // Aspect Needed
            itemHeight = itemSizeHeight;
            itemWidth = (-xPercentage * 100 * DisplayManager.HEIGHT / DisplayManager.WIDTH * itemHeight *
                    parent.getHeight()) / parent.getWidth();

            lostWidth = itemSizeWidth - itemWidth;
            if (itemWidth > 1) {
                itemHeight /= itemWidth;

                itemWidth = 1;
            }
            xPercentage = itemWidth; //TODO not sure this works
        } else if (yPercentage < 0) { // Aspect Needed
            itemWidth = itemSizeWidth;
            itemHeight = (-yPercentage * 100 * DisplayManager.WIDTH / DisplayManager.HEIGHT * itemWidth *
                    parent.getWidth()) / parent.getHeight();

            lostHeight = itemSizeHeight - itemHeight;
            if (itemHeight > 1) {
                itemWidth /= itemHeight;

                itemHeight = 1;
            }
            yPercentage = itemHeight; //TODO not sure this works
        } else {
            itemWidth = itemSizeWidth;
            itemHeight = itemSizeHeight;
        }

        guiConstraintsManager.setWidthConstraint(new RelativeConstraint(itemWidth));
        guiConstraintsManager.setHeightConstraint(new RelativeConstraint(itemHeight));


        if (xPercentageTaken + xPercentage > 1) {
            xPercentageTaken = 0;
            yPercentageTaken += yPercentage;
        }

        float distanceFromSideX = Math.max(0, distanceX + lostWidth / 2);
        float distanceFromSideY = Math.max(0, distanceY + lostHeight / 2);
        if (distanceFromSideY == 0 && itemHeight < 1 && maxVerticalElements == 1)
            distanceFromSideY = (1 - itemHeight) / 2;
        if (distanceFromSideX == 0 && itemWidth < 1 && maxHorizontalElements == 1)
            distanceFromSideX = (1 - itemWidth) / 2;

//        float distanceFromSideX = Math.max(0, distance + lostHeight * DisplayManager.WIDTH / DisplayManager.HEIGHT / 2);
//        float distanceFromSideY = Math.max(0, distance + lostWidth * DisplayManager.HEIGHT / DisplayManager.WIDTH / 2);

        if (xPercentageTaken == 0) {
            float percentage = xPercentage;
            int i = 1;
            while (percentage < 1) {
                int index = (nbElements + i) * 2 + 4;
                if (arguments.length <= index)
                    break;
                float p = (float) arguments[index] / 100;
                percentage += p;
                i++;
            }
            if (percentage < 1) {
                distanceFromSideX = (1 - percentage) / 2;
            }
            guiConstraintsManager.setxConstraint(new SideConstraint(Side.LEFT).setDistanceFromSide(distanceFromSideX));
        }

        if (yPercentageTaken == 0) {
            float percentage = yPercentage;
            int i = 1;
            while (percentage < 1) {
                int index = (nbElements + i) * 2 + 5;
                if (arguments.length <= index)
                    break;
                float p = (float) arguments[index] / 100;
                percentage += p;
                i++;
            }
            if (percentage < 1) {
                distanceFromSideY = (1 - percentage) / 2;
            }
            guiConstraintsManager.setyConstraint(new SideConstraint(Side.TOP).setDistanceFromSide(distanceFromSideY));
        }

        if (xPercentageTaken > 0)
            guiConstraintsManager.setxConstraint(
                    new StickyConstraint(Side.RIGHT, components.get((nbElements - 1) % maxHorizontalElements))
                            .setDistanceFromSide(distanceFromSideX));

        if (yPercentageTaken > 0)
            guiConstraintsManager.setyConstraint(
                    new StickyConstraint(Side.BOTTOM, components.get((nbElements - 1) % maxVerticalElements))
                            .setDistanceFromSide(distanceFromSideY));

//        guiConstraintsManager.setxConstraint(new RelativeConstraint(x));
//        guiConstraintsManager.setyConstraint(new RelativeConstraint(y));

        nbElements++;
        xPercentageTaken += xPercentage;

        assert xPercentageTaken >= 0 && xPercentageTaken <= 1;

        if (xPercentageTaken == 1 || nbElements % maxHorizontalElements == 0) {
            xPercentageTaken = 0;
            yPercentageTaken += yPercentage;
        }
        assert yPercentageTaken >= 0 && yPercentageTaken <= 1;

        return guiConstraintsManager;
    }

    private float getItemSize(int maxItems, double distance, double percentagePer) {
        double totalDistance = distance * (maxItems + 1);
        return (float) ((1 - totalDistance) * percentagePer);
    }
}
