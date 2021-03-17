package guis.prefabs.GuiHouseDetails;

import static guis.prefabs.GuiHouseDetails.GuiHouseDetails.DEFAULT_BACKGROUND;

import guis.basics.GuiRectangle;
import guis.constraints.CenterConstraint;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RatioedPatternGlobalConstraint;
import guis.constraints.RelativeConstraint;
import guis.constraints.Side;
import guis.constraints.StickyConstraint;
import guis.presets.buttons.GuiAbstractButton;
import guis.presets.buttons.GuiRectangleButton;
import resources.ResourceManager.Resource;

/**
 * Left tab containing all categories
 */
public class CategoriesTab extends GuiRectangle {

    public GuiAbstractButton foodCatButton;
    public GuiAbstractButton drinksCatButton;
    public GuiAbstractButton socialCatButton;

    private final static GuiConstraintsManager CONSTRAINTS = new GuiConstraintsManager.Builder()
            .setWidthConstraint(new RelativeConstraint(.47f))
            .setHeightConstraint(new RelativeConstraint(.6f))
            .setxConstraint(new StickyConstraint(Side.LEFT).setDistanceFromSide(0))
            .setyConstraint(new CenterConstraint())
            .create();

    public CategoriesTab(GuiHouseDetails gui) {
        super(gui, DEFAULT_BACKGROUND, CONSTRAINTS);
        setChildrenConstraints(new RatioedPatternGlobalConstraint(1, 3, 0, 0, -1f, 33.33f, -1f, 33.33f, -1f, 33.33f));

        addFoodButton();
        addDrinksButton();
        addSocialButton();
    }

    private void addDrinksButton() {
        this.drinksCatButton = new GuiRectangleButton(this, DEFAULT_BACKGROUND, null, null, null);

        this.drinksCatButton.setOnPress(() -> ((GuiHouseDetails) getParent()).onDrinksCategoryClick());
    }

    private void addSocialButton() {
        this.socialCatButton = new GuiRectangleButton(this, DEFAULT_BACKGROUND, null, null, null);

        this.socialCatButton.setOnPress(() -> ((GuiHouseDetails) getParent()).onSocialCategoryClick());
    }

    private void addFoodButton() {
        this.foodCatButton = new GuiRectangleButton(this, Resource.FISH.getTexture(), null, null, null);

        this.foodCatButton.setOnPress(() -> ((GuiHouseDetails) getParent()).onFoodCategoryClick());
    }
}
