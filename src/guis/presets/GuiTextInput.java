package guis.presets;

import static guis.Gui.DEFAULT_FONT;
import static org.lwjgl.glfw.GLFW.*;

import fontMeshCreator.Line;
import fontMeshCreator.Text;
import guis.GuiInterface;
import guis.basics.GuiRectangle;
import guis.basics.GuiText;
import guis.constraints.*;
import guis.presets.buttons.GuiRectangleButton;
import inputs.ClickType;
import inputs.Key;
import inputs.KeyInput;
import inputs.KeyboardUtils;
import inputs.MouseUtils;
import inputs.callbacks.MousePressCallback;
import inputs.callbacks.MouseReleaseCallback;
import inputs.requests.KeyMappingRequest;
import inputs.requests.Request.RequestType;
import inputs.requests.RequestManager;
import inputs.requests.TextInputRequest;
import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.lwjgl.glfw.GLFW;
import renderEngine.DisplayManager;
import util.math.Vector2f;

public class GuiTextInput extends GuiPreset implements GuiClickablePreset {

    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    private final GuiRectangleButton outline;
    private final GuiRectangle       cursor;
    private final GuiText            guiText;
    private final GuiRectangle       selectedTextOutline;

    private long lastBlinkTime;
    private int  cursorPosition;

    private final float minCursorXPosition;
    private       float textSize;

    private boolean textSelected;

    private       ClickType     clickType;
    /**
     * ESCAPE by default
     */
    private final Set<KeyInput> cancelInputs;

    private       int           keyRequestId;
    private       int           charRequestId;
    /**
     * ENTER & KP_ENTER by default
     */
    private final Set<KeyInput> sendInputs;

    private final TextInputRequest  textInputRequest = new TextInputRequest(this::processKeyboardInput);
    private final KeyMappingRequest shortcutRequest  = new KeyMappingRequest(this::processKeyMappings);

    private SendTextCallback onSendTextCallback = text -> {
    };
    private double           spaceWidth;

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

        this.clickType = ClickType.NONE;
        this.selectedTextOutline = new GuiRectangle(this, new Background<>(Color.BLUE), guiConstraintsManager);
        this.selectedTextOutline.setCornerRadius(0);
        this.selectedTextOutline.setDisplayedByDefault(false);

        this.outline = new GuiRectangleButton(this, Background.NO_BACKGROUND, null, guiConstraintsManager);
        this.outline.getShape().setFilled(false);
        this.outline.getShape().setOutlineWidth(2);
        this.outline.setCornerRadius(0);

        this.cursor = new GuiRectangle(this, Background.BLACK_BACKGROUND,
                new GuiConstraintsManager.Builder().setDefault()
                        .setWidthConstraint(new PixelConstraint(2))
                        .setHeightConstraint(new RelativeConstraint(.65f))
                        .setxConstraint(new SideConstraint(Side.LEFT, this.outline)).create());
        Text text = new Text("", .8f, DEFAULT_FONT, Color.BLACK);
        this.spaceWidth = text.getFont().getLoader().getMetaData().getSpaceWidth() * 2 * text.getFontSize();
        text.setMaxLines(1);
        text.setMaxLineLength(this.outline.getWidth());
        this.guiText = new GuiText(this.outline, text);
        this.minCursorXPosition = (float) (this.outline.getX() - this.outline.getWidth() + this.spaceWidth * 2);
        this.guiText.getText().setCenteredHorizontally(false);
        this.guiText.setOnUpdate(() -> this.guiText.getText().setPosition(
                new Vector2f(this.minCursorXPosition, (this.guiText.getText().getPosition().y - 0.5) * 2)));

        this.cursor.setCornerRadius(0);

        this.textSelected = false;

        this.cancelInputs = new HashSet<>();
        this.cancelInputs.add(Key.ESCAPE.getKeyInput());

        this.sendInputs = new HashSet<>();
        this.sendInputs.add(new KeyInput((char) GLFW_KEY_ENTER));
        this.sendInputs.add(new KeyInput((char) GLFW_KEY_KP_ENTER));
    }

    public void addCancelInput(KeyInput cancelInput) {
        this.cancelInputs.add(cancelInput);
    }

    public void addSendInput(KeyInput cancelInput) {
        this.sendInputs.add(cancelInput);
    }

    public Set<KeyInput> getCancelInputs() {
        return this.cancelInputs;
    }

    public void setSelectedBackgroundColor(Color selectedBackgroundColor) {
        this.selectedTextOutline.setTexture(new Background<>(selectedBackgroundColor));
    }

    public void setOutlineColor(Color color) {
        this.outline.getShape().setBorderColor(color);
    }

    /**
     * Handles shortcuts
     */
    public boolean processKeyMappings(int action, KeyInput keyInput) {
        Text text = this.guiText.getText();
        String content = text.getTextString();

        Character key = keyInput.getKey();
        if (action == GLFW_PRESS || action == GLFW_REPEAT) {
            if (this.cancelInputs.contains(keyInput.toDefaultKeyboardLayout()))
                return onCancel();

            if (this.sendInputs.contains(keyInput.toDefaultKeyboardLayout()))
                return onSend();

            if (key == GLFW_KEY_BACKSPACE) {
                updateDelCursorAndText(GLFW_KEY_BACKSPACE);
            } else if (key == GLFW_KEY_DELETE) {
                updateDelCursorAndText(GLFW_KEY_DELETE);
            } else if (key == GLFW_KEY_TAB) {
                updateAddCursorAndText('\t');
            } else if (key == GLFW_KEY_LEFT) {
                if (this.textSelected)
                    unselectText();

                shiftCursorLeft();
            } else if (key == GLFW_KEY_RIGHT) {
                if (this.textSelected)
                    unselectText();

                shiftCursorRight();
            } else if (key == GLFW_KEY_HOME) {
                if (this.textSelected)
                    unselectText();

                this.cursorPosition = 0;
                this.cursor.setX(this.minCursorXPosition);
            } else if (key == GLFW_KEY_END) {
                if (this.textSelected)
                    unselectText();

                this.cursorPosition = content.length();
                this.cursor.setX(this.minCursorXPosition + this.textSize);
            } else if (action == GLFW_PRESS && (key == GLFW_KEY_X || key == GLFW_KEY_C) && KeyboardUtils.isControl &&
                    this.textSelected) {
                glfwSetClipboardString(DisplayManager.getWindow(), content);
                if (key == GLFW_KEY_X) {
                    unselectText();
                    clearText();
                }
            } else if (key == GLFW_KEY_V && KeyboardUtils.isControl) {
                if (this.textSelected) {
                    unselectText();
                    clearText();
                }
                String clipBoardContent = glfwGetClipboardString(DisplayManager.getWindow());
                if (clipBoardContent != null) {
                    long nbLines = clipBoardContent.codePoints().filter(value -> value == 13).count();
                    if (text.getMaxLines() > nbLines) {
                        for (int c : clipBoardContent.chars().toArray()) {
                            if (!updateAddCursorAndText((char) c))
                                break;
                        }
                    }
                }
            } else if (action == GLFW_PRESS && key == GLFW_KEY_Q && KeyboardUtils.isControl) {
                if (!content.isEmpty())
                    selectText();
                return false;
            }
        } //TODO: ADD TABULATIONS (auto completion)
        return false;
    }

    private boolean onSend() {
        this.onSendTextCallback.onSend(this.guiText.getText().getTextString());
        unselectText();
        clearText();
        return false;
    }

    private boolean onCancel() {
        if (this.textSelected)
            unselectText();
        else {
            unfocus();
            return true;
        }
        return false;
    }

    /**
     * Handles input in input
     * return true if unfocused
     */
    public boolean processKeyboardInput(int action, KeyInput keyInput) {
        Character key = keyInput.getKey();
        if (action == GLFW_PRESS || action == GLFW_REPEAT) {
            if (this.cancelInputs.contains(keyInput.toDefaultKeyboardLayout()))
                return onCancel();

            if (this.sendInputs.contains(keyInput.toDefaultKeyboardLayout()))
                return onSend();

            if (key == GLFW_KEY_SPACE)
                updateAddCursorAndText(' ');
            else
                updateAddCursorAndText(key);
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
            clearText();
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
     * @return false if not enough room
     */
    private boolean updateAddCursorAndText(char charToAdd) {
        Text text = this.guiText.getText();
        String content = text.getTextString();

        if (this.textSelected) {
            clearText();
            content = "";
            unselectText();
        }

        List<Line> lines = text.getLines();
        double lineLength = 0;
        if (!lines.isEmpty())
            lineLength = lines.get(0).getLineLength();


        double newCharacterWidth = text.getCharacterWidth(charToAdd);

        if (lineLength + newCharacterWidth + this.spaceWidth <= text.getMaxLineLength()) {
            if (this.cursorPosition > 0)
                text.setTextString(
                        content.substring(0, this.cursorPosition) + charToAdd + content.substring(this.cursorPosition));
            else
                text.setTextString(charToAdd + content);

            this.cursor.setX((float) (this.cursor.getX() + newCharacterWidth));

            this.cursorPosition++;
            this.textSize += newCharacterWidth;
        } else
            return false;

        return true;
    }

    public void clearText() {
        Color color = this.guiText.getText().getColor(1);
        this.guiText.getText().clear();
        setTextColor(color);
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
                return false;

            focus();
            return true;
        });
    }

    private void createRequests() {
        this.keyRequestId = RequestManager.getInstance().request(new KeyMappingRequest(this.shortcutRequest));
        this.charRequestId = RequestManager.getInstance().request(new TextInputRequest(this.textInputRequest));
    }

    public void focus() {
        if (this.clickType == ClickType.NONE) { // Start blinking effect
            this.lastBlinkTime = System.currentTimeMillis();
            this.clickType = ClickType.M1;

            this.cursor.setDisplayed(true);
            this.service.schedule(this::createRequests, 100, TimeUnit.MILLISECONDS);
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
    }

    public GuiRectangleButton getOutline() {
        return this.outline;
    }

    public float getCursorMaxPosition() {
        return this.minCursorXPosition + this.textSize;
    }

    public void unfocus() {
        super.unfocus();

        unselectText();
        this.cursor.setDisplayed(false);
        this.clickType = ClickType.NONE;
        this.lastBlinkTime = 0;
        RequestManager.getInstance().cancelRequest(RequestType.CHAR, this.charRequestId);
        RequestManager.getInstance().cancelRequest(RequestType.KEY, this.keyRequestId);
    }

    public void updateCursor() {
        if (this.clickType == ClickType.M1 && !this.textSelected) {
            if ((System.currentTimeMillis() - this.lastBlinkTime) > 1000) {
                this.lastBlinkTime += 1000;
                this.cursor.setDisplayed(!this.cursor.isDisplayed());
            }
        }
    }

    public GuiText getGuiText() {
        return this.guiText;
    }

    public String getText() {
        return this.guiText.getText().getTextString();
    }

    public void setCursorColor(Color color) {
        this.cursor.setTexture(new Background<>(color));
    }

    public void setTextColor(Color color) {
        this.guiText.getText().setColor(color);
    }

    public void setOnSend(SendTextCallback onSendTextCallback) {
        this.onSendTextCallback = onSendTextCallback;
    }

    @Override
    public boolean onMousePress(int button) {
        return false;
    }

    @Override
    public boolean onMouseRelease(int button) {
        return false;
    }

    @Override
    public void setOnMouseRelease(MouseReleaseCallback onMouseReleaseCallback) {

    }

    @Override
    public void setOnMousePress(MousePressCallback onMousePressCallback) {

    }

    @Override
    public boolean isReleaseInsideNeeded() {
        return false;
    }

    @Override
    public void setReleaseInsideNeeded(boolean releaseInsideNeeded) {

    }

    @Override
    public void reset() {

    }

    @Override
    public boolean isClicked() {
        return this.clickType != ClickType.NONE;
    }

    @Override
    public ClickType getClickType() {
        return this.clickType;
    }

    @Override
    public void setClickType(ClickType clickType) {
        this.clickType = clickType;
    }

    public interface SendTextCallback {

        void onSend(String text);
    }
}