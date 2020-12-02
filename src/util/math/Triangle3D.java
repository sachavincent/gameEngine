package util.math;

import com.sun.istack.internal.NotNull;
import java.util.Objects;

public class Triangle3D {

    private final Vector3f pointA;
    private final Vector3f pointB;
    private final Vector3f pointC;

    public Triangle3D(@NotNull Vector3f pointA, @NotNull Vector3f pointB, @NotNull Vector3f pointC) {
        this.pointA = pointA;
        this.pointB = pointB;
        this.pointC = pointC;
    }

    public Vector3f getPointA() {
        return this.pointA;
    }

    public Vector3f getPointB() {
        return this.pointB;
    }

    public Vector3f getPointC() {
        return this.pointC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Triangle3D that = (Triangle3D) o;
        return (Objects.equals(pointA, that.pointA) || Objects.equals(pointA, that.pointB) ||
                Objects.equals(pointA, that.pointC)) &&
                (Objects.equals(pointB, that.pointA) || Objects.equals(pointB, that.pointB) ||
                        Objects.equals(pointB, that.pointC)) &&
                (Objects.equals(pointC, that.pointA) || Objects.equals(pointC, that.pointB) ||
                        Objects.equals(pointC, that.pointC));
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointA, pointB, pointC);
    }
}
