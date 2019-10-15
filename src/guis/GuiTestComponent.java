package guis;

public class GuiTestComponent extends GuiComponent {

    public GuiTestComponent(Gui parent, String texture) {
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
