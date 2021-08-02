package guis.prefabs.GuiHouseDetails;

import static guis.Gui.DEFAULT_FONT;

import fontMeshCreator.Text;
import guis.GuiTexture;
import guis.basics.GuiRectangle;
import guis.basics.GuiText;
import guis.constraints.CenterConstraint;
import guis.constraints.GuiConstraintsManager;
import guis.constraints.RelativeConstraint;
import guis.constraints.layout.RatioedPatternLayout;
import guis.presets.Background;
import guis.presets.GuiProgressIcon;
import java.awt.Color;
import java.util.List;
import resources.ResourceManager.Resource;
import resources.ResourceType;

/**
 * Progress Icon & Percentage at the center
 */
public class CategoryView extends GuiRectangle {

    public final GuiProgressIcon categoryIcon;
    public final GuiText         categoryPercentage;

    private final static GuiConstraintsManager CONSTRAINTS = new GuiConstraintsManager.Builder()
            .setWidthConstraint(new RelativeConstraint(1))
            .setHeightConstraint(new RelativeConstraint(.6f))
            .setxConstraint(new CenterConstraint())
            .setyConstraint(new CenterConstraint())
            .create();

    public CategoryView(GuiHouseDetails gui) {
        super(gui, Background.NO_BACKGROUND, CONSTRAINTS);

        setLayout(new RatioedPatternLayout(2, 1, 0.03f, 0, 70f, -1f, 30f, -1f));

        this.categoryIcon = new GuiProgressIcon(this, Resource.FISH.getBackgroundTexture(), null);
        List<Resource> foodResources = Resource.getResourceOfType(ResourceType.FOOD);
        for (Resource r : foodResources) {
            if (r == Resource.FISH)
                continue;
            this.categoryIcon.addTexture(new GuiTexture(r.getBackgroundTexture(), this.categoryIcon));
        }

        Text text = new Text("0%", 1.5f, DEFAULT_FONT, Color.BLACK);

        this.categoryPercentage = new GuiText(this, text);
    }
}
