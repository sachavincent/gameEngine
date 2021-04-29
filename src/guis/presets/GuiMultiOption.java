package guis.presets;

import static guis.Gui.DEFAULT_FONT;

import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.basics.GuiText;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RatioedPatternGlobalConstraint;
import guis.presets.buttons.GuiTriangleButton;
import java.awt.Color;
import java.util.List;

public class GuiMultiOption<T> extends GuiPreset {

    private List<T> options;

    private T selectedOption;

    private final GuiTriangleButton previous;
    private final GuiTriangleButton next;

    private final GuiText guiText;

    private OptionSelectedCallback<T> optionSelectedCallback;

    public GuiMultiOption(GuiInterface parent, Background<?> background, T defaultOption,
            GuiConstraintsManager constraintsManager, Color arrowsColor, List<T> options) {
        super(parent, background, constraintsManager);

        this.options = options;
        this.selectedOption = defaultOption;

        setLayout(new RatioedPatternGlobalConstraint(3, 1, 0, 0, 7f, 30f, 86f, 100f, 7f, 30f));

        Background<Color> arrowBackground = new Background<>(arrowsColor);
        this.previous = new GuiTriangleButton(this, arrowBackground, 270);
        this.guiText = new GuiText(this, new Text(this.selectedOption.toString(), .8f, DEFAULT_FONT, Color.BLACK));
        this.next = new GuiTriangleButton(this, arrowBackground, 90);

        this.previous.setOnPress(this::selectPrevious);
        this.next.setOnPress(this::selectNext);
    }

    public void setOptions(List<T> options) {
        this.options = options;
    }

    public void setSelectedOption(T selectedOption) {
        this.selectedOption = selectedOption;

        this.guiText.setText(
                new Text(this.selectedOption.toString(), .8f, DEFAULT_FONT, Color.BLACK));

        if (this.optionSelectedCallback != null)
            this.optionSelectedCallback.onOptionSelected(this.selectedOption);
    }

    private void selectPrevious() {
        T newOption;

        int index = this.options.indexOf(this.selectedOption);
        if (index == -1)
            return;

        if (index == 0)
            newOption = this.options.get(this.options.size() - 1);
        else
            newOption = this.options.get(index - 1);

        setSelectedOption(newOption);
    }

    private void selectNext() {
        T newOption;

        int index = this.options.indexOf(this.selectedOption);
        if (index == -1)
            return;

        if (index == this.options.size() - 1)
            newOption = this.options.get(0);
        else
            newOption = this.options.get(index + 1);

        setSelectedOption(newOption);
    }

    public void setOptionSelectedCallback(OptionSelectedCallback<T> optionSelectedCallback) {
        this.optionSelectedCallback = optionSelectedCallback;
    }

    public interface OptionSelectedCallback<T> {

        void onOptionSelected(T res);
    }
}
