package guis.presets;

import guis.Gui;
import guis.GuiComponent;
import guis.GuiInterface;
import guis.basics.GuiBasics;
import guis.constraints.GuiConstraintsManager;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiPreset extends GuiComponent {

    private List<GuiBasics> basics = new ArrayList<>();

    protected GuiPreset(GuiInterface parent, GuiConstraintsManager constraintsManager) {
        super(parent);

        setConstraints(constraintsManager);
    }

    public List<GuiBasics> getBasics() {
        return this.basics;
    }

    protected void addBasic(GuiBasics guiBasics) {
        this.basics.add(guiBasics);

//        if (getParent() instanceof Gui)
//            ((Gui) getParent()).addComponent(guiBasics);
    }

    protected void removeBasic(GuiBasics guiBasics) {
        this.basics.remove(guiBasics);
//
//        if (getParent() instanceof Gui)
//            ((Gui) getParent()).removeComponent(guiBasics);
    }

    @Override
    public void updateTexturePosition() {
        super.updateTexturePosition();

        this.basics.forEach(GuiComponent::updateTexturePosition);
    }

    public void setX(float x) {
        if (!(getParent() instanceof Gui))
            return;

        this.basics.forEach(guiBasics -> {
            guiBasics.setX(x);

            guiBasics.updateTexturePosition();
        });

        super.setX(x);

        updateTexturePosition();
    }

    public void setY(float y) {
        if (!(getParent() instanceof Gui))
            return;

        this.basics.forEach(guiBasics -> {
            guiBasics.setY(y);

            guiBasics.updateTexturePosition();
        });

        super.setY(y);

        updateTexturePosition();
    }

    @Override
    public void setStartX(float startX) {
        if ((startX < -1 || startX > 1) && getParentGui(this).areTransitionsOfComponentDone(this))
            throw new IllegalArgumentException("New coordinates don't belong in window");

        super.setStartX(startX);

        this.basics.forEach(guiBasics -> {
            guiBasics.setStartX(startX);

            guiBasics.updateTexturePosition();
        });
    }

    @Override
    public void setStartY(float startY) {
        if ((startY < -1 || startY > 1) && getParentGui(this).areTransitionsOfComponentDone(this))
            throw new IllegalArgumentException("New coordinates don't belong in window");

        super.setStartY(startY);

        this.basics.forEach(guiBasics -> {
            guiBasics.setStartY(startY);

            guiBasics.updateTexturePosition();
        });
    }

    @Override
    public void setFinalX(float finalX) {
        if ((finalX < -1 || finalX > 1) && getParentGui(this).areTransitionsOfComponentDone(this))
            throw new IllegalArgumentException("New coordinates don't belong in window");

        super.setFinalX(finalX);

        this.basics.forEach(guiBasics -> {
            guiBasics.setFinalX(finalX);

            guiBasics.updateTexturePosition();
        });
    }

    @Override
    public void setFinalY(float finalY) {
        if ((finalY < -1 || finalY > 1) && getParentGui(this).areTransitionsOfComponentDone(this))
            throw new IllegalArgumentException("New coordinates don't belong in window");

        super.setFinalY(finalY);

        this.basics.forEach(guiBasics -> {
            guiBasics.setFinalY(finalY);

            guiBasics.updateTexturePosition();
        });
    }
}
