package guis.prefabs;

import static guis.prefabs.GuiHouseDetails.MenuButton.PEOPLE;

import fontMeshCreator.Text;
import guis.Gui;
import guis.basics.GuiRectangle;
import guis.basics.GuiText;
import guis.constraints.AspectConstraint;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.constraints.StickyConstraint;
import guis.constraints.StickyConstraint.StickySide;
import guis.presets.GuiBackground;
import guis.presets.buttons.GuiAbstractButton;
import guis.presets.buttons.GuiAbstractButton.ButtonType;
import guis.transitions.Transition;
import inputs.callbacks.PressCallback;
import items.buildings.houses.HouseItem;
import java.awt.Color;
import language.Words;
import renderEngine.GuiRenderer;

public class GuiHouseDetails extends Gui {

    private final static GuiConstraints[] DEFAULT_DIMENSIONS = new GuiConstraints[]{
            new RelativeConstraint(.35f), new RelativeConstraint(.4f)};

    private final static GuiBackground<?> DEFAULT_BACKGROUND = new GuiBackground<>(new Color(255, 255, 255));

    private static GuiHouseDetails instance;

    private final GuiRectangle   overallView;
    private final GuiRectangle   detailsView;
    private final GuiConstraints upperWidthConstraint;

    private final GuiAbstractButton peopleButton;

    private HouseItem houseItem;

    private DetailsType detailsType = DetailsType.NONE;

    private GuiRectangle outlineRectangle, outlineRectangle2;

    private final static GuiBackground<Object> stickyFigureImage = new GuiBackground<>("stick_figure.png");

    private GuiHouseDetails(HouseItem houseItem) {
        super(DEFAULT_BACKGROUND);

        setHouseItem(houseItem);

        GuiConstraintsManager menuConstraints = new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(DEFAULT_DIMENSIONS[0])
                .setHeightConstraint(DEFAULT_DIMENSIONS[1])
                .create();

        setConstraints(menuConstraints);
        setBackground(new GuiBackground<>(new Color(255, 255, 255, 0)));

        GuiConstraintsManager upperRectangleConstraints = new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(new RelativeConstraint(.6f, this))
                .setHeightConstraint(new RelativeConstraint(.2f, this))
                .setxConstraint(new RelativeConstraint(0, this))
                .setyConstraint(new RelativeConstraint(-1, this))
                .create();

        GuiConstraintsManager rightRectangleConstraints = new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(new RelativeConstraint(.2f, this))
                .setHeightConstraint(new RelativeConstraint(.3f, this))
                .setxConstraint(new RelativeConstraint(1, this))
                .setyConstraint(new RelativeConstraint(-1, this))
                .create();

        overallView = new GuiRectangle(this, new GuiBackground<>(Color.WHITE), upperRectangleConstraints);

        detailsView = new GuiRectangle(this, new GuiBackground<>(Color.LIGHT_GRAY), rightRectangleConstraints);
        removeComponent(detailsView);

        peopleButton = addButton(PEOPLE, ButtonType.RECTANGLE,
                new GuiBackground<>(new Color(0, 0, 0, 50)));

        outlineRectangle = new GuiRectangle(this, new GuiBackground<>(Color.BLACK),
                new RelativeConstraint(1, this), new RelativeConstraint(1, this), false);
        outlineRectangle.setOutlineWidth(1.5);

        upperWidthConstraint = upperRectangleConstraints.getWidthConstraint();
        outlineRectangle2 = new GuiRectangle(this, new GuiBackground<>(Color.BLACK),
                upperWidthConstraint, new RelativeConstraint(1, this), false);
        outlineRectangle2.setOutlineWidth(.5);
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

    public static GuiHouseDetails getHouseDetailsGui() {
        return instance;
    }

    public void update() {
        if (peopleButton != null && houseItem != null) {
            Text text = peopleButton.getText().getText();
            text.setTextString(houseItem.getNumberOfPeople() + " / " + houseItem.getMaxPeopleCapacity());
            peopleButton.setupText(text);
        }
    }

    public GuiAbstractButton addButton(MenuButton menuButton, ButtonType buttonType, GuiBackground<?> background,
            Transition... transitions) {
        PressCallback onPress = null;


        GuiConstraintsManager constraints = null;
        Text text = null;
        switch (menuButton) {
            case PEOPLE:
                constraints = new GuiConstraintsManager.Builder()
                        .setDefault()
                        .setWidthConstraint(new RelativeConstraint(0.15f, overallView))
                        .setHeightConstraint(new RelativeConstraint(0.35f, overallView))
                        .setxConstraint(new RelativeConstraint(0, overallView))
                        .setyConstraint(new RelativeConstraint(0.5f, overallView))
                        .create();

                text = new Text(houseItem.getNumberOfPeople() + " / " + houseItem.getMaxPeopleCapacity(), .5f,
                        DEFAULT_FONT, new Color(0, 0, 0));

                onPress = () -> {
                    if (this.detailsView != null) {
                        switchDisplayedType(DetailsType.PEOPLE);
                    }
                };

                break;
            case MONEY:
                break;
            case CATEGORY_FOOD:
                break;
            case CATEGORY_SOCIAL:
                break;
            case CATEGORY_DRINKS:
                break;
            default:
                throw new IllegalArgumentException("Incompatible button");
        }

        GuiAbstractButton button = createButton(buttonType, background, text, null, constraints);

        constraints = new GuiConstraintsManager.Builder()
                .setDefault()
                .setHeightConstraint(new RelativeConstraint(1, button))
                .setWidthConstraint(new AspectConstraint(1))
                .setxConstraint(new StickyConstraint(StickySide.LEFT, button))
                .setyConstraint(new RelativeConstraint(0, button))
                .create();

        new GuiRectangle(this, stickyFigureImage, constraints);

        if (button != null) {
            button.setDisplayed(false);
            button.setOnPress(onPress);

            setComponentTransitions(button, transitions);
        }

        return button;
    }

    private void switchDisplayedType(DetailsType type) {
        if (this.detailsType != DetailsType.NONE) {
            removeComponent(this.detailsView);
            if (this.detailsType == type) {
                this.detailsType = DetailsType.NONE;

                return;
            }
        }
        if (type != this.detailsType) {
            switch (type) {
                case PEOPLE:
                    this.detailsType = DetailsType.PEOPLE;
                    addComponent(this.detailsView);


//                    Text text = new Text(houseItem.getNumberOfPeople() + " / " + houseItem.getMaxPeopleCapacity(), .5f,

                    //todo améliorer
                    final float nbCategories = 4;
                    final float offsetWidthRelative = 0.1f;
                    final float offsetHeightRelative = 0.1f;
                    for (int i = 0; i < nbCategories; i++) {
                        Text text = new Text(houseItem.getNumberOfPeople() + " / " + houseItem.getMaxPeopleCapacity(),
                                .5f, DEFAULT_FONT, new Color(0, 0, 0));
                        GuiConstraintsManager constraints = new GuiConstraintsManager.Builder()
                                .setDefault()
                                .setHeightConstraint(new RelativeConstraint(1 / (offsetHeightRelative *
                                        /* one for each side of text, uniform spacing */
                                        (nbCategories + 1) + nbCategories), this.detailsView))
                                .setWidthConstraint(new RelativeConstraint(.3f, this.detailsView))
                                .setxConstraint(new RelativeConstraint(1f - offsetWidthRelative * 2, this.detailsView))
                                .setyConstraint(new RelativeConstraint(
                                        -1 + offsetHeightRelative +
                                                i * (2f - offsetHeightRelative * 2) / (nbCategories - 1),
                                        this.detailsView))
                                .create();

                        GuiText guiText = new GuiText(this, text, constraints);
                        new GuiRectangle(this, stickyFigureImage, new GuiConstraintsManager.Builder()
                                .setDefault()
                                .setHeightConstraint(new RelativeConstraint(1, guiText))
                                .setWidthConstraint(new AspectConstraint(1))
                                .setxConstraint(new StickyConstraint(StickySide.LEFT, guiText))
                                .setyConstraint(new RelativeConstraint(0, guiText))
                                .create());
                    }
                    break;
                default:
                    break;
            }

            // Reset borders
            removeComponent(outlineRectangle);
            removeComponent(outlineRectangle2);
            outlineRectangle = new GuiRectangle(this, new GuiBackground<>(Color.BLACK),
                    new RelativeConstraint(1, this), new RelativeConstraint(1, this), false);
            outlineRectangle.setOutlineWidth(1.5);

            outlineRectangle2 = new GuiRectangle(this, new GuiBackground<>(Color.BLACK),
                    upperWidthConstraint, new RelativeConstraint(1, this), false);
            outlineRectangle2.setOutlineWidth(.5);
        }
    }

    enum DetailsType {
        PEOPLE,
        MONEY,
        NONE
    }

    public static class Builder {

        private final GuiHouseDetails guiHouseDetails;

        public Builder(HouseItem houseItem) {
            guiHouseDetails = new GuiHouseDetails(houseItem);
        }

        public GuiHouseDetails create() {
            if (instance != null) {
                Gui.hideGui(instance);
            }

            instance = guiHouseDetails;
            GuiRenderer.getInstance().addGui(instance);

            return instance;
        }
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
