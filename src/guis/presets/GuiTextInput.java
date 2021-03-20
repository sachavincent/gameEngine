package guis.presets;

import static guis.Gui.DEFAULT_FONT;
import static org.lwjgl.glfw.GLFW.*;

import fontMeshCreator.Character;
import fontMeshCreator.Text;
import guis.Gui;
import guis.GuiInterface;
import guis.GuiTexture;
import guis.basics.GuiRectangle;
import guis.basics.GuiText;
import guis.constraints.*;
import guis.presets.buttons.GuiRectangleButton;
import inputs.MouseUtils;
import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;
import util.math.Vector2f;

public class GuiTextInput extends GuiPreset {

    private final GuiRectangleButton outline;
    private final GuiRectangle       cursor;
    private final GuiText            guiText;

    private long lastBlinkTime;

    private int cursorPosition;

    private int maxLength = Integer.MAX_VALUE;

    private final float   minCursorXPosition;
    private       boolean leftControlKeyPressed;
    private       boolean rightControlKeyPressed;

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
        this.guiText.getText()
                .setPosition(new Vector2f(this.minCursorXPosition, (this.guiText.getText().getPosition().y - 0.5) * 2));

        this.cursor.setCornerRadius(0);
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
     * return true if input is processed
     */
    public boolean processKeyboardInput(int action, int key) {
        Text text = this.guiText.getText();
        String content = text.getTextString();

        if (action == GLFW_PRESS || action == GLFW_REPEAT) {
            switch (key) {
                case GLFW_KEY_LEFT_CONTROL:
                    this.leftControlKeyPressed = true;
                    return true;
                case GLFW_KEY_RIGHT_CONTROL:
                    this.rightControlKeyPressed = true;
                    return true;
                case GLFW_KEY_SPACE:
                    if (this.cursorPosition < this.maxLength) {
                        if (this.cursorPosition > 0)
                            text.setTextString(content.substring(0, this.cursorPosition) + " " +
                                    content.substring(this.cursorPosition));
                        else
                            text.setTextString(" " + content);
                        this.cursor.setX((float) (this.cursor.getX() +
                                text.getFont().getLoader().getMetaData().getSpaceWidth() * 2 * text.getFontSize()));

                        this.cursorPosition++;
                    }
                    return true;
                case GLFW_KEY_ESCAPE:
                    unfocus();
                    return true;
                case GLFW_KEY_BACKSPACE:
                    if (!content.isEmpty() && this.cursorPosition > 0) {
                        shiftCursorLeft();
                        StringBuilder builder = new StringBuilder(content);
                        builder.deleteCharAt(this.cursorPosition);
                        text.setTextString(builder.toString());
                    }
                    return true;
                case GLFW_KEY_DELETE:
                    if (!content.isEmpty()) {
                        StringBuilder builder = new StringBuilder(content);
                        if (content.length() > this.cursorPosition) {
                            builder.deleteCharAt(this.cursorPosition);
                            text.setTextString(builder.toString());
                        }
                    }
                    return true;
                case GLFW_KEY_LEFT:
                    shiftCursorLeft();
                    return true;
                case GLFW_KEY_RIGHT:
                    shiftCursorRight();
                    return true;
                case GLFW_KEY_HOME:
                    this.cursorPosition = 0;
                    this.cursor.setX(this.minCursorXPosition);
                    return true;
                case GLFW_KEY_END:
                    this.cursorPosition = content.length();
                    this.cursor.setX(this.minCursorXPosition + getTextSize());
                    return true;
                default: // Any character
                    if (action == GLFW_PRESS && key == GLFW_KEY_A && isAnyControlKeyPressed()) {
                        //TODO: Handle select all
                        return true;
                    }

                    if (this.cursorPosition < this.maxLength) {
                        String keyText = glfwGetKeyName(key, 0);
                        if (keyText == null || keyText.length() > 1)
                            return false;

                        char character = keyText.charAt(0);
                        Character c = text.getFont().getLoader().getMetaData()
                                .getCharacter(character);
                        if (c == null) //TODO: Accents ne fonctionnent pas
                            return false;

                        if (this.cursorPosition > 0)
                            text.setTextString(content.substring(0, this.cursorPosition) + keyText +
                                    content.substring(this.cursorPosition));
                        else
                            text.setTextString(keyText + content);

                        this.cursor.setX((float) (this.cursor.getX() + c.getxAdvance() * 2 * text.getFontSize()));
                        this.cursorPosition++;
                    }
                    return true;
            }
        } else if (action == GLFW_RELEASE) {
            switch (key) {
                case GLFW_KEY_LEFT_CONTROL:
                    this.leftControlKeyPressed = false;
                    return true;
                case GLFW_KEY_RIGHT_CONTROL:
                    this.rightControlKeyPressed = false;
                    return true;
                default:
                    return true;
            }
        }
        return false;
    }

    private boolean isAnyControlKeyPressed() {
        return this.rightControlKeyPressed || this.leftControlKeyPressed;
    }

    private void shiftCursorLeft() {
        if (this.cursorPosition > 0) {
            Text text = this.guiText.getText();
            String content = text.getTextString();

            this.cursor.setX((float) (this.cursor.getX() - text.getFont().getLoader().getMetaData()
                    .getCharacter(content.charAt(this.cursorPosition - 1)).getxAdvance() * 2 *
                    text.getFontSize()));

            this.cursorPosition--;
        }
    }

    private void shiftCursorRight() {
        Text text = this.guiText.getText();
        String content = text.getTextString();

        if (this.cursorPosition < content.length()) {
            this.cursor.setX((float) (this.cursor.getX() + text.getFont().getLoader().getMetaData()
                    .getCharacter(content.charAt(this.cursorPosition)).getxAdvance() * 2 *
                    text.getFontSize()));

            this.cursorPosition++;
        }
    }

    private float getTextSize() {
        String textString = this.guiText.getText().getTextString();
        return (float) textString.chars().mapToDouble(c -> {
            Character character = this.guiText.getText().getFont().getLoader().getMetaData().getCharacter(c);
            return character.getxAdvance() * 2 * this.guiText.getText().getFontSize();
        }).sum();
    }

    public void setOutlineConstraints(GuiConstraints widthConstraint, GuiConstraints heightConstraint) {
        GuiConstraintHandler handler = new GuiConstraintHandler(this, this.outline);

        this.outline.setWidth(handler.handleWidthConstraint(widthConstraint));
        this.outline.setHeight(handler.handleHeightConstraint(heightConstraint));

        this.guiText.setWidth(handler.handleWidthConstraint(widthConstraint));
        this.guiText.setHeight(handler.handleHeightConstraint(heightConstraint));

        handler = new GuiConstraintHandler(this.outline, this.cursor);
        this.cursor.setHeight(handler.handleHeightConstraint(new RelativeConstraint(.8f, this.outline)));
        this.cursor.setDisplayed(false);

        this.outline.setOnPress(() -> {
            if (!this.clicked) { //Start blinking effect
                this.lastBlinkTime = System.currentTimeMillis();
                this.clicked = true;
                this.cursor.setDisplayed(true);
            }

            float xCursor = MouseUtils.getCursorPos().x;
            if (xCursor > this.minCursorXPosition) {
                if (xCursor > getCursorMaxPosition()) {
                    this.cursor.setX(getCursorMaxPosition());
                    this.cursorPosition = this.guiText.getText().getTextString().length();
                } else {
                    String textString = this.guiText.getText().getTextString();
                    List<Double> characterWidths = textString.chars().mapToDouble(c -> {
                        Character character = this.guiText.getText().getFont().getLoader().getMetaData()
                                .getCharacter(c);
                        return character.getxAdvance() * 2 * this.guiText.getText().getFontSize();
                    }).boxed().collect(Collectors.toList());
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
        return this.minCursorXPosition + getTextSize();
    }

    public void unfocus() {
        this.cursor.setDisplayed(false);
        this.clicked = false;
        this.lastBlinkTime = 0;
    }

    public void updateCursor() {
        if (this.clicked) {
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
