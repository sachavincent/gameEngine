package guis.prefabs;

import fontMeshCreator.Text;
import guis.Gui;
import guis.constraints.AspectConstraint;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.presets.Background;
import guis.presets.buttons.GuiAbstractButton;
import guis.presets.buttons.GuiAbstractButton.ButtonType;
import guis.transitions.Transition;
import inputs.callbacks.PressCallback;
import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import language.Words;
import renderEngine.DisplayManager;

public class GuiEscapeMenu extends Gui {

    private final static GuiConstraints[] DEFAULT_DIMENSIONS = new GuiConstraints[]{
            new RelativeConstraint(0.2f), new RelativeConstraint(0.9f)};

    private final static Background<?> DEFAULT_BACKGROUND = new Background<>(Color.WHITE);

    private static GuiEscapeMenu instance;

//    private GuiAbstractButton resumeButton;
//    private GuiAbstractButton quitAndSaveButton;
//    private GuiAbstractButton settingsButton;
//    private GuiAbstractButton quitButton;

    private GuiEscapeMenu() {
        super(DEFAULT_BACKGROUND);

        GuiConstraintsManager menuConstraints = new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(DEFAULT_DIMENSIONS[0])
                .setHeightConstraint(DEFAULT_DIMENSIONS[1])
                .create();

        setConstraints(menuConstraints);
    }

    private GuiEscapeMenu(Background<?> background, GuiConstraintsManager constraintsManager) {
        super(background);

        setConstraints(constraintsManager);
    }

//    public GuiAbstractButton getButton(MenuButton menuButton) {
//        if (this.resumeButton == null)
//            throw new IllegalStateException("Resume button not implemented.");
//
//        return this.resumeButton;
//    }

    public void setButtonDimensions(MenuButton menuButton, GuiConstraintsManager constraints) {

    }

    public void addButton(MenuButton menuButton, ButtonType buttonType, Background<?> background,
            PressCallback onPress, Transition... transitions) {
        GuiConstraintsManager constraints = new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(new RelativeConstraint(0.7f, this))
                .setHeightConstraint(new AspectConstraint(0.25f))
                .create();

        GuiAbstractButton button;

        List<MenuButton> l = new ArrayList<>(EnumSet.allOf(MenuButton.class));
        float y = (l.indexOf(menuButton) + 1) / (l.size() + 1f);

        constraints.setyConstraint(new RelativeConstraint(y));

        button = createButton(buttonType, background,
                new Text(menuButton.getString(), .7f, DEFAULT_FONT, new Color(72, 72, 72)), null, constraints);
        if (button != null) {
            button.enableFilter();

            button.setDisplayed(false);
            button.setOnPress(onPress);
            setComponentTransitions(button, transitions);
        }
    }

    public void addButton(MenuButton menuButton, ButtonType buttonType, Background<?> background,
            Transition... transitions) {
        PressCallback pressCallback;

        switch (menuButton) {
            case RESUME:
                pressCallback = () -> {
                    final List<Transition> lTransitions = getAllTransitions();
                    if (!lTransitions.isEmpty() && lTransitions.stream().allMatch(Transition::isDone) || isDisplayed())
                        hide();
                };
                break;
            case SAVE_AND_QUIT:
                pressCallback = () -> {
                    System.out.println("Save & Quit");
                    //TODO: Save
                    quitFunction();
                };
                break;
            case QUICK_SAVE:
                pressCallback = () -> {
//                    saveGame(); //TODO: Save (quick???)
                };
                break;
            case SETTINGS:
                pressCallback = () -> {
                    settingsFunction();
                };

                break;
            case QUIT:
                pressCallback = () -> {
                    //TODO: "Are you sure" popup (AlertDialog like?)

                    // if sure
                    quitFunction();
                    // else
                    //TODO: Close popup
                };

                break;
            default:
                throw new IllegalArgumentException("Incompatible button");
        }

        addButton(menuButton, buttonType, background, pressCallback, transitions);
    }

    private void settingsFunction() {
    }


    private void quitFunction() {
        DisplayManager.closeDisplay();
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

    public static GuiEscapeMenu getEscapeMenu() {
        return instance;
    }

    public enum MenuButton implements MenuButtons {
        RESUME(Words.RESUME),
        SAVE_AND_QUIT(Words.SAVE_AND_QUIT),
        QUICK_SAVE(Words.QUICK_SAVE),//TODO: Quick save
        SETTINGS(Words.SETTINGS),
        QUIT(Words.QUIT);

        private final String string;

        MenuButton(String string) {
            this.string = string;
        }

        @Override
        public String getString() {
            return this.string;
        }
    }

    public static class Builder {

        private final GuiEscapeMenu guiEscapeMenu;

        public Builder() {
            guiEscapeMenu = new GuiEscapeMenu();
        }

        public Builder setBackground(Background<?> background) {
            guiEscapeMenu.setBackground(background);

            return this;
        }

        public Builder setConstraints(GuiConstraintsManager constraintsManager) {
            guiEscapeMenu.setConstraints(constraintsManager);

            return this;
        }

        public Builder addButton(MenuButton menuButton, ButtonType buttonType, Background<?> background,
                PressCallback clickListener, Transition... transitions) {
            guiEscapeMenu.addButton(menuButton, buttonType, background, clickListener, transitions);

            return this;
        }

        public Builder addButton(MenuButton menuButton, ButtonType buttonType, Background<?> background,
                Transition... transitions) {
            guiEscapeMenu.addButton(menuButton, buttonType, background, transitions);

            return this;
        }

        public Builder setTransitionsToAllButtons(final Transition transition, final int delay, boolean delayFirst) {
            guiEscapeMenu.setTransitionsToAllButtons(transition, delay, delayFirst);

            return this;
        }

        public Builder setTransitionsToAllButtons(Transition transition) {
            guiEscapeMenu.setTransitionsToAllButtons(transition, 0, false);

            return this;
        }

        public Builder setTransitions(Transition... transitions) {
            guiEscapeMenu.setTransitions(transitions);

            return this;
        }

        public GuiEscapeMenu create() {
            instance = guiEscapeMenu;
//             GuiRenderer.addGui(instance);

            Gui.hideGui(instance);
            return instance;
        }
    }

}
