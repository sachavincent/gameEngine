package guis.presets.checkbox.buttons;

import guis.GuiInterface;
import guis.basics.GuiBasics;
import guis.constraints.GuiConstraintsManager;
import guis.presets.GuiPreset;
import inputs.callbacks.ClickCallback;
import java.awt.Color;
import util.MouseUtils;

public abstract class GuiCheckbox extends GuiPreset {

    GuiBasics checkboxLayout;
    GuiBasics checkmark;


    GuiCheckbox(GuiInterface parent, Color color, GuiConstraintsManager constraintsManager) {
        super(parent, constraintsManager);

        addBackgroundComponent(color);
        setupComponents();
    }

    protected abstract void addBackgroundComponent(Color color);

    private void setupComponents() {
        setListeners();

        addBasic(this.checkboxLayout);
    }

    public void setCheckmark(GuiBasics checkmark) {
        if (checkmark.getBaseWidth() > checkboxLayout.getBaseWidth() ||
                checkmark.getBaseHeight() > checkboxLayout.getBaseHeight())
            throw new IllegalArgumentException("Checkmark out of checkbox bounds.");

        this.checkmark = checkmark;

        addBasic(this.checkmark);
    }

    private void setListeners() {
        checkboxLayout.setOnClick(() -> {
            System.out.println("Click");

            setClicked(true);

            updateTexturesOnClick();
        });

        checkboxLayout.setOnRelease(() -> {
            System.out.println("Release");

            if (isClicked() && MouseUtils.isCursorInGuiComponent(this) && checkmark != null)
                checkmark.setDisplayed(!checkmark.isDisplayed());

            setClicked(false);


            updateTexturesOnClick();
        });

        checkboxLayout.setOnHover(() -> {
//            System.out.println("Hover");
        });

        checkboxLayout.setOnLeave(() -> {
            System.out.println("Leave");
            checkboxLayout.updateTexturePosition();
        });

        checkboxLayout.setOnEnter(() -> {
            System.out.println("Enter");
            checkboxLayout.updateTexturePosition();
        });
    }

    private void updateTexturesOnClick() {
        this.checkboxLayout.updateTexturePosition();

        if (this.checkmark != null)
            this.checkmark.updateTexturePosition();
    }

    @Override
    public void setOnClick(ClickCallback onClickCallback) {
        checkboxLayout.setOnClick(onClickCallback);
    }
}
