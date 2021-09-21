package util;

import util.math.Vector3f;

public class Offset {

    private Vector3f offsetPosition;
    private int      offsetRotation;
    private float    offsetScale;

    private final boolean fixedRotation;

    public Offset(boolean fixedRotation) {
        this(new Vector3f(), 0, 0, fixedRotation);
    }

    public Offset() {
        this(false);
    }

    public Offset(Vector3f offsetPosition, int offsetRotation, float offsetScale) {
        this(offsetPosition, offsetRotation, offsetScale, false);
    }

    public Offset(Vector3f offsetPosition, int offsetRotation) {
        this(offsetPosition, offsetRotation, 0, false);
    }

    public Offset(Vector3f offsetPosition, boolean fixedRotation) {
        this(offsetPosition, 0, 0, fixedRotation);
    }

    public Offset(Vector3f offsetPosition) {
        this(offsetPosition, false);
    }

    public Offset(Vector3f offsetPosition, int offsetRotation, float offsetScale, boolean fixedRotation) {
        this.offsetPosition = offsetPosition;
        this.offsetRotation = offsetRotation;
        this.offsetScale = offsetScale;
        this.fixedRotation = fixedRotation;
    }

    public Vector3f getOffsetPosition() {
        return this.offsetPosition;
    }

    public void setOffsetPosition(Vector3f offsetPosition) {
        this.offsetPosition = offsetPosition;
    }

    public int getOffsetRotation() {
        return this.offsetRotation;
    }

    public void setOffsetRotation(int offsetRotation) {
        this.offsetRotation = offsetRotation;
    }

    public float getOffsetScale() {
        return this.offsetScale;
    }

    public void setOffsetScale(float offsetScale) {
        this.offsetScale = offsetScale;
    }

    public boolean isFixedRotation() {
        return this.fixedRotation;
    }
}