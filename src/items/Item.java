package items;

import entities.Camera.Direction;
import java.util.Objects;
import models.TexturedModel;

public abstract class Item {

    private static long max_id = 1;

    protected String name;
    protected long   id;

    protected int xWidth, height, zWidth;

    protected TexturedModel texture;
    protected TexturedModel previewTexture;

    protected TexturedModel boundingBox;
    protected TexturedModel selectionBox;

    protected Direction facingDirection = Direction.NORTH;
    protected float     scale = 1f;

    protected boolean selected;

    public TexturedModel getPreviewTexture() {
        return this.previewTexture;
    }

    public void setPreviewTexture(TexturedModel previewTexture) {
        this.previewTexture = previewTexture;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void select() {
        this.selected = true;
    }

    public void unselect() {
        this.selected = false;
    }

    public Item(int xWidth, int height, int zWidth) {
        this.xWidth = xWidth;
        this.height = height;
        this.zWidth = zWidth;

        this.id = max_id++;
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

    public Direction getFacingDirection() {
        return this.facingDirection;
    }

    public void setFacingDirection(Direction facingDirection) {
        this.facingDirection = facingDirection;
    }

    public void setRotation(int rotation) {
        this.facingDirection = Direction.getDirectionFromDegree(rotation);
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

    public int getxWidth() {
        return this.xWidth;
    }

    public int getHeight() {
        return this.height;
    }

    public int getzWidth() {
        return this.zWidth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Item item = (Item) o;
        return id == item.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
