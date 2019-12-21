package guis.presets.checkbox;

import guis.GuiInterface;
import guis.basics.GuiBasics;
import guis.constraints.GuiConstraintsManager;
import guis.presets.GuiPreset;
import java.awt.Color;
import util.MouseUtils;

public abstract class GuiAbstractCheckbox extends GuiPreset {

    GuiBasics checkboxLayout;
    GuiBasics checkmark;


    GuiAbstractCheckbox(GuiInterface parent, Color color, GuiConstraintsManager constraintsManager) {
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
        setOnClick(() -> {
            System.out.println("Click");

            setClicked(true);

            updateTexturesOnClick();
        });

        setOnRelease(() -> {
            System.out.println("Release");

            if (isClicked() && MouseUtils.isCursorInGuiComponent(this) && checkmark != null)
                checkmark.setDisplayed(!checkmark.isDisplayed());

            setClicked(false);

            updateTexturesOnClick();
        });

        setOnPress(() -> {
            System.out.println("Press");

            setClicked(false);

            updateTexturesOnClick();
        });

        setOnHover(() -> {
//            System.out.println("Hover");
        });

        setOnLeave(() -> {
            System.out.println("Leave");
            checkboxLayout.updateTexturePosition();
        });

        setOnEnter(() -> {
            System.out.println("Enter");
            checkboxLayout.updateTexturePosition();
        });
    }

    private void updateTexturesOnClick() {
        super.updateTexturePosition();

        this.checkboxLayout.updateTexturePosition();

        if (this.checkmark != null)
            this.checkmark.updateTexturePosition();
    }
//
//    @Override
//    public void setOnClick(ClickCallback onClickCallback) {
//        checkboxLayout.setOnClick(onClickCallback);
//    }
}