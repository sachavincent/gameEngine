package guis.prefabs.GuiMainMenu;

import fontMeshCreator.Text;
import guis.basics.GuiRectangle;
import guis.basics.GuiText;
import guis.constraints.CenterConstraint;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.PatternGlobalConstraint;
import guis.constraints.RatioedPatternGlobalConstraint;
import guis.constraints.RelativeConstraint;
import guis.presets.Background;
import guis.presets.buttons.ButtonGroup;
import guis.presets.buttons.GuiRectangleButton;
import inputs.ClickType;
import java.awt.Color;
import language.Language;
import language.TextConverter;
import language.Words;
import org.lwjgl.glfw.GLFW;

public class GuiLanguageSettings extends GuiTab {

    public GuiLanguageSettings(GuiMultiTab parent) {
        super(Background.NO_BACKGROUND, parent, Words.LANGUAGE);

        setConstraints(new GuiConstraintsManager.Builder()
                .setWidthConstraint(new RelativeConstraint(1, parent.content))
                .setHeightConstraint(new RelativeConstraint(1, parent.content))
                .setxConstraint(new CenterConstraint(parent.content))
                .setyConstraint(new RelativeConstraint(0, parent.content))
                .create());

        int nbSupportedLanguages = Language.getNbSupportedLanguages();

        setLayout(new RatioedPatternGlobalConstraint(1, 2, .2f, 0, 100f, 20f, 100f, 80f));

        GuiRectangle titleArea = new GuiRectangle(this, Background.NO_BACKGROUND);
        GuiRectangle buttonArea = new GuiRectangle(this, Background.NO_BACKGROUND);

        buttonArea.setLayout(
                new PatternGlobalConstraint(Math.min(nbSupportedLanguages, 2), 8, .03f));

        Text text = new Text(Words.LANGUAGE, .8f, DEFAULT_FONT, Color.BLACK);
        GuiText title = new GuiText(titleArea, text);
        ButtonGroup buttonGroup = new ButtonGroup(Language.getNbSupportedLanguages());

        for (Language language : Language.values()) {
            GuiRectangleButton button = new GuiRectangleButton(buttonArea, Background.NO_BACKGROUND, null,
                    new Text(language.getName(), .8f, DEFAULT_FONT, Color.BLACK));

            button.setToggleType(true);
            button.enableFilter();
            button.setButtonGroup(buttonGroup);
            button.setOnMousePress(b -> {
                if (b == GLFW.GLFW_MOUSE_BUTTON_1)
                    TextConverter.setNewLanguage(language);
            });

            if (language == TextConverter.getLanguage())
                buttonGroup.setButtonClicked(ClickType.M1, button.ID);
        }
    }
}