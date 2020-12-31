package guis.prefabs;

import fontMeshCreator.Text;
import guis.Gui;
import guis.GuiTexture;
import guis.basics.GuiEllipse;
import guis.basics.GuiRectangle;
import guis.basics.GuiText;
import guis.constraints.*;
import guis.presets.Background;
import guis.presets.buttons.GuiAbstractButton;
import guis.presets.buttons.GuiAbstractButton.ButtonType;
import guis.presets.graphs.GuiDonutGraph;
import guis.presets.graphs.GuiDonutGraph.Sector;
import items.buildings.houses.HouseItem;
import java.awt.Color;
import language.Words;

public class GuiHouseDetails extends Gui {

    private final static GuiConstraints[] DEFAULT_DIMENSIONS = new GuiConstraints[]{
            new RelativeConstraint(.17f), new AspectConstraint(1.2f)};

    //    private final static GuiBackground<?> DEFAULT_BACKGROUND = new GuiBackground<>(new Color(255, 255, 255, 0));
    private final static Background<?> DEFAULT_BACKGROUND = new Background<>("gui2.png");

    private static GuiHouseDetails instance;

    private final GuiRectangle overallView;

    private final GuiTexture[] categoryIconsArray = new GuiTexture[CategoryState.values().length];
    private final GuiRectangle categoryView;
    private final GuiText      categoryPercentage;

    private final GuiRectangle peopleTab;
    private final GuiRectangle moneyTab;
    private final GuiRectangle categoriesTab;

    private final GuiRectangle categoryIcon;

    private final GuiConstraints upperWidthConstraint;

    private GuiAbstractButton peopleButton;
    private GuiAbstractButton moneyButton;

    private GuiAbstractButton foodCatButton;
    private GuiAbstractButton drinksCatButton;
    private GuiAbstractButton socialCatButton;

    private HouseItem houseItem;

    private State         state;
    private CategoryState categoryState;

    private GuiRectangle outlineRectangle, outlineRectangle2;

    private final static Background<String> stickyFigureImage = new Background<>("stick_figure.png");
    private final static Background<String> coinsImage        = new Background<>("coins.png");

    private GuiDonutGraph<Integer> peopleDistributionGraph;
    private GuiRectangle           stickyFigure;

    private GuiHouseDetails() {
        super(DEFAULT_BACKGROUND);

        GuiConstraintsManager menuConstraints = new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(DEFAULT_DIMENSIONS[0])
                .setHeightConstraint(DEFAULT_DIMENSIONS[1])
                .create();

        setConstraints(menuConstraints);

        GuiConstraintsManager upperRectangleConstraints = new GuiConstraintsManager.Builder()
                .setWidthConstraint(new RelativeConstraint(1))
                .setHeightConstraint(new RelativeConstraint(.2f))
                .setxConstraint(new CenterConstraint())
                .setyConstraint(new SideConstraint(Side.TOP).setDistanceFromSide(0))
                .create();

        GuiConstraintsManager centerRectangleConstraints = new GuiConstraintsManager.Builder()
                .setWidthConstraint(new RelativeConstraint(1))
                .setHeightConstraint(new RelativeConstraint(.6f))
                .setxConstraint(new CenterConstraint())
                .setyConstraint(new CenterConstraint())
                .create();

        GuiConstraintsManager lowerRectangleConstraints = new GuiConstraintsManager.Builder()
                .setWidthConstraint(new RelativeConstraint(1))
                .setHeightConstraint(new RelativeConstraint(.2f))
                .setxConstraint(new CenterConstraint())
                .setyConstraint(new SideConstraint(Side.BOTTOM).setDistanceFromSide(0))
                .create();

        GuiConstraintsManager rightRectangleConstraints = new GuiConstraintsManager.Builder()
                .setWidthConstraint(new RelativeConstraint(.39f))
                .setHeightConstraint(new RelativeConstraint(.4f))
                .setxConstraint(new StickyConstraint(Side.RIGHT).setDistanceFromSide(0))
                .setyConstraint(new SideConstraint(Side.TOP).setDistanceFromSide(0))
                .create();

        GuiConstraintsManager leftRectangleConstraints = new GuiConstraintsManager.Builder()
                .setWidthConstraint(new RelativeConstraint(.47f))
                .setHeightConstraint(new RelativeConstraint(.6f))
                .setxConstraint(new StickyConstraint(Side.LEFT).setDistanceFromSide(0))
                .setyConstraint(new CenterConstraint())
                .create();

        overallView = new GuiRectangle(this, Background.NO_BACKGROUND, upperRectangleConstraints);

        peopleTab = new GuiRectangle(this, new Background<>(new Color(0, 0, 0, 120)),
                rightRectangleConstraints);

        moneyTab = new GuiRectangle(this, new Background<>(new Color(0, 0, 0, 120)),
                rightRectangleConstraints);

        addPeopleButton();
        addMoneyButton();

        categoriesTab = new GuiRectangle(this, DEFAULT_BACKGROUND, leftRectangleConstraints);
        categoriesTab.setChildrenConstraints(new PatternGlobalConstraint(1, 3, 0));

        addFoodButton();
        addDrinksButton();
        addSocialButton();

        categoryView = new GuiRectangle(this, Background.NO_BACKGROUND, centerRectangleConstraints);
        categoryView.setChildrenConstraints(new RatioedPatternGlobalConstraint(2, 1, 0.08, 78, -1, 22, -1));

        categoryIcon = new GuiRectangle(categoryView, coinsImage, null);

        categoryIconsArray[CategoryState.FOOD.ordinal()] = new GuiTexture(coinsImage, categoryIcon);
        categoryIconsArray[CategoryState.DRINKS.ordinal()] = new GuiTexture(coinsImage, categoryIcon);
        categoryIconsArray[CategoryState.SOCIAL.ordinal()] = new GuiTexture(coinsImage, categoryIcon);

        for (int i = 0; i < CategoryState.values().length; i++)
            categoryIcon.addTexture(categoryIconsArray[i]);

        Text text = new Text("0%", 1.5f, DEFAULT_FONT, Color.BLACK);
        categoryPercentage = new GuiText(categoryView, text, null);

        outlineRectangle = new GuiRectangle(this, new Background<>(Color.BLACK),
                new RelativeConstraint(1, this), new RelativeConstraint(1, this), false);
        outlineRectangle.setOutlineWidth(.5);

        upperWidthConstraint = upperRectangleConstraints.getWidthConstraint();
        outlineRectangle2 = new GuiRectangle(this, new Background<>(Color.BLACK),
                upperWidthConstraint, new RelativeConstraint(1, this), false);
        outlineRectangle2.setOutlineWidth(.5);

        addDistributionGraph();

        Gui.hideGui(this);
    }

    public void setHouseItem(HouseItem houseItem) {
        this.houseItem = houseItem;
    }

    public HouseItem getHouseItem() {
        return this.houseItem;
    }

    public void removeHouseItem() {
        this.houseItem = null;
    }

    public static GuiHouseDetails getInstance() {
        return instance == null ? instance = new GuiHouseDetails() : instance;
    }

    public boolean update() {
        super.update();

        assert houseItem != null;

        Text text = peopleButton.getText().getText();
        text.setTextString(houseItem.getNumberOfPeople() + " / " + houseItem.getMaxPeopleCapacity());
        peopleButton.setupText(text);

        peopleDistributionGraph.reset();

        houseItem.getClasses().forEach((socialClass, people) -> {
            int size = people.size();
            if (size > 0)
                peopleDistributionGraph.addSector(new Sector<>(size, socialClass.getColor(), socialClass.getName()));
        });

        if (houseItem.getNumberOfPeople() < houseItem.getMaxPeopleCapacity()) {
            peopleDistributionGraph.addSector(
                    new Sector<>(houseItem.getMaxPeopleCapacity() - houseItem.getNumberOfPeople(),
                            Color.LIGHT_GRAY, Words.AVAILABLE_SPACE));
        }


        // Reset borders
        removeComponent(outlineRectangle);
        removeComponent(outlineRectangle2);
        outlineRectangle = new GuiRectangle(this, new Background<>(Color.BLACK),
                new RelativeConstraint(1, this), new RelativeConstraint(1, this), false);
        outlineRectangle.setOutlineWidth(.5);

        outlineRectangle2 = new GuiRectangle(this, new Background<>(Color.BLACK),
                upperWidthConstraint, new RelativeConstraint(1, this), false);
        outlineRectangle2.setOutlineWidth(.5);

        return true;
    }

    private void addMoneyButton() {
        GuiConstraintsManager constraintsManager = new GuiConstraintsManager.Builder()
                .setHeightConstraint(new RelativeConstraint(1, this.peopleButton))
                .setWidthConstraint(new AspectConstraint(1))
                .setxConstraint(new SideConstraint(Side.RIGHT))
                .setyConstraint(new SideConstraint(Side.TOP))
                .create();

        this.moneyButton = createButton(ButtonType.RECTANGLE, coinsImage, null, null, constraintsManager);

        this.moneyButton.setOnPress(this::onMoneyClick);
    }

    private void addDrinksButton() {
        Text text = new Text(Words.CATEGORY_DRINKS, .75f, DEFAULT_FONT, new Color(0, 0, 0));
        this.drinksCatButton = createButton(ButtonType.RECTANGLE, DEFAULT_BACKGROUND, text, null, null,
                this.categoriesTab);

        this.drinksCatButton.setOnPress(this::onDrinksCategoryClick);
    }

    private void addSocialButton() {
        Text text = new Text(Words.CATEGORY_SOCIAL, .75f, DEFAULT_FONT, new Color(0, 0, 0));
        this.socialCatButton = createButton(ButtonType.RECTANGLE, DEFAULT_BACKGROUND, text, null, null,
                this.categoriesTab);

        this.socialCatButton.setOnPress(this::onSocialCategoryClick);
    }

    private void addFoodButton() {
        Text text = new Text(Words.CATEGORY_FOOD, .75f, DEFAULT_FONT, new Color(0, 0, 0));
        this.foodCatButton = createButton(ButtonType.RECTANGLE, DEFAULT_BACKGROUND, text, null, null,
                this.categoriesTab);

        this.foodCatButton.setOnPress(this::onFoodCategoryClick);
    }

    private void addPeopleButton() {
        Text text = new Text("? / ?", .5f, DEFAULT_FONT, new Color(0, 0, 0));

        this.peopleButton = createButton(ButtonType.RECTANGLE,
                new Background<>(new Color(0, 0, 0, 50)), text, null, new GuiConstraintsManager.Builder()
                        .setWidthConstraint(new RelativeConstraint(0.18f, overallView))
                        .setHeightConstraint(new RelativeConstraint(0.35f, overallView))
                        .setxConstraint(new RelativeConstraint(0, overallView))
                        .setyConstraint(new RelativeConstraint(0.5f, overallView))
                        .create());
        this.peopleButton.enableFilter();

        this.stickyFigure = new GuiRectangle(this, stickyFigureImage, new GuiConstraintsManager.Builder()
                .setHeightConstraint(new RelativeConstraint(1, this.peopleButton))
                .setWidthConstraint(new AspectConstraint(1))
                .setxConstraint(new StickyConstraint(Side.LEFT, this.peopleButton))
                .setyConstraint(new RelativeConstraint(0, this.peopleButton))
                .create());

        if (this.peopleButton != null) {
            this.peopleButton.setDisplayed(false);
            this.peopleButton.setOnPress(this::onPeopleClick);

            setComponentTransitions(this.peopleButton);
        }
    }

    private void addDistributionGraph() {
        this.peopleDistributionGraph = new GuiDonutGraph<>(this.peopleTab,
                new GuiConstraintsManager.Builder()
                        .setDefault()
                        .setWidthConstraint(new RelativeConstraint(.7f))
                        .setHeightConstraint(new AspectConstraint(1))
                        .create());

        GuiEllipse outerCircle = new GuiEllipse(this.peopleDistributionGraph, Background.WHITE_BACKGROUND,
                new RelativeConstraint(1), new AspectConstraint(1), false);

        GuiEllipse innerCircle = new GuiEllipse(this.peopleDistributionGraph, Background.WHITE_BACKGROUND,
                new RelativeConstraint(.7f),
                new AspectConstraint(1), false);

        this.peopleDistributionGraph.setOuterCircle(outerCircle);
        this.peopleDistributionGraph.setInnerCircle(innerCircle);
        this.peopleDistributionGraph.setupListeners();
    }

    private void onMoneyClick() {
        switch (this.state) {
            case MONEY:
                this.state = State.DEFAULT;

                hideMoneyTab();
                break;
            case PEOPLE:
                this.state = State.MONEY;

                hidePeopleTab();
                showMoneyTab();
                break;
            case DEFAULT:
                this.state = State.MONEY;

                showMoneyTab();
                break;
            default:
                assert false;
                break;
        }

    }

    private void onPeopleClick() {
        switch (this.state) {
            case MONEY:
                this.state = State.PEOPLE;

                hideMoneyTab();
                showPeopleTab();
                break;
            case PEOPLE:
                this.state = State.DEFAULT;

                hidePeopleTab();
                break;
            case DEFAULT:
                this.state = State.PEOPLE;

                showPeopleTab();
                break;
            default:
                assert false;
                break;
        }
    }

    private void onFoodCategoryClick() {
        switch (this.categoryState) {
            case FOOD:
                break;
            case DRINKS:
                this.categoryState = CategoryState.FOOD;

                showFoodCategory();
                break;
            case SOCIAL:
                this.categoryState = CategoryState.FOOD;

                showFoodCategory();
                break;
            default:
                assert false;
                break;
        }
    }

    private void onSocialCategoryClick() {
        switch (this.categoryState) {
            case FOOD:
                this.categoryState = CategoryState.SOCIAL;

                showSocialCategory();
                break;
            case DRINKS:
                this.categoryState = CategoryState.SOCIAL;

                showSocialCategory();
                break;
            case SOCIAL:
                break;
            default:
                assert false;
                break;
        }
    }

    private void onDrinksCategoryClick() {
        switch (this.categoryState) {
            case FOOD:
                this.categoryState = CategoryState.DRINKS;

                showDrinksCategory();
                break;
            case DRINKS:
                break;
            case SOCIAL:
                this.categoryState = CategoryState.DRINKS;

                showDrinksCategory();
                break;
            default:
                assert false;
                break;
        }
    }

    private void hidePeopleTab() {
        this.peopleTab.setDisplayed(false);
        this.peopleDistributionGraph.setDisplayed(false);
    }

    private void showPeopleTab() {
        this.peopleTab.setDisplayed(true);
        this.peopleDistributionGraph.setDisplayed(true);
    }

    private void hideMoneyTab() {
        this.moneyTab.setDisplayed(false);
    }

    private void showMoneyTab() {
        this.moneyTab.setDisplayed(true);
    }

    private void formLoad() {
        this.state = State.DEFAULT;
        this.categoryState = CategoryState.FOOD;

        hidePeopleTab();
        hideMoneyTab();

        this.peopleButton.setDisplayed(true);
        this.peopleButton.setDisplayedComponents(true);
        this.stickyFigure.setDisplayed(true);
        this.moneyButton.setDisplayed(true);
        this.moneyButton.setDisplayedComponents(true);
        this.categoriesTab.setDisplayed(true);

        this.drinksCatButton.setDisplayed(true);
        this.drinksCatButton.setDisplayedComponents(true);
        this.foodCatButton.setDisplayed(true);
        this.foodCatButton.setDisplayedComponents(true);
        this.socialCatButton.setDisplayed(true);
        this.socialCatButton.setDisplayedComponents(true);

        this.categoryView.setDisplayed(true);
        this.categoryPercentage.setDisplayed(true);
        this.categoryIcon.setDisplayed(true);

        showFoodCategory();
    }

    @Override
    public void setDisplayed(boolean displayed) {
        super.setDisplayed(displayed);

        if (displayed) {
            formLoad();
            update();
        }
    }

    private void showFoodCategory() {
        System.out.println("Showing food category");
        categoryIcon.setTextureIndex(CategoryState.FOOD.ordinal());
    }

    private void showSocialCategory() {
        System.out.println("Showing social category");
        categoryIcon.setTextureIndex(CategoryState.SOCIAL.ordinal());
    }

    private void showDrinksCategory() {
        System.out.println("Showing drinks category");
        categoryIcon.setTextureIndex(CategoryState.DRINKS.ordinal());
    }

    enum State {
        PEOPLE,
        MONEY,
        DEFAULT
    }

    enum CategoryState {
        FOOD,
        DRINKS,
        SOCIAL
    }

    public enum MenuButton implements MenuButtons {
        PEOPLE(Words.PEOPLE),
        MONEY(Words.MONEY),
        CATEGORY_FOOD(Words.CATEGORY_FOOD),
        CATEGORY_SOCIAL(Words.CATEGORY_SOCIAL),
        CATEGORY_DRINKS(Words.CATEGORY_DRINKS);

        private final String string;

        MenuButton(String string) {
            this.string = string;
        }

        public String getString() {
            return this.string;
        }
    }
}
