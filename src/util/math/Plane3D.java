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
        float x1 = pointA.x;
        float x2 = pointB.x;
        float x3 = pointC.x;
        float x4 = pointD.x;
        float y1 = pointA.y;
        float y2 = pointB.y;
        float y3 = pointC.y;
        float y4 = pointD.y;
        float z1 = pointA.z;
        float z2 = pointB.z;
        float z3 = pointC.z;
        float z4 = pointD.z;

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
//                (normal.x * (point.x - pointA.x) + normal.y * (point.y - pointA.y) + normal.z * (point.z - pointA.z)) == 0;

        float anglesum = 0;
        Vector3f[] points = new Vector3f[4];
        points[0] = pointA;
        points[1] = pointB;
        points[2] = pointC;
        points[3] = pointD;

        Vector3f p1 = new Vector3f();
        Vector3f p2 = new Vector3f();

        for (int i = 0; i < 4; i++) {
            p1.x = points[i].x - point.x;
            p1.y = points[i].y - point.y;
            p1.z = points[i].z - point.z;
            p2.x = points[(i + 1) % 4].x - point.x;
            p2.y = points[(i + 1) % 4].y - point.y;
            p2.z = points[(i + 1) % 4].z - point.z;

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
        newPlane.pointA = newPlane.pointA.add(vector);
        newPlane.pointB = newPlane.pointB.add(vector);
        newPlane.pointC = newPlane.pointC.add(vector);
        newPlane.pointD = newPlane.pointD.add(vector);

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
//        N.x = c * M.x - s * M.z;
//        N.y = M.y;
//        N.z = s * M.x + c * M.z;
//        if (M.equals(N)) {
        Vector3f newPointA = new Vector3f();
        Vector3f newPointB = new Vector3f();
        Vector3f newPointC = new Vector3f();
        Vector3f newPointD = new Vector3f();

        newPointA.x = c * pointA.x - s * pointA.z;
        newPointA.y = pointA.y;
        newPointA.z = s * pointA.x + c * pointA.z;

        newPointB.x = c * pointB.x - s * pointB.z;
        newPointB.y = pointB.y;
        newPointB.z = s * pointB.x + c * pointB.z;

        newPointC.x = c * pointC.x - s * pointC.z;
        newPointC.y = pointC.y;
        newPointC.z = s * pointC.x + c * pointC.z;

        newPointD.x = c * pointD.x - s * pointD.z;
        newPointD.y = pointD.y;
        newPointD.z = s * pointD.x + c * pointD.z;
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
//        rotationMatrix.m00 = axis.x * axis.x * v + costheta;
//        rotationMatrix.m10 = axis.x * axis.y * v - axis.z * sqrt;
//        rotationMatrix.m20 = axis.x * axis.z * v + axis.y * sqrt;
//
//        rotationMatrix.m01 = axis.y * axis.x * v + axis.z * sqrt;
//        rotationMatrix.m11 = axis.y * axis.y * v + costheta;
//        rotationMatrix.m21 = axis.y * axis.z * v - axis.x * sqrt;
//
//        rotationMatrix.m02 = axis.z * axis.x * v - axis.y * sqrt;
//        rotationMatrix.m12 = axis.z * axis.y * v + axis.x * sqrt;
//        rotationMatrix.m22 = axis.z * axis.z * v + costheta;
//
//        pointA = Matrix3f.mul(rotationMatrix, pointA);
//        pointB = Matrix3f.mul(rotationMatrix, pointB);
//        pointC = Matrix3f.mul(rotationMatrix, pointC);
//        pointD = Matrix3f.mul(rotationMatrix, pointD);
//
//        newPointA.x = c * pointA.x - s * pointA.z;
//        newPointA.y = pointA.y;
//        newPointA.z = s * pointA.x + c * pointA.z;
//
//        newPointB.x = c * pointB.x - s * pointB.z;
//        newPointB.y = pointB.y;
//        newPointB.z = s * pointB.x + c * pointB.z;
//
//        newPointC.x = c * pointC.x - s * pointC.z;
//        newPointC.y = pointC.y;
//        newPointC.z = s * pointC.x + c * pointC.z;
//
//        newPointD.x = c * pointD.x - s * pointD.z;
//        newPointD.y = pointD.y;
//        newPointD.z = s * pointD.x + c * pointD.z;
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
