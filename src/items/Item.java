package items;

import entities.Camera.Direction;
import java.util.Objects;
import models.BoundingBox;
import models.TexturedModel;
import terrains.TerrainPosition;

public abstract class Item {

    private static long max_id = 1;

    protected final long id;

    protected String name;

    protected int xNegativeOffset, xPositiveOffset, height, zNegativeOffset, zPositiveOffset;

    protected TexturedModel texture;
    protected TexturedModel previewTexture;

    protected BoundingBox   boundingBox;
    protected TexturedModel selectionBox;

    protected Direction facingDirection = Direction.NORTH;
    protected float     scale           = 1f;

    protected boolean selected;

    private TerrainPosition terrainPosition;

    public Item(TerrainPosition terrainPosition, String name, int xNegativeOffset, int xPositiveOffset, int height,
            int zNegativeOffset, int zPositiveOffset) {
        this.terrainPosition = terrainPosition;

        this.xNegativeOffset = xNegativeOffset;
        this.xPositiveOffset = xPositiveOffset;

        this.height = height;

        this.zNegativeOffset = zNegativeOffset;
        this.zPositiveOffset = zPositiveOffset;

        this.id = max_id++;

        this.name = name;
    }

    public Item(Item parent, TerrainPosition position) {
        this.terrainPosition = parent.terrainPosition.add(position);
        this.name = parent.getName();
        this.id = parent.getId();
    }

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

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public void setSelectionBox(TexturedModel selectionBox) {
        this.selectionBox = selectionBox;
    }

    public int getxNegativeOffset() {
        return this.xNegativeOffset;
    }

    public int getxPositiveOffset() {
        return this.xPositiveOffset;
    }

    public int getzNegativeOffset() {
        return this.zNegativeOffset;
    }

    public int getzPositiveOffset() {
        return this.zPositiveOffset;
    }

    public TerrainPosition getOffset(Direction direction) {
        TerrainPosition res = new TerrainPosition(0, 0);
        switch (direction) {
            case NORTH:
                res.setX(this.xPositiveOffset);
                break;
            case SOUTH:
                res.setX(-this.xNegativeOffset);
                break;
            case WEST:
                res.setZ(-this.zNegativeOffset);
                break;
            case EAST:
                res.setZ(this.zPositiveOffset);
                break;
        }
        return res;
    }

    public int getHeight() {
        return this.height;
    }

    public TerrainPosition getPosition() {
        return this.terrainPosition;
    }

    public void setPosition(TerrainPosition terrainPosition) {
        this.terrainPosition = terrainPosition;
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
