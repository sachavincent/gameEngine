package util.math;

import entities.Camera;
import java.util.Objects;

public class Plane3D {

    private Vector3f pointA;
    private Vector3f pointB;
    private Vector3f pointC;
    private Vector3f pointD;

    public Plane3D(Plane3D plane3D) {
        this.pointA = new Vector3f(plane3D.pointA);
        this.pointB = new Vector3f(plane3D.pointB);
        this.pointC = new Vector3f(plane3D.pointC);
        this.pointD = new Vector3f(plane3D.pointD);
    }

    public Plane3D(Vector3f pointA, Vector3f pointB, Vector3f pointC, Vector3f pointD) {
        this.pointA = pointA;
        this.pointB = pointB;
        this.pointC = pointC;
        this.pointD = pointD;
    }

    public static Plane3D planeFromTriangles(Triangle3D triangle1, Triangle3D triangle2) {
        Vector3f a1 = triangle1.getPointA();
        Vector3f b1 = triangle1.getPointB();
        Vector3f c1 = triangle1.getPointC();

        Vector3f a2 = triangle2.getPointA();
        Vector3f b2 = triangle2.getPointB();
        Vector3f c2 = triangle2.getPointC();

        boolean AEquals = false;
        boolean BEquals = false;
        boolean CEquals = false;

        Plane3D res;

        if (a1.equals(a2) || a1.equals(b2) || a1.equals(c2))
            AEquals = true;
        if (b1.equals(a2) || b1.equals(b2) || b1.equals(c2))
            BEquals = true;
        if (c1.equals(a2) || c1.equals(b2) || c1.equals(c2))
            CEquals = true;

        if (!AEquals && !BEquals && !CEquals)
            return null;
        if (AEquals && BEquals && CEquals)
            return null;

        Vector3f d;
        if (AEquals && BEquals) {
            if (a1.equals(a2) && b1.equals(b2))
                d = c2;
            else if (a1.equals(a2))
                d = b2;
            else if (b1.equals(b2))
                d = a2;
            else
                return null;

            res = new Plane3D(c1, a1, d, b1);
        } else if (AEquals && CEquals) {
            if (a1.equals(a2) && c1.equals(c2))
                d = b2;
            else if (a1.equals(a2))
                d = c2;
            else if (c1.equals(c2))
                d = a2;
            else
                return null;

            res = new Plane3D(b1, a1, d, c1);
        } else if (BEquals && CEquals) {
            if (b1.equals(b2) && c1.equals(c2))
                d = a2;
            else if (b1.equals(b2))
                d = a2;
            else if (c1.equals(c2))
                d = b2;
            else
                return null;

            res = new Plane3D(a1, b1, d, c1);
        } else
            return null;

        if (!res.isCoplanar())
            return null;

        if (!res.containsParallelLines())
            return null;

        return res;
    }

    public boolean containsParallelLines() {
        Vector3f AB = Vector3f.sub(pointB, pointA, null);
        Vector3f DC = Vector3f.sub(pointC, pointD, null);
        Vector3f BD = Vector3f.sub(pointD, pointB, null);
        Vector3f AC = Vector3f.sub(pointC, pointA, null);

        float angle = Vector3f.angle(AB, DC);
        float angle2 = Vector3f.angle(BD, AC);

        return angle == 0 || angle2 == 0;
    }

    public Vector3f getNormal() {
        Vector3f AB = Vector3f.sub(pointB, pointA, null);
        Vector3f AC = Vector3f.sub(pointC, pointA, null);
        return (Vector3f) Vector3f.cross(AB, AC, null).normalise();
    }

    public boolean isCoplanar() {
        float x1 = pointA.getX();
        float x2 = pointB.getX();
        float x3 = pointC.getX();
        float x4 = pointD.getX();
        float y1 = pointA.getY();
        float y2 = pointB.getY();
        float y3 = pointC.getY();
        float y4 = pointD.getY();
        float z1 = pointA.getZ();
        float z2 = pointB.getZ();
        float z3 = pointC.getZ();
        float z4 = pointD.getZ();

        float a1 = x2 - x1;
        float b1 = y2 - y1;
        float c1 = z2 - z1;
        float a2 = x3 - x1;
        float b2 = y3 - y1;
        float c2 = z3 - z1;
        float a = b1 * c2 - b2 * c1;
        float b = a2 * c1 - a1 * c2;
        float c = a1 * b2 - b1 * a2;
        float d = (-a * x1 - b * y1 - c * z1);

        return a * x4 + b * y4 + c * z4 + d == 0f;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Plane3D plane3D = (Plane3D) o;
        return (Objects.equals(pointA, plane3D.pointA) || Objects.equals(pointA, plane3D.pointB) ||
                Objects.equals(pointA, plane3D.pointC) || Objects.equals(pointA, plane3D.pointD)) &&
                (Objects.equals(pointB, plane3D.pointA) || Objects.equals(pointB, plane3D.pointB) ||
                        Objects.equals(pointB, plane3D.pointC) || Objects.equals(pointB, plane3D.pointD)) &&
                (Objects.equals(pointC, plane3D.pointA) || Objects.equals(pointC, plane3D.pointB) ||
                        Objects.equals(pointC, plane3D.pointC) || Objects.equals(pointC, plane3D.pointD)) &&
                (Objects.equals(pointD, plane3D.pointA) || Objects.equals(pointD, plane3D.pointB) ||
                        Objects.equals(pointD, plane3D.pointC) || Objects.equals(pointD, plane3D.pointD));
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointA, pointB, pointC, pointD);
    }

    @Override
    public String toString() {
        return "Plane3D{" +
                "pointA=" + pointA +
                ", pointB=" + pointB +
                ", pointC=" + pointC +
                ", pointD=" + pointD +
                '}';
    }

    public Vector3f getPointC() {
        return this.pointC;
    }

    public Vector3f getPointB() {
        return this.pointB;
    }

    public Vector3f getPointA() {
        return this.pointA;
    }

    public Vector3f getPointD() {
        return this.pointD;
    }

    public void setPointA(Vector3f pointA) {
        this.pointA = pointA;
    }

    public void setPointB(Vector3f pointB) {
        this.pointB = pointB;
    }

    public void setPointC(Vector3f pointC) {
        this.pointC = pointC;
    }

    public void setPointD(Vector3f pointD) {
        this.pointD = pointD;
    }

    public boolean isPointOnPlane(Vector3f point) {
//        Vector3f normal = getNormal();

//        boolean equationChecks =
//                (normal.getX() * (point.getX() - pointA.getX()) + normal.getY() * (point.getY() - pointA.getY()) + normal.getZ() * (point.getZ() - pointA.getZ())) == 0;

        float anglesum = 0;
        Vector3f[] points = new Vector3f[4];
        points[0] = pointA;
        points[1] = pointB;
        points[2] = pointC;
        points[3] = pointD;

        Vector3f p1 = new Vector3f();
        Vector3f p2 = new Vector3f();

        for (int i = 0; i < 4; i++) {
            p1.setX(points[i].getX() - point.getX());
            p1.setY(points[i].getY() - point.getY());
            p1.setZ(points[i].getZ() - point.getZ());
            p2.setX(points[(i + 1) % 4].getX() - point.getX());
            p2.setY(points[(i + 1) % 4].getY() - point.getY());
            p2.setZ(points[(i + 1) % 4].getZ() - point.getZ());

            float m1 = p1.length();
            float m2 = p2.length();
            if (m1 * m2 <= 0.000001)
                return false;

            anglesum += Math.toDegrees(Math.acos(Vector3f.dot(p1, p2) / (m1 * m2)));
        }

        return anglesum > 360 - 0.001 && anglesum < 360 + 0.001;
    }

    public Plane3D add(Vector3f vector) {
        Plane3D newPlane = new Plane3D(this);
        newPlane.pointA.add(vector);
        newPlane.pointB.add(vector);
        newPlane.pointC.add(vector);
        newPlane.pointD.add(vector);

        return newPlane;
    }

    public void rotate(int degree) {
        if (degree == 0)
            return;
//        Vector3f M = getNormal();
//        Vector3f N = new Vector3f();

        float c = (float) Math.round(Math.cos(Math.toRadians(degree)));
        float s = (float) Math.round(Math.sin(Math.toRadians(degree)));
//
//        N.setX(c * M.getX() - s * M.getZ();
//        N.setY(M.getY();
//        N.setZ(s * M.getX() + c * M.getZ();
//        if (M.equals(N)) {
        Vector3f newPointA = new Vector3f();
        Vector3f newPointB = new Vector3f();
        Vector3f newPointC = new Vector3f();
        Vector3f newPointD = new Vector3f();

        newPointA.setX(c * pointA.getX() - s * pointA.getZ());
        newPointA.setY(pointA.getY());
        newPointA.setZ(s * pointA.getX() + c * pointA.getZ());

        newPointB.setX(c * pointB.getX() - s * pointB.getZ());
        newPointB.setY(pointB.getY());
        newPointB.setZ(s * pointB.getX() + c * pointB.getZ());

        newPointC.setX(c * pointC.getX() - s * pointC.getZ());
        newPointC.setY(pointC.getY());
        newPointC.setZ(s * pointC.getX() + c * pointC.getZ());

        newPointD.setX(c * pointD.getX() - s * pointD.getZ());
        newPointD.setY(pointD.getY());
        newPointD.setZ(s * pointD.getX() + c * pointD.getZ());
        setPointA(newPointA);
        setPointB(newPointB);
        setPointC(newPointC);
        setPointD(newPointD);

        //        }
//        float costheta = Vector3f.dot(M, N) / (M.length() * N.length());
//        Vector3f axis = Vector3f.cross(M, N).normalize();
//        float sqrt = (float) Math.sqrt(1 - costheta * costheta);
//        Matrix3f rotationMatrix = new Matrix3f();
//        float v = 1 - costheta;
//        rotationMatrix.m00 = axis.getX() * axis.getX() * v + costheta;
//        rotationMatrix.m10 = axis.getX() * axis.getY() * v - axis.getZ() * sqrt;
//        rotationMatrix.m20 = axis.getX() * axis.getZ() * v + axis.getY() * sqrt;
//
//        rotationMatrix.m01 = axis.getY() * axis.getX() * v + axis.getZ() * sqrt;
//        rotationMatrix.m11 = axis.getY() * axis.getY() * v + costheta;
//        rotationMatrix.m21 = axis.getY() * axis.getZ() * v - axis.getX() * sqrt;
//
//        rotationMatrix.m02 = axis.getZ() * axis.getX() * v - axis.getY() * sqrt;
//        rotationMatrix.m12 = axis.getZ() * axis.getY() * v + axis.getX() * sqrt;
//        rotationMatrix.m22 = axis.getZ() * axis.getZ() * v + costheta;
//
//        pointA = Matrix3f.mul(rotationMatrix, pointA);
//        pointB = Matrix3f.mul(rotationMatrix, pointB);
//        pointC = Matrix3f.mul(rotationMatrix, pointC);
//        pointD = Matrix3f.mul(rotationMatrix, pointD);
//
//        newPointA.setX(c * pointA.getX() - s * pointA.getZ();
//        newPointA.setY(pointA.getY();
//        newPointA.setZ(s * pointA.getX() + c * pointA.getZ();
//
//        newPointB.setX(c * pointB.getX() - s * pointB.getZ();
//        newPointB.setY(pointB.getY();
//        newPointB.setZ(s * pointB.getX() + c * pointB.getZ();
//
//        newPointC.setX(c * pointC.getX() - s * pointC.getZ();
//        newPointC.setY(pointC.getY();
//        newPointC.setZ(s * pointC.getX() + c * pointC.getZ();
//
//        newPointD.setX(c * pointD.getX() - s * pointD.getZ();
//        newPointD.setY(pointD.getY();
//        newPointD.setZ(s * pointD.getX() + c * pointD.getZ();
//
//        setPointA(newPointA);
//        setPointB(newPointB);
//        setPointC(newPointC);
//        setPointD(newPointD);
    }

    public boolean doesLineIntersect(Vector3f line) {
        Vector3f origin = Camera.getInstance().getPosition();

        Vector3f normal = getNormal();
        float dot_dn = Vector3f.dot(line, normal);
        if (dot_dn == 0)
            return false;

        Vector3f pointPlane = this.pointA;
        float t = -(Vector3f.dot(Vector3f.sub(origin, pointPlane, null), normal)) / dot_dn;

        Vector3f P = Vector3f.add((Vector3f) line.scale(t), origin, null);
        P.format();

        return isPointOnPlane(P);
    }
}
