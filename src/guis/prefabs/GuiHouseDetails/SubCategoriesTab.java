package guis.prefabs.GuiHouseDetails;

import guis.basics.GuiRectangle;
import guis.constraints.CenterConstraint;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.constraints.Side;
import guis.constraints.SideConstraint;

/**
 * Bottom tab containing all sub-categories
 */
public class SubCategoriesTab extends GuiRectangle {

    private static final GuiConstraintsManager CONSTRAINTS = new GuiConstraintsManager.Builder()
            .setWidthConstraint(new RelativeConstraint(1))
            .setHeightConstraint(new RelativeConstraint(.2f))
            .setxConstraint(new CenterConstraint())
            .setyConstraint(new SideConstraint(Side.BOTTOM).setDistanceFromSide(0))
            .create();

    public SubCategoriesTab(GuiHouseDetails gui) {
        super(gui, GuiHouseDetails.DEFAULT_BACKGROUND, CONSTRAINTS);
    }
}
