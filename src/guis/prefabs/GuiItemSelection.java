package guis.prefabs;

import abstractItem.AbstractDirtRoadItem;
import abstractItem.AbstractInsula;
import abstractItem.AbstractMarket;
import fontMeshCreator.FontType;
import fontMeshCreator.Text;
import guis.Gui;
import guis.GuiComponent;
import guis.constraints.CenterConstraint;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.GuiGlobalConstraints;
import guis.constraints.RelativeConstraint;
import guis.constraints.SideConstraint;
import guis.constraints.SideConstraint.Side;
import guis.presets.GuiBackground;
import guis.presets.buttons.GuiAbstractButton;
import guis.presets.buttons.GuiAbstractButton.ButtonType;
import guis.presets.buttons.GuiCircularButton;
import guis.presets.buttons.GuiRectangleButton;
import guis.transitions.Transition;
import inputs.callbacks.PressCallback;
import items.buildings.Market;
import items.buildings.houses.Insula;
import items.roads.DirtRoadItem;
import java.awt.Color;
import java.io.File;
import java.util.EnumSet;
import renderEngine.GuiRenderer;
import textures.FontTexture;

public class GuiItemSelection extends Gui {

    private final static FontType DEFAULT_FONT = new FontType(
            new FontTexture("roboto.png").getTextureID(), new File("res/roboto.fnt")); //TODO System-wide font

    private final static GuiConstraints[] DEFAULT_COORDINATES = new GuiConstraints[]{
            new SideConstraint(Side.RIGHT), new CenterConstraint()};

    private final static GuiConstraints[] DEFAULT_DIMENSIONS = new GuiConstraints[]{
            new RelativeConstraint(0.2f), new RelativeConstraint(0.25f)};

    private final static GuiBackground<?> DEFAULT_BACKGROUND = new GuiBackground<>(Color.WHITE);

    private static GuiItemSelection instance;

    private EnumSet<MenuButton> buttons = EnumSet.noneOf(MenuButton.class);

    private GuiGlobalConstraints childrenConstraints;

    private GuiItemSelection() {
        super(DEFAULT_BACKGROUND);

        GuiConstraintsManager menuConstraints = new GuiConstraintsManager.Builder()
                .setWidthConstraint(DEFAULT_DIMENSIONS[0])
                .setHeightConstraint(DEFAULT_DIMENSIONS[1])
                .setxConstraint(DEFAULT_COORDINATES[0])
                .setyConstraint(DEFAULT_COORDINATES[1])
                .create();

        setConstraints(menuConstraints);
    }

    private GuiItemSelection(GuiBackground<?> background, GuiConstraintsManager constraintsManager) {
        super(background);

        setConstraints(constraintsManager);
    }

    public void addButton(MenuButton menuButton, ButtonType buttonType, GuiBackground<?> background,
            Transition... transitions) {
        PressCallback pressCallback;

        switch (menuButton) {
            case DIRT_ROAD:
                pressCallback = () -> {
                    System.out.println("DirtRoad selected");

                    GuiSelectedItem.getSelectedItemGui().setSelectedItem(new AbstractDirtRoadItem());
                };
                break;
            case INSULA:
                pressCallback = () -> {
                    System.out.println("Insula selected");

                    GuiSelectedItem.getSelectedItemGui().setSelectedItem(new AbstractInsula());
                };
                break;
            case MARKET:
                pressCallback = () -> {
                    System.out.println("Market selected");

                    GuiSelectedItem.getSelectedItemGui().setSelectedItem(new AbstractMarket());
                };
                break;
            default:
                throw new IllegalArgumentException("Incompatible button");
        }

        addButton(menuButton, buttonType, background, pressCallback, transitions);
    }

    public void addButton(MenuButton menuButton, ButtonType buttonType, GuiBackground<?> background,
            PressCallback onPress, Transition... transitions) {
//        GuiConstraintsManager constraints = new GuiConstraintsManager.Builder()
//                .setWidthConstraint(new RelativeConstraint(0.2f, this))
//                .setHeightConstraint(new AspectConstraint(1))
//                .setxConstraint(new RelativeConstraint(-1 + .5f * buttons.size(), this))
//                .setyConstraint(new RelativeConstraint(1, this))
//                .create();

        GuiAbstractButton button;

        button = createButton(menuButton, buttonType, background);
        if (button != null) {
            buttons.add(menuButton);

            button.setDisplayed(false);
            button.setOnPress(onPress);

            setComponentTransitions(button, transitions);
        }
    }

    private GuiAbstractButton createButton(MenuButton menuButton, ButtonType buttonType, GuiBackground<?> background) {
        switch (buttonType) {
            case CIRCULAR:
                return new GuiCircularButton(this, background, null,
                        new Text(menuButton.getTooltipName(), .55f, DEFAULT_FONT, Color.DARK_GRAY),
                        0); //TODO: Handle fontSize automatically (text length with button width)
            // TODO: Add color parameter
            case RECTANGLE:
                return new GuiRectangleButton(this, background, null,
                        new Text(menuButton.getTooltipName(), .55f, DEFAULT_FONT, Color.DARK_GRAY), 0);
        }
        return null;
    }

    public void setTransitionsToAllButtons(final Transition transition, final int delay, boolean delayFirst) {
        if (delayFirst)
            transition.setDelay(delay);

        getComponents().keySet().stream().filter(GuiAbstractButton.class::isInstance)
                .forEach(guiComponent -> {
                    setComponentTransitions(guiComponent, transition.copy());

                    transition.setDelay(transition.getDelay() + delay);
                });

    }

    @Override
    public void addComponent(GuiComponent<?> guiComponent, Transition... transitions) {
        if (guiComponent == null)
            return;

        if (childrenConstraints != null) {
            GuiConstraintsManager constraints = childrenConstraints.addElement(guiComponent);
            if (constraints == null)
                throw new IllegalArgumentException("Too many components in the gui!");

            guiComponent.setConstraints(constraints);
        }

        super.addComponent(guiComponent, transitions);
    }

    public void setChildrenConstraints(GuiGlobalConstraints guiConstraints) {
        this.childrenConstraints = guiConstraints;
    }

    public static GuiItemSelection getItemSelectionGui() {
        return instance;
    }

    public enum MenuButton {
        DIRT_ROAD(DirtRoadItem.NAME),
        INSULA(Insula.NAME),
        MARKET(Market.NAME);

        private String tooltipName;

        MenuButton(String tooltipName) {
            this.tooltipName = tooltipName;
        }

        public String getTooltipName() {
            return this.tooltipName;
        }
    }

    public static class Builder {

        private GuiItemSelection guiItemSelection;

        public Builder() {
            guiItemSelection = new GuiItemSelection();
        }

        public Builder setBackground(GuiBackground<?> guiBackground) {
            guiItemSelection.setBackground(guiBackground);

            return this;
        }

        public Builder setConstraints(GuiConstraintsManager constraintsManager) {
            guiItemSelection.setConstraints(constraintsManager);

            return this;
        }

        public Builder addButton(MenuButton menuButton, ButtonType buttonType, GuiBackground<?> background,
                PressCallback clickListener, Transition... transitions) {
            guiItemSelection.addButton(menuButton, buttonType, background, clickListener, transitions);

            return this;
        }

        public Builder addButton(MenuButton menuButton, ButtonType buttonType, GuiBackground<?> background,
                Transition... transitions) {
//            if (!guiItemSelection.buttons.contains(menuButton))
            guiItemSelection.addButton(menuButton, buttonType, background, transitions);

            return this;
        }

        public Builder setTransitionsToAllButtons(final Transition transition, final int delay, boolean delayFirst) {
            guiItemSelection.setTransitionsToAllButtons(transition, delay, delayFirst);

            return this;
        }

        public Builder setTransitionsToAllButtons(Transition transition) {
            guiItemSelection.setTransitionsToAllButtons(transition, 0, false);

            return this;
        }

        public Builder setTransitions(Transition... transitions) {
            guiItemSelection.setTransitions(transitions);

            return this;
        }

        public Builder setChildrenConstraints(GuiGlobalConstraints guiConstraints) {
            guiConstraints.setParent(guiItemSelection);

            guiItemSelection.setChildrenConstraints(guiConstraints);

            return this;
        }

        public GuiItemSelection create() {
            instance = guiItemSelection;
            GuiRenderer.getInstance().addGui(instance);
            instance.getTooltipGuis().forEach(gui -> GuiRenderer.getInstance().addGui(gui));

            return instance;
        }
    }

}
