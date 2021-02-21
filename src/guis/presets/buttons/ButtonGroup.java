package guis.presets.buttons;

public class ButtonGroup {

    private final int                 maxButtons;
    private final GuiAbstractButton[] buttons;

    private int nbButtons;

    public ButtonGroup(int nbButtons) {
        this.maxButtons = nbButtons;
        this.buttons = new GuiAbstractButton[this.maxButtons];
        this.nbButtons = 0;
    }

    public void setButtonClicked(int id) {
        for (GuiAbstractButton button : this.buttons) {
            if (button == null)
                return;

            button.clicked = button.ID == id;
            button.setFilterTransparency(button.clicked ? 1 : 0);
        }
    }

    public void addButton(GuiAbstractButton button) {
        this.buttons[Math.min(this.nbButtons++, this.maxButtons - 1)] = button;
    }

    public int getButtonIndex(int idButton) {
        for (int i = 0; i < this.buttons.length; i++) {
            GuiAbstractButton button = this.buttons[i];
            if (button.ID == idButton)
                return i;
        }
        return 0;
    }

    public int getMaxButtons() {
        return this.maxButtons;
    }
}
