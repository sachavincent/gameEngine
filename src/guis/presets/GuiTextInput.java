package guis.presets;

import static guis.Gui.DEFAULT_FONT;
import static org.lwjgl.glfw.GLFW.*;

import fontMeshCreator.Text;
import guis.Gui;
import guis.GuiInterface;
import guis.GuiTexture;
import guis.basics.GuiRectangle;
import guis.basics.GuiText;
import guis.constraints.*;
import guis.presets.buttons.GuiRectangleButton;
import inputs.ClickType;
import inputs.KeyboardUtils;
import inputs.MouseUtils;
import inputs.requests.TextInputRequest;
import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;
import org.lwjgl.glfw.GLFW;
import util.math.Vector2f;

public class GuiTextInput extends GuiPreset {

    private final GuiRectangleButton outline;
    private final GuiRectangle       cursor;
    private final GuiText            guiText;
    private final GuiRectangle       selectedTextOutline;

    private long lastBlinkTime;
    private int  cursorPosition;

    private int maxLength = Integer.MAX_VALUE;

    private final float minCursorXPosition;
    private       float textSize;

    private boolean textSelected;

    private final TextInputRequest textInputRequest = new TextInputRequest(
            (action, keyInput) -> this.processKeyboardInput(action, keyInput.getKey()));

    public GuiTextInput(GuiInterface parent, GuiConstraintsManager constraintsManager) {
        this(parent, Background.NO_BACKGROUND, constraintsManager);
    }

    public GuiTextInput(GuiInterface parent, Background<?> background) {
        this(parent, background, null);
    }

    public GuiTextInput(GuiInterface parent) {
        this(parent, (GuiConstraintsManager) null);
    }

    public GuiTextInput(GuiInterface parent, Background<?> background, GuiConstraintsManager constraintsManager) {
        super(parent, background, constraintsManager);

        GuiConstraintsManager guiConstraintsManager = new GuiConstraintsManager.Builder()
                .setDefault()
                .setWidthConstraint(new RelativeConstraint(1))
                .setHeightConstraint(new RelativeConstraint(1))
                .create();

        this.selectedTextOutline = new GuiRectangle(this, new Background<>(Color.BLUE), guiConstraintsManager);
        this.selectedTextOutline.setCornerRadius(0);
        this.selectedTextOutline.setDisplayedByDefault(false);

        this.outline = new GuiRectangleButton(this, null, guiConstraintsManager);
        this.outline.getButtonShape().setFilled(false);
        this.outline.getButtonShape().setOutlineWidth(0.1);
        this.outline.setCornerRadius(0);

        this.cursor = new GuiRectangle(this, Background.BLACK_BACKGROUND,
                new GuiConstraintsManager.Builder().setDefault()
                        .setWidthConstraint(new PixelConstraint(2))
                        .setHeightConstraint(new RelativeConstraint(1))
                        .setxConstraint(new SideConstraint(Side.LEFT, this.outline)).create());
        Text text = new Text("", .8f, DEFAULT_FONT, Color.BLACK);
        this.guiText = Gui.setupText(this.outline, text);

        this.minCursorXPosition = this.outline.getX() - this.outline.getWidth() + SideConstraint.DISTANCE_FROM_SIDE;
        this.guiText.getText().setCentered(false);
        this.guiText.setOnUpdate(() -> this.guiText.getText().setPosition(
                new Vector2f(this.minCursorXPosition, (this.guiText.getText().getPosition().y - 0.5) * 2)));

        this.cursor.setCornerRadius(0);

        this.textSelected = false;
    }

    public void setSelectedBackgroundColor(Color selectedBackgroundColor) {
        this.selectedTextOutline.setTexture(new Background<>(selectedBackgroundColor));
    }

    public void setOutlineColor(Color color) {
        this.outline.getButtonShape()
                .addTexture(new GuiTexture(new Background<>(color), this.outline.getButtonShape()));
        this.outline.getButtonShape().setTextureIndex(this.outline.getButtonShape().getNbTextures() - 1);
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    /**
     * Handles input in input
     * return true if unfocused
     */
    public boolean processKeyboardInput(int action, char key) {
        Text text = this.guiText.getText();
        String content = text.getTextString();

        if (action == GLFW_PRESS || action == GLFW_REPEAT) {
            switch (key) {
                case GLFW_KEY_SPACE:
                    updateAddCursorAndText(' ');
                    break;
                case GLFW_KEY_ESCAPE:
                    if (this.textSelected)
                        unselectText();
                    else {
                        unfocus();
                        return true;
                    }
                    break;
                case GLFW_KEY_BACKSPACE:
                    updateDelCursorAndText(GLFW_KEY_BACKSPACE);
                    break;
                case GLFW_KEY_DELETE:
                    updateDelCursorAndText(GLFW_KEY_DELETE);
                    break;
                case GLFW_KEY_LEFT:
                    if (this.textSelected)
                        unselectText();

                    shiftCursorLeft();
                    break;
                case GLFW_KEY_RIGHT:
                    if (this.textSelected)
                        unselectText();

                    shiftCursorRight();
                    break;
                case GLFW_KEY_HOME:
                    if (this.textSelected)
                        unselectText();

                    this.cursorPosition = 0;
                    this.cursor.setX(this.minCursorXPosition);
                    break;
                case GLFW_KEY_END:
                    if (this.textSelected)
                        unselectText();

                    this.cursorPosition = content.length();
                    this.cursor.setX(this.minCursorXPosition + this.textSize);
                    break;
                default: // Any character
                    if (action == GLFW_PRESS && key == GLFW_KEY_Q && KeyboardUtils.isControl) {
                        if (!content.isEmpty())
                            selectText();
                        break;
                    }
                    boolean capsEnabled = (KeyboardUtils.isCapsLock && !KeyboardUtils.isShift) ||
                            (!KeyboardUtils.isCapsLock && KeyboardUtils.isShift);

                    char charToAdd;
                    if (key >= GLFW_KEY_0 && key <= GLFW_KEY_9 && capsEnabled)
                        charToAdd = key;
                    else {
                        String keyText = glfwGetKeyName(key, 0);
                        if (keyText == null || keyText.length() != 1)
                            break;
                        charToAdd = keyText.charAt(0);

                        if (capsEnabled && Character.isAlphabetic(charToAdd)) // a -> A
                            charToAdd -= 32;
                    }

                    updateAddCursorAndText(charToAdd);
                    break;
            }
        }
        return false;
    }

    /**
     * Sets cursorPosition, changes x value of cursor
     * and deletes character of string
     *
     * @param action the action defines where to move the cursor after deletion
     */
    private void updateDelCursorAndText(int action) {
        Text text = this.guiText.getText();
        String content = text.getTextString();

        if (action != GLFW_KEY_DELETE && action != GLFW_KEY_BACKSPACE)
            return;
        if (content.isEmpty() && !this.textSelected)
            return;

        if (this.textSelected) {
            unselectText();
            resetText();
        } else if (this.cursorPosition > 0 && action == GLFW_KEY_BACKSPACE || this.cursorPosition < content.length()) {
            char charToDel;
            if (action == GLFW_KEY_BACKSPACE) {
                charToDel = shiftCursorLeft();
            } else
                charToDel = content.charAt(this.cursorPosition);

            double delCharacterWidth = text.getCharacterWidth(charToDel);

            StringBuilder builder = new StringBuilder(content);
            builder.deleteCharAt(this.cursorPosition);
            text.setTextString(builder.toString());

            this.textSize -= delCharacterWidth;
        }
    }

    /**
     * Sets cursorPosition, changes x value of cursor
     * and adds character to string
     *
     * @param charToAdd new character
     */
    private void updateAddCursorAndText(char charToAdd) {
        Text text = this.guiText.getText();
        String content = text.getTextString();

        if (!this.textSelected && content.length() >= this.maxLength)
            return;

        double newCharacterWidth = text.getCharacterWidth(charToAdd);

        if (this.textSelected) {
            resetText();
            content = "";
            unselectText();
        }

        if (content.length() < this.maxLength) {
            if (this.cursorPosition > 0)
                text.setTextString(
                        content.substring(0, this.cursorPosition) + charToAdd + content.substring(this.cursorPosition));
            else
                text.setTextString(charToAdd + content);

            this.cursor.setX((float) (this.cursor.getX() + newCharacterWidth));

            this.cursorPosition++;
        }
        this.textSize += newCharacterWidth;
    }

    private void resetText() {
        this.guiText.getText().setTextString("");
        this.cursor.setX(this.minCursorXPosition);
        this.cursorPosition = 0;

        this.textSize = 0;
    }

    private void unselectText() {
        this.selectedTextOutline.setDisplayed(false);
        this.lastBlinkTime = System.currentTimeMillis();
        this.cursor.setDisplayed(true);
        this.textSelected = false;
    }

    private void selectText() {
        this.selectedTextOutline.setWidth(this.textSize / 2);
        this.selectedTextOutline.setX(this.minCursorXPosition + this.textSize / 2);
        this.selectedTextOutline.setDisplayed(true);
        this.cursor.setDisplayed(false);
        this.lastBlinkTime = 0;
        this.textSelected = true;
    }

    /**
     * shifts cursor to the left
     *
     * @return the character shifted over
     */
    private char shiftCursorLeft() {
        if (this.cursorPosition <= 0)
            return 0;

        Text text = this.guiText.getText();
        String content = text.getTextString();

        char c = content.charAt(this.cursorPosition - 1);
        this.cursor.setX((float) (this.cursor.getX() - text.getCharacterWidth(c)));

        this.cursorPosition--;
        return c;
    }

    /**
     * shifts cursor to the right
     *
     * @return the character shifted over
     */
    private char shiftCursorRight() {
        Text text = this.guiText.getText();
        String content = text.getTextString();

        if (this.cursorPosition >= content.length())
            return 0;

        char c = content.charAt(this.cursorPosition);
        this.cursor.setX((float) (this.cursor.getX() + text.getCharacterWidth(c)));

        this.cursorPosition++;
        return c;
    }

    private float getTextSize() {
        return this.textSize;
    }

    public void setOutlineConstraints(GuiConstraints widthConstraint, GuiConstraints heightConstraint) {
        GuiConstraintHandler handler = new GuiConstraintHandler(this, this.outline);

        this.outline.setWidth(handler.handleWidthConstraint(widthConstraint));
        this.outline.setHeight(handler.handleHeightConstraint(heightConstraint));
        this.selectedTextOutline.setHeight(this.outline.getHeight());

        this.guiText.setWidth(handler.handleWidthConstraint(widthConstraint));
        this.guiText.setHeight(handler.handleHeightConstraint(heightConstraint));

        handler = new GuiConstraintHandler(this.outline, this.cursor);
        this.cursor.setHeight(handler.handleHeightConstraint(new RelativeConstraint(.8f, this.outline)));
        this.cursor.setDisplayed(false);

        this.outline.setOnMousePress(button -> {
            if (button != GLFW.GLFW_MOUSE_BUTTON_1)
                return;

            if (this.clickType == ClickType.NONE) { // Start blinking effect
                this.lastBlinkTime = System.currentTimeMillis();
                this.clickType = ClickType.M1;

                this.cursor.setDisplayed(true);

                KeyboardUtils.request(new TextInputRequest(this.textInputRequest));
            }

            if (this.textSelected)
                unselectText();

            float xCursor = MouseUtils.getCursorPos().x;
            if (xCursor > this.minCursorXPosition) {
                Text text = this.guiText.getText();
                if (xCursor > getCursorMaxPosition()) {
                    this.cursor.setX(getCursorMaxPosition());
                    this.cursorPosition = text.getTextString().length();
                } else {
                    String textString = text.getTextString();
                    List<Double> characterWidths = textString.chars().mapToDouble(text::getCharacterWidth)
                            .boxed().collect(Collectors.toList());
                    float total = this.minCursorXPosition;
                    int index = 0;
                    for (double val : characterWidths) {
                        float floatVal = (float) val;
                        float tempTotal = total + floatVal;
                        if (floatVal + total > xCursor) {
                            float previousChar = xCursor - total;
                            float min = Math.min(tempTotal - xCursor, previousChar);
                            this.cursor.setX(min == previousChar ? total : tempTotal);
                            this.cursorPosition = index;
                            return;
                        }
                        total = tempTotal;
                        index++;
                    }
                }
            }
        });
    }

    public GuiRectangleButton getOutline() {
        return this.outline;
    }

    public float getCursorMaxPosition() {
        return this.minCursorXPosition + this.textSize;
    }

    public void unfocus() {
        unselectText();
        this.cursor.setDisplayed(false);
        this.clickType = ClickType.NONE;
        this.lastBlinkTime = 0;
    }

    public void updateCursor() {
        if (this.clickType == ClickType.M1 && !this.textSelected) {
            if ((System.currentTimeMillis() - this.lastBlinkTime) > 1000) {
                this.lastBlinkTime += 1000;
                this.cursor.setDisplayed(!this.cursor.isDisplayed());
            }
        }
    }

    public String getText() {
        return this.guiText.getText().getTextString();
    }

    @Override
    public void setDisplayed(boolean displayed) {
        super.setDisplayed(displayed);

        unfocus();
    }
}
