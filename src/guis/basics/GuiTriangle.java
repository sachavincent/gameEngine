package guis.basics;

import guis.GuiInterface;
import guis.constraints.GuiConstraints;
import guis.constraints.GuiConstraintsManager;
import guis.presets.Background;
import models.RawModel;
import renderEngine.Loader;

public class GuiTriangle extends GuiShape {

    public final static int[] POSITIONS_0   = {-1, -1, 1, -1, 0, 1};
    public final static int[] POSITIONS_90  = {-1, 1, -1, -1, 1, 0};
    public final static int[] POSITIONS_180 = {1, 1, -1, 1, 0, -1};
    public final static int[] POSITIONS_270 = {1, 1, -1, 0, 1, -1};

    public static RawModel[] TRIANGLES = new RawModel[]{
            Loader.getInstance().loadToVAO(POSITIONS_0, 2),
            Loader.getInstance().loadToVAO(POSITIONS_90, 2),
            Loader.getInstance().loadToVAO(POSITIONS_180, 2),
            Loader.getInstance().loadToVAO(POSITIONS_270, 2)
    };

    private int rotation;

    public GuiTriangle(GuiInterface gui, Background<?> background, GuiConstraints width, GuiConstraints height) {
        this(gui, background, width, height, true);
    }

    public GuiTriangle(GuiInterface gui, Background<?> background, GuiConstraints width, GuiConstraints height,
            int rotation) {
        this(gui, background, width, height, true, rotation);
    }

    public GuiTriangle(GuiInterface gui, Background<?> background, GuiConstraints width, GuiConstraints height,
            boolean filled, int rotation) {
        this(gui, background, width, height, filled);

        setRotation(rotation);
    }

    public GuiTriangle(GuiInterface gui, Background<?> background, GuiConstraints width, GuiConstraints height,
            boolean filled) {
        super(gui, background, width, height, filled);
    }

    public GuiTriangle(GuiInterface gui, Background<?> background, GuiConstraintsManager guiConstraintsManager) {
        this(gui, background, guiConstraintsManager, true);
    }

    public GuiTriangle(GuiInterface gui, Background<?> background, GuiConstraintsManager guiConstraintsManager,
            boolean filled) {
        super(gui, background, guiConstraintsManager, filled);
    }

    public GuiTriangle(GuiInterface gui, Background<?> background, boolean filled) {
        this(gui, background, null, filled);
    }

    public GuiTriangle(GuiInterface gui, Background<?> background, int rotation) {
        this(gui, background, rotation, null);
    }

    public GuiTriangle(GuiInterface gui, Background<?> background, int rotation,
            GuiConstraintsManager guiConstraintsManager) {
        this(gui, background, rotation, guiConstraintsManager, true);
    }

    public GuiTriangle(GuiInterface gui, Background<?> background, int rotation,
            GuiConstraintsManager guiConstraintsManager, boolean filled) {
        this(gui, background, guiConstraintsManager, filled);

        setRotation(rotation);
    }

    public GuiTriangle(GuiInterface gui, Background<?> background, int rotation, boolean filled) {
        this(gui, background, rotation, null, filled);
    }

    @Override
    public double getOutlineWidth() {
        return 0.1d;
    }

    @Override
    public float getCornerRadius() {
        return 0;
    }

    public void setRotation(int angle) {
        if (angle < 0)
            return;

        this.rotation = angle / 90;
    }

    public int getRotation() {
        return this.rotation;
    }

    @Override
    public RawModel getTemplate() {
        return TRIANGLES[this.rotation];
    }
}
