package guis.presets;

import guis.Gui;
import guis.GuiComponent;
import guis.GuiInterface;
import guis.basics.GuiBasics;
import guis.constraints.GuiConstraintsManager;
import java.util.LinkedList;
import java.util.List;

public abstract class GuiPreset extends GuiComponent {

    private List<GuiBasics> basics = new LinkedList<>();;

    protected GuiPreset(GuiInterface parent, GuiConstraintsManager constraintsManager) {
        super(parent);

        setConstraints(constraintsManager);
    }

    public List<GuiBasics> getBasics() {
        return this.basics;
    }

    protected void addBasic(GuiBasics guiBasics) {
        this.basics.add(guiBasics);

        if (getParent() instanceof Gui)
            ((Gui) getParent()).addComponent(guiBasics);
    }

    protected void removeBasic(GuiBasics guiBasics) {
        this.basics.remove(guiBasics);

        if (getParent() instanceof Gui)
            ((Gui) getParent()).removeComponent(guiBasics);
    }

    @Override
    public void updateTexturePosition() {
        super.updateTexturePosition();

        this.basics.forEach(GuiComponent::updateTexturePosition);
    }
}
