package items;

import models.TexturedModel;
import terrains.Terrain;
import util.math.Vector3f;

public abstract class Item {

    private static long max_id = 1;

    protected String name;
    protected long   id;

    protected TexturedModel texture;
    protected TexturedModel boundingBox;
    protected TexturedModel selectionBox;

    protected int   rotation;
    protected float scale = 1f;

    protected boolean selected;

    public boolean isSelected() {
        return this.selected;
    }

    public void select() {
        this.selected = true;
    }

    public void unselect() {
        this.selected = false;
    }

    public Item() {
        this.id = max_id++;
        System.out.println(getClass());
    }

    public String getName() {
        return this.name;
    }

    public long getId() {
        return this.id;
    }

    public TexturedModel getTexture() {
        return texture;
    }

    public void setTexture(TexturedModel texture) {
        this.texture = texture;
    }

    public int getRotation() {
        return this.rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation % 360;
    }

    public float getScale() {
        return this.scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public TexturedModel getSelectionBox() {
        return this.selectionBox;
    }

    public TexturedModel getBoundingBox() {
        return this.boundingBox;
    }

    public void setBoundingBox(TexturedModel boundingBox) {
        this.boundingBox = boundingBox;
    }

    public void setSelectionBox(TexturedModel selectionBox) {
        this.selectionBox = selectionBox;
    }

    public abstract void place(Terrain terrain, Vector3f position);
}
