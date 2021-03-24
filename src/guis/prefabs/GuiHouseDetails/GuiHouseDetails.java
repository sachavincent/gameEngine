package guis.prefabs.GuiHouseDetails;

import fontMeshCreator.Text;
import guis.Gui;
import guis.basics.GuiEllipse;
import guis.basics.GuiRectangle;
import guis.constraints.*;
import guis.presets.Background;
import guis.presets.buttons.ButtonGroup;
import guis.presets.buttons.GuiAbstractButton;
import guis.presets.buttons.GuiAbstractButton.ButtonType;
import guis.presets.buttons.GuiRectangleButton;
import guis.presets.graphs.GuiDonutGraph;
import guis.presets.graphs.GuiDonutGraph.Sector;
import items.buildings.houses.HouseItem;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import language.Words;
import resources.ResourceManager;
import resources.ResourceManager.Resource;
import resources.ResourceManager.Stock;
import resources.ResourceType;

public class GuiHouseDetails extends Gui {

    private final static GuiConstraints[] DEFAULT_DIMENSIONS = new GuiConstraints[]{
            new RelativeConstraint(.22f), new AspectConstraint(1.3f)};

    //    private final static GuiBackground<?> DEFAULT_BACKGROUND = new GuiBackground<>(new Color(255, 255, 255, 0));
    public final static Background<?> DEFAULT_BACKGROUND = new Background<>("#0D47A1");

    private static GuiHouseDetails instance;

    private final GuiRectangle overallView;

    private final GuiRectangle peopleTab;
    private final GuiRectangle moneyTab;

    private final CategoriesTab    categoriesTab;
    private final SubCategoriesTab subCategoriesTab;
    private final CategoryView     categoryView;

    private GuiAbstractButton peopleButton;
    private GuiAbstractButton moneyButton;

    private HouseItem houseItem;

    private State        state;
    private ResourceType resourceType;

    private final static Background<String> stickyFigureImage = new Background<>("stick_figure.png");

    private final static Background<String> coinsImage = new Background<>("coins.png");

    private GuiDonutGraph<Integer> peopleDistributionGraph;
    private GuiRectangle           stickyFigure;

    private GuiHouseDetails() {
        super(DEFAULT_BACKGROUND);

        setAlpha(.95f);

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


        GuiConstraintsManager rightRectangleConstraints = new GuiConstraintsManager.Builder()
                .setWidthConstraint(new RelativeConstraint(.39f))
                .setHeightConstraint(new RelativeConstraint(.4f))
                .setxConstraint(new StickyConstraint(Side.RIGHT).setDistanceFromSide(0))
                .setyConstraint(new SideConstraint(Side.TOP).setDistanceFromSide(0))
                .create();


        this.overallView = new GuiRectangle(this, Background.NO_BACKGROUND, upperRectangleConstraints);

        this.peopleTab = new GuiRectangle(this, new Background<>(new Color(0, 0, 0, 120)),
                rightRectangleConstraints);

        this.moneyTab = new GuiRectangle(this, new Background<>(new Color(0, 0, 0, 120)),
                rightRectangleConstraints);

        addPeopleButton();
        addMoneyButton();

        this.categoriesTab = new CategoriesTab(this);
        this.subCategoriesTab = new SubCategoriesTab(this);

        this.categoryView = new CategoryView(this);

        addDistributionGraph();

        setDisplayed(false);
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
        return instance == null ? (instance = new GuiHouseDetails()) : instance;
    }

    public void setCurrentCategoryPercentage(int percentage) {
        this.categoryView.categoryIcon.setProgressPercentage(percentage);

        Text text = this.categoryView.categoryPercentage.getText();
        text.setTextString(percentage + "%");
        this.categoryView.categoryPercentage.setText(text);
    }

    /**
     * updating GUI with new houseItem
     */
    public boolean update() {
        super.update();

        assert this.houseItem != null;

        Text text = this.peopleButton.getText().getText();
        text.setTextString(this.houseItem.getNumberOfPeople() + " / " + this.houseItem.getMaxPeopleCapacity());
        this.peopleButton.getText().setText(text);

        this.peopleDistributionGraph.reset();

        this.houseItem.getClasses().forEach((socialClass, people) -> {
            int size = people.size();
            if (size > 0)
                this.peopleDistributionGraph
                        .addSector(new Sector<>(size, socialClass.getColor(), socialClass.getName()));
        });

        if (this.houseItem.getNumberOfPeople() < this.houseItem.getMaxPeopleCapacity()) {
            this.peopleDistributionGraph.addSector(
                    new Sector<>(this.houseItem.getMaxPeopleCapacity() - this.houseItem.getNumberOfPeople(),
                            Color.LIGHT_GRAY, Words.AVAILABLE_SPACE));
        }

        return true;
    }

    /**
     * Called when category selected
     */
    private void setCategorySelected(ResourceType resourceType) {
        if (this.houseItem == null)
            return;

        this.resourceType = resourceType;

        Map<Resource, Integer> resourcesNeeded = this.houseItem.getResourcesNeeded();
//        List<Resource> subCategories = resourcesNeeded.keySet().stream()
//                .filter(resource -> resource.getResourceType().equals(resourceType)).collect(Collectors.toList());

        List<Resource> subCategories = Arrays.stream(Resource.values())
                .filter(resource -> resource.getResourceType() == ResourceType.FOOD).collect(Collectors.toList());

        if (subCategories.isEmpty())
            return;

        this.subCategoriesTab.clearComponents();
        this.subCategoriesTab.setChildrenConstraints(new PatternGlobalConstraint(subCategories.size(), 1, 0));

        AtomicBoolean selected = new AtomicBoolean();
        ButtonGroup group = new ButtonGroup(subCategories.size());
        subCategories.forEach(resource -> {
            if (resourcesNeeded.containsKey(resource)) {
                GuiRectangleButton button = new GuiRectangleButton(this.subCategoriesTab, resource.getBackgroundTexture(),
                        (GuiConstraintsManager) null);
                button.enableFilter();
                button.setToggleType(true);
                button.setButtonGroup(group);
                button.setOnPress(() -> {
                    int value = resourcesNeeded.get(resource);
                    Stock stock = ResourceManager.getResources().get(resource);

                    setCurrentCategoryPercentage(stock.getAmount() * 100 / value);
                    this.categoryView.categoryIcon.setTextureIndex(group.getButtonIndex(button.ID));
                });

                if (!selected.get()) {
                    int value = resourcesNeeded.get(resource);
                    Stock stock = ResourceManager.getResources().get(resource);

                    setCurrentCategoryPercentage(stock.getAmount() * 100 / value);
                    selected.set(true);
                    button.setClicked(true);
                }
            } else {
                GuiRectangle rectangle = new GuiRectangle(subCategoriesTab, resource.getBackgroundTexture(), null);
            }
        });

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

    private void addPeopleButton() {
        Text text = new Text("? / ?", .5f, DEFAULT_FONT, new Color(0, 0, 0));

        this.peopleButton = createButton(ButtonType.RECTANGLE,
                new Background<>(new Color(0, 0, 0, 50)), text, null, new GuiConstraintsManager.Builder()
                        .setWidthConstraint(new RelativeConstraint(0.18f, this.overallView))
                        .setHeightConstraint(new RelativeConstraint(0.35f, this.overallView))
                        .setxConstraint(new RelativeConstraint(0, this.overallView))
                        .setyConstraint(new RelativeConstraint(0.5f, this.overallView))
                        .create());
        this.peopleButton.enableFilter();
        this.peopleButton.setCornerRadius(Gui.CORNER_RADIUS);

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

    void onFoodCategoryClick() {
        switch (this.resourceType) {
            case FOOD:
                break;
            case BEVERAGE:
                this.resourceType = ResourceType.FOOD;

                showFoodCategory();
                break;
            case SOCIAL:
                this.resourceType = ResourceType.FOOD;

                showFoodCategory();
                break;
            default:
                assert false;
                break;
        }
    }

    void onSocialCategoryClick() {
        switch (this.resourceType) {
            case FOOD:
                this.resourceType = ResourceType.SOCIAL;

                showSocialCategory();
                break;
            case BEVERAGE:
                this.resourceType = ResourceType.SOCIAL;

                showSocialCategory();
                break;
            case SOCIAL:
                break;
            default:
                assert false;
                break;
        }
    }

    void onDrinksCategoryClick() {
        switch (this.resourceType) {
            case FOOD:
                this.resourceType = ResourceType.BEVERAGE;

                showDrinksCategory();
                break;
            case BEVERAGE:
                break;
            case SOCIAL:
                this.resourceType = ResourceType.BEVERAGE;

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
        setCategorySelected(ResourceType.FOOD);

        hidePeopleTab();
        hideMoneyTab();

        this.peopleButton.setDisplayed(true);
        this.peopleButton.setDisplayedComponents(true);
        this.stickyFigure.setDisplayed(true);
        this.moneyButton.setDisplayed(true);
        this.moneyButton.setDisplayedComponents(true);
        this.categoriesTab.setDisplayed(true);
//
//        this.categoriesTab.drinksCatButton.setDisplayed(true);
//        this.categoriesTab.drinksCatButton.setDisplayedComponents(true);
//        this.categoriesTab.foodCatButton.setDisplayed(true);
//        this.categoriesTab.foodCatButton.setDisplayedComponents(true);
//        this.categoriesTab.socialCatButton.setDisplayed(true);
//        this.categoriesTab.socialCatButton.setDisplayedComponents(true);

        this.categoryView.setDisplayed(true);
//        this.categoryView.categoryPercentage.setDisplayed(true);
//        this.categoryView.categoryIcon.setDisplayed(true);

        this.subCategoriesTab.setDisplayed(true);
        showFoodCategory();
    }

    @Override
    public void setDisplayed(boolean displayed) {
        super.setDisplayed(displayed);

        if (displayed) {
            formLoad();
            update();
        } else if (this.houseItem != null) {
            HouseItem houseItem = this.houseItem;
            this.houseItem = null; // Prevent infinite loop
            houseItem.unselect();
        }
    }

    private void showFoodCategory() {
        System.out.println("Showing food category");
        setCategorySelected(ResourceType.FOOD);
        this.categoryView.categoryIcon.setTextureIndex(0);
    }

    private void showSocialCategory() {
        System.out.println("Showing social category");
        setCategorySelected(ResourceType.SOCIAL);
//        categoryIcon.setTextureIndex(ResourceType.SOCIAL.ordinal() + 1);
    }

    private void showDrinksCategory() {
        System.out.println("Showing drinks category");
        setCategorySelected(ResourceType.BEVERAGE);
//        categoryIcon.setTextureIndex(ResourceType.BEVERAGE.ordinal() + 1);
    }

    enum State {
        PEOPLE,
        MONEY,
        DEFAULT
    }
}
