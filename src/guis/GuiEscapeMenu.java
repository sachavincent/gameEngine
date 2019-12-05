package guis;

import fontMeshCreator.FontType;
import fontMeshCreator.Text;
import guis.constraints.AspectConstraint;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.presets.GuiBackground;
import guis.presets.buttons.GuiAbstractButton;
import guis.presets.buttons.GuiAbstractButton.ButtonType;
import guis.presets.buttons.GuiCircularButton;
import guis.presets.buttons.GuiRectangleButton;
import guis.transitions.Transition;
import inputs.callbacks.PressCallback;
import java.awt.Color;
import java.io.File;
import language.Words;
import renderEngine.DisplayManager;
import textures.FontTexture;

public class GuiEscapeMenu extends Gui {

    private final static FontType DEFAULT_FONT = new FontType(
            new FontTexture("roboto.png").getTextureID(), new File("res/roboto.fnt")); //TODO System-wide font

    private final static GuiConstraints[] DEFAULT_DIMENSIONS = new GuiConstraints[]{
            new RelativeConstraint(0.2f), new RelativeConstraint(0.9f)};

    private final static GuiBackground DEFAULT_BACKGROUND = new GuiBackground(Color.WHITE);

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

    private GuiEscapeMenu(GuiBackground<?> background, GuiConstraintsManager constraintsManager) {
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

    public void addButton(MenuButton menuButton, ButtonType buttonType, GuiBackground<?> background,
            Transition... transitions) {
        PressCallback pressCallback = null;

        switch (menuButton) {
            case RESUME:
                pressCallback = this::hide;
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
            case QUIT:
                pressCallback = () -> {
                    //TODO: "Are you sure" popup (AlertDialog like?)

                    // if sure
                    quitFunction();
                    // else
                    //TODO: Close popup
                };

                break;
        }

        addButton(menuButton, buttonType, background, pressCallback, transitions);
    }


    public void addButton(MenuButton menuButton, ButtonType buttonType, GuiBackground<?> background,
            PressCallback onPress, Transition... transitions) {
        GuiConstraintsManager constraints = new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(new RelativeConstraint(0.7f, instance))
                .setHeightConstraint(new AspectConstraint(0.25f))
                .create();

        GuiAbstractButton button;
        switch (menuButton) {
            case RESUME:
                constraints.setyConstraint(new RelativeConstraint(0.25f));
                break;
            case SAVE_AND_QUIT:
                constraints
                        .setyConstraint(new RelativeConstraint(0.5f)); //TODO: More buttons = less space between buttons
                //TODO: Spacing = parameter (in pixels maybe?)

                break;
            case QUICK_SAVE:
                constraints
                        .setyConstraint(
                                new RelativeConstraint(0.75f)); //TODO: More buttons = less space between buttons
                //TODO: Spacing = parameter (in pixels maybe?)

                break;
            case QUIT:
                constraints.setyConstraint(new RelativeConstraint(1f));

                break;
            //TODO: Other buttons
        }

        button = createButton(menuButton, buttonType, background, constraints);

        if (button != null) {
            button.setOnPress(onPress);

            setComponentTransitions(button, transitions);
        }
    }

    private void quitFunction() {
        DisplayManager.closeDisplay();
    }

    private GuiAbstractButton createButton(MenuButton menuButton, ButtonType buttonType, GuiBackground<?> background,
            GuiConstraintsManager constraints) {
        switch (buttonType) {
            case CIRCULAR:
                return new GuiCircularButton(this, background,
                        new Text(menuButton.getString(), .7f, DEFAULT_FONT, new Color(72, 72, 72)),
                        constraints); //TODO: Handle fontSize automatically (text length with button width)
            // TODO: Add color parameter
            case RECTANGLE:
                return new GuiRectangleButton(this, background,
                        new Text(menuButton.getString(), .7f, DEFAULT_FONT, new Color(72, 72, 72)), constraints);
        }
        return null;
    }

    public static GuiEscapeMenu getEscapeMenu() {
        return instance;
    }

    public enum MenuButton {
        RESUME(Words.RESUME),
        SAVE_AND_QUIT(Words.SAVE_AND_QUIT),
        QUICK_SAVE(Words.QUICK_SAVE),//TODO: Quick save
        QUIT(Words.QUIT),
        SETTINGS(Words.SETTINGS);

        private String string;

        MenuButton(String string) {
            this.string = string;
        }

        public String getString() {
            return this.string;
        }
    }

    public static class Builder {

        private GuiEscapeMenu guiEscapeMenu;

        public Builder() {
            guiEscapeMenu = new GuiEscapeMenu();
        }

        public Builder setBackground(GuiBackground guiBackground) {
            guiEscapeMenu.setBackground(guiBackground);

            return this;
        }

        public Builder setConstraints(GuiConstraintsManager constraintsManager) {
            guiEscapeMenu.setConstraints(constraintsManager);

            return this;
        }

        public Builder addButton(MenuButton menuButton, ButtonType buttonType, GuiBackground<?> background,
                PressCallback clickListener, Transition... transitions) {
            guiEscapeMenu.addButton(menuButton, buttonType, background, clickListener, transitions);

            return this;
        }

        public Builder addButton(MenuButton menuButton, ButtonType buttonType, GuiBackground<?> background,
                Transition... transitions) {
            guiEscapeMenu.addButton(menuButton, buttonType, background, transitions);

            return this;
        }


        public GuiEscapeMenu create() {
            return (instance = guiEscapeMenu);
        }
    }

}
