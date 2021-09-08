package guis.constraints.layout;

import display.Display;
import guis.constraints.CenterConstraint;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.GuiConstraintsManager.Constraints;
import guis.constraints.GuiConstraintsManager.ConstraintsType;
import guis.constraints.RelativeConstraint;
import guis.constraints.Side;
import guis.constraints.SideConstraint;
import guis.constraints.StickyConstraint;
import java.util.Arrays;
import java.util.stream.Stream;

public class RatioedPatternLayout extends GuiLayout {

    private float xPercentageTaken;
    private float yPercentageTaken;

    /**
     * @param percentages x and y list
     * if any < 0 then Aspect ratio constraint used
     */
    public RatioedPatternLayout(int horizontal, int vertical, float distanceX, float distanceY,
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

        if (this.nbElements >= maxHorizontalElements * maxVerticalElements)
            throw new IllegalArgumentException(
                    "Full space: " + " nbElements = " + this.nbElements + " >= " +
                            (maxHorizontalElements * maxVerticalElements));

        float distanceX = (float) arguments[2];
        float distanceY = (float) arguments[3];

        GuiConstraintsManager guiConstraintsManager = new GuiConstraintsManager();

//        float itemWidth = (1 - distance * (maxHorizontalElements + 1)) / maxHorizontalElements;
//        float itemHeight = (1 - distance * (maxVerticalElements + 1)) / maxVerticalElements;
        float arg1Float;
        float arg2Float;
        Object arg1 = arguments[this.nbElements * 2 + 4];
        Object arg2 = arguments[this.nbElements * 2 + 5];
        if (arg1 instanceof Integer)
            arg1Float = (int) arg1;
        else
            arg1Float = (float) arg1;
        if (arg2 instanceof Integer)
            arg2Float = (int) arg2;
        else
            arg2Float = (float) arg2;
        float xPercentage = arg1Float / 100;
        float yPercentage = arg2Float / 100;

        float itemSizeWidth = getItemSize(maxHorizontalElements, distanceX, xPercentage < 0 ? 1 : xPercentage);
        float itemSizeHeight = getItemSize(maxVerticalElements, distanceY, yPercentage < 0 ? 1 : yPercentage);

        float lostWidth = 0;
        float lostHeight = 0;
        float itemHeight;
        float itemWidth;
        if (xPercentage < 0) { // Aspect Needed
            itemHeight = itemSizeHeight;
            itemWidth = (-xPercentage * 100 * Display.getWindow().getHeight() / Display.getWindow().getWidth() *
                    itemHeight * this.parent.getHeight()) / this.parent.getWidth();

            lostWidth = itemSizeWidth - itemWidth;
            if (itemWidth > 1) {
                itemHeight /= itemWidth;

                itemWidth = 1;
            }
            xPercentage = itemWidth; //TODO not sure this works
        } else if (yPercentage < 0) { // Aspect Needed
            itemWidth = itemSizeWidth;
            itemHeight =
                    (-yPercentage * 100 * Display.getWindow().getWidth() / Display.getWindow().getHeight() * itemWidth *
                            this.parent.getWidth()) / this.parent.getHeight();

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


        if (this.xPercentageTaken + xPercentage > 1) {
            this.xPercentageTaken = 0;
            this.yPercentageTaken += yPercentage;
        }

        float distanceFromSideX = Math.max(0, distanceX + lostWidth / 2);
        float distanceFromSideY = Math.max(0, distanceY + lostHeight / 2);
        if (distanceFromSideY == 0 && itemHeight < 1 && maxVerticalElements == 1)
            distanceFromSideY = (1 - itemHeight) / 2;
        if (distanceFromSideX == 0 && itemWidth < 1 && maxHorizontalElements == 1)
            distanceFromSideX = (1 - itemWidth) / 2;

//        float distanceFromSideX = Math.max(0, distance + lostHeight *  Display.getWindow().getWidth() /  Display.getWindow().getHeight() / 2);
//        float distanceFromSideY = Math.max(0, distance + lostWidth *  Display.getWindow().getHeight() /  Display.getWindow().getWidth() / 2);

        if (this.xPercentageTaken == 0) {
            float percentage = xPercentage;
            int i = 1;
            while (percentage < 1) {
                int index = (this.nbElements + i) * 2 + 4;
                if (arguments.length <= index)
                    break;
                Object arg = arguments[index];
                float p;
                if (arg instanceof Integer)
                    p = (int) arg / 100f;
                else
                    p = (float) arg / 100f;
                percentage += p;
                i++;
            }
            if (percentage < 1) {
                distanceFromSideX = (1 - percentage) / 2;
            }
            if (maxHorizontalElements > 1)
                guiConstraintsManager
                        .setxConstraint(new SideConstraint(Side.LEFT).setDistanceFromSide(distanceFromSideX));
            else
                guiConstraintsManager.setxConstraint(new CenterConstraint());
        }

        if (this.yPercentageTaken == 0) {
            float percentage = yPercentage;
            int i = 1;
            while (percentage < 1) {
                int index = (this.nbElements + i) * 2 + 5;
                if (arguments.length <= index)
                    break;
                Object arg = arguments[index];
                float p;
                if (arg instanceof Integer)
                    p = (int) arg / 100f;
                else
                    p = (float) arg / 100f;
                percentage += p;
                i++;
            }
            if (percentage < 1) {
                distanceFromSideY = (1 - percentage) / 2;
            }
            if (maxVerticalElements > 1)
                guiConstraintsManager
                        .setyConstraint(new SideConstraint(Side.TOP).setDistanceFromSide(distanceFromSideY));
            else
                guiConstraintsManager.setyConstraint(new CenterConstraint());
        }

        if (this.xPercentageTaken > 0)
            guiConstraintsManager.setxConstraint(
                    new StickyConstraint(Side.RIGHT, this.components.get((this.nbElements - 1) % maxHorizontalElements))
                            .setDistanceFromSide(distanceFromSideX));

        if (this.yPercentageTaken > 0)
            guiConstraintsManager.setyConstraint(
                    new StickyConstraint(Side.BOTTOM, this.components.get((this.nbElements - 1) % maxVerticalElements))
                            .setDistanceFromSide(distanceFromSideY));

//        guiConstraintsManager.setxConstraint(new RelativeConstraint(x));
//        guiConstraintsManager.setyConstraint(new RelativeConstraint(y));

        this.nbElements++;
        this.xPercentageTaken += xPercentage;

        assert this.xPercentageTaken >= 0 && this.xPercentageTaken <= 1;

        if (this.xPercentageTaken == 1 || this.nbElements % maxHorizontalElements == 0) {
            this.xPercentageTaken = 0;
            this.yPercentageTaken += yPercentage;
        }
        assert this.yPercentageTaken >= 0 && this.yPercentageTaken <= 1;

        return guiConstraintsManager;
    }

    private float getItemSize(int maxItems, double distance, double percentagePer) {
        double totalDistance = distance * (maxItems - 1);
        return (float) ((1 - totalDistance) * percentagePer);
    }
}
