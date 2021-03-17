package guis.presets;

import static guis.Gui.DEFAULT_FONT;

import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.basics.GuiText;
import guis.basics.GuiTriangle;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RatioedPatternGlobalConstraint;
import guis.presets.buttons.GuiTriangleButton;
import java.awt.Color;
import java.util.List;

public class GuiMultiOption<T> extends GuiPreset {

    private final List<T> options;

    private T selectedOption;

    private GuiTriangleButton previous;
    private GuiTriangleButton       next;

    private GuiText guiText;

    private OptionSelectedCallback<T> optionSelectedCallback;

    public GuiMultiOption(GuiInterface parent, Background<?> background, T defaultOption,
            GuiConstraintsManager constraintsManager, Color arrowsColor, List<T> options) {
        super(parent, background, constraintsManager);

        this.options = options;
        this.selectedOption = defaultOption;

        setChildrenConstraints(new RatioedPatternGlobalConstraint(3, 1, 0, 0,7f, 30f, 86f, 100f, 7f, 30f));

        Background<Color> arrowBackground = new Background<>(arrowsColor);
        this.previous = new GuiTriangleButton(this, arrowBackground, 270);
        this.guiText = new GuiText(this, new Text(this.selectedOption.toString(), .8f, DEFAULT_FONT, Color.BLACK));
        this.next = new GuiTriangleButton(this, arrowBackground, 90);

        this.previous.setOnPress(this::selectPrevious);
        this.next.setOnPress(this::selectNext);
    }

    private void selectPrevious() {
        int index = this.options.indexOf(this.selectedOption);
        if (index == 0)
            this.selectedOption = this.options.get(this.options.size() - 1);
        else
            this.selectedOption = this.options.get(index - 1);

        this.guiText.setText(
                new Text(this.selectedOption.toString(), .8f, DEFAULT_FONT, Color.BLACK));
        if (this.optionSelectedCallback != null)
            this.optionSelectedCallback.onOptionSelected(this.selectedOption);
    }

    private void selectNext() {
        int index = this.options.indexOf(this.selectedOption);
        if (index == this.options.size() - 1)
            this.selectedOption = this.options.get(0);
        else
            this.selectedOption = this.options.get(index + 1);

        this.guiText.setText(
                new Text(this.selectedOption.toString(), .8f, DEFAULT_FONT, Color.BLACK));

        if (this.optionSelectedCallback != null)
            this.optionSelectedCallback.onOptionSelected(this.selectedOption);
    }

    public void setOptionSelectedCallback(OptionSelectedCallback<T> optionSelectedCallback) {
        this.optionSelectedCallback = optionSelectedCallback;
    }

    public interface OptionSelectedCallback<T> {

        void onOptionSelected(T res);
    }
}
