package guis.components;

import guis.Gui;

public class GuiSlider extends GuiComponent {

    public GuiSlider(Gui parent, String texture) {
        super(parent, texture);
    }

    @Override
    public void onClick() {
        System.out.println("Click");
    }

    @Override
    public void onHover() {

    }

    @Override
    public void onScroll() {

    }

    @Override
    public void onType() {

    }


}
