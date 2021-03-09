package entities;

import renderEngine.FrustumCullingFilter;
import terrains.TerrainPosition;
import util.MousePicker;
import util.math.Vector3f;

public class Camera {

    public final static  double MOVING_STEP = .3;
    private static final int    MAX_ZOOM    = 10;

    private final Vector3f position = new Vector3f(1, 20, 1);
    private       float    pitch    = 20;
    private       int      yaw;
    private       float    roll;

    private Direction direction;

    public static Camera instance;

    private Vector3f movingFactor = new Vector3f(0, 0, 0);

    private float   distanceFromTerrainPoint;
    private int     angleAroundTerrainPoint;
    private boolean distanceFromTerrainPointChanged;

    public Vector3f getFocusPoint() {
        return this.focusPoint;
    }

    public void setDistanceFromTerrainPoint(float distanceFromTerrainPoint) {
        this.distanceFromTerrainPoint = Math.max(MAX_ZOOM, distanceFromTerrainPoint);
    }

    private Vector3f focusPoint;

    private Camera() {
        setYaw(90);
    }

    public static Camera getInstance() {
        return instance == null ? (instance = new Camera()) : instance;
    }


    public void setPitch(float pitch) {
        this.pitch = pitch;
        if (this.pitch > 90)
            this.pitch = 90;
    }

    public int getAngleAroundTerrainPoint() {
        return this.angleAroundTerrainPoint;
    }

    public void setYaw(int yaw) {
        yaw = (yaw < 0 ? yaw + 360 : yaw) % 360;

        this.angleAroundTerrainPoint = yaw;
        this.yaw = angleAroundTerrainPoint;

        updateDirection();
    }

    public void setFocusPoint(Vector3f focusPoint) {
        this.focusPoint = focusPoint;
    }

    public void move() {
        this.position.x += movingFactor.x;
        this.position.y += movingFactor.y;
        this.position.z += movingFactor.z;
        if (!movingFactor.equals(new Vector3f(0, 0, 0))) {
            focusPoint = MousePicker.getInstance().getCurrentTerrainPoint();
            FrustumCullingFilter.updateFrustum();
        }

        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
        if (focusPoint != null && distanceFromTerrainPointChanged) {
            float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(angleAroundTerrainPoint)));
            float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(angleAroundTerrainPoint)));
            position.x = focusPoint.x - offsetX;
            position.z = focusPoint.z + offsetZ;
            position.y = focusPoint.y + verticalDistance;
            distanceFromTerrainPointChanged = false;
        }
    }

    private void updateDirection() {
        this.direction = Direction.getDirectionFromDegree(this.yaw);
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public int getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromTerrainPoint * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromTerrainPoint * Math.sin(Math.toRadians(pitch)));
    }

    public void moveTo(int degree) {
        if (degree == -1) { // Reset movement
            resetMovement();

            return;
        }

        float xFactor = 1;
        float zFactor = 1;
        switch (this.direction) {
            case NORTH:
                zFactor = (float) (MOVING_STEP * -Math.cos(Math.toRadians(360 - degree)));
                if (yaw > 0)
                    xFactor = (float) (MOVING_STEP * Math.sin(Math.toRadians(degree)));
                else
                    xFactor = (float) (MOVING_STEP * -Math.sin(Math.toRadians(degree)));
                break;
            case SOUTH:
                zFactor = (float) (MOVING_STEP * -Math.cos(Math.toRadians(360 - degree)));
                xFactor = (float) (MOVING_STEP * Math.sin(Math.toRadians(degree)));
                break;
            case EAST:
            case WEST:
                xFactor = (float) (MOVING_STEP * Math.sin(Math.toRadians(degree)));
                zFactor = (float) (MOVING_STEP * -Math.cos(Math.toRadians(degree)));
                break;
        }
        this.movingFactor.x = xFactor;
        this.movingFactor.z = zFactor;
    }

    public void resetMovement() {
        this.movingFactor = new Vector3f(0, 0, 0);
    }

    /**
     * Move towards the terrain point
     */
    public void moveCloser() {
//        System.out.println("moving closer");
//        Vector3f terrainPoint = MousePicker.getInstance().getCurrentTerrainPoint();
//        if (terrainPoint == null)
//            return;
//
//        double distance = terrainPoint.distance(position);
//
//        if (distance > 0) {
//            double newDistance = Math.max(MAX_ZOOM, distance - 1d);
//
////            System.out.println("NEwD : " + newDistance + " from " + terrainPoint);
////            Vector3f dir = new Vector3f(
////                    -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
////                    Math.sin(Math.toRadians(pitch)),
////                    Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
////            position.x += (float) (newDistance * dir.x);
////            position.y += (float) (newDistance * dir.y);
////            position.z += (float) (newDistance * dir.z);
////
////            System.out.println("New position: " + position);
//
//
//            float horizontalDistance = (float) (newDistance * Math.cos(Math.toRadians(pitch)));
//            float verticalDistance = (float) (newDistance * Math.sin(Math.toRadians(pitch)));
//
//            float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(yaw)));
//            float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(yaw)));
//
//            position.x = terrainPoint.x - offsetX;
//            position.z = terrainPoint.z - offsetZ;
//            position.y = terrainPoint.y + verticalDistance;
//        }
        setDistanceFromTerrainPoint(distanceFromTerrainPoint - 1);
        distanceFromTerrainPointChanged = true;
    }

    /**
     * Move further from the terrain point
     */
    public void moveFurther() {
//        System.out.println("moving further, yaw: " + yaw);
//        Vector3f terrainPoint = MousePicker.getInstance().getCurrentTerrainPoint();
//        if (terrainPoint == null)
//            return;
//
//        terrainPoint.y = terrainPoint.y - 0.01f;
//        double distance = terrainPoint.distance(position);
//
//        if (distance > 0) {
//            double newDistance = distance + 1f;
//
////            System.out.println("NEwD : " + newDistance + " from " + terrainPoint);
////            Vector3f dir = new Vector3f(
////                    -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
////                    Math.sin(Math.toRadians(pitch)),
////                    Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
////            position.x += (float) (newDistance * dir.x);
////            position.y += (float) (newDistance * dir.y);
////            position.z += (float) (newDistance * dir.z);
////
////            System.out.println("New position: " + position);
//
//
//            DecimalFormat df = new DecimalFormat("#.##");
//            double horizontalDistance =
//                    newDistance * Double.parseDouble(df.format(Math.cos(Math.toRadians(pitch))).replace(',', '.'));
//            double verticalDistance =
//                    newDistance * Double.parseDouble(df.format(Math.sin(Math.toRadians(pitch))).replace(',', '.'));
//
//            System.out.println("horizontal: " + horizontalDistance);
//            System.out.println(terrainPoint);
//            System.out.println("pitch : " + pitch);
//            System.out.println("distance: " + distance);
//            float offsetX = (float) (horizontalDistance * Math.cos(Math.toRadians(yaw)));
//            float offsetZ = (float) (horizontalDistance * Math.sin(Math.toRadians(yaw)));
//
//            position.x = position.x  + offsetX;
//            position.z =  position.z  + offsetZ;
//            position.y = (float) (position.y + verticalDistance);
//        }
        setDistanceFromTerrainPoint(distanceFromTerrainPoint + 1);
        distanceFromTerrainPointChanged = true;
    }

    public enum Direction {
        WEST(0),
        NORTH(90),
        EAST(180),
        SOUTH(270);

        int degree;

        Direction(int degree) {
            this.degree = degree;
        }

        public int getDegree() {
            return this.degree;
        }

        public static Direction getDirectionFromDegree(int degree) {
            return values()[(int) Math.round((((double) (degree < 0 ? degree + 360 : degree) % 360) / 90)) % 4];
//            return Arrays.stream(values()).filter(direction -> direction.degree == (degree % 360))
//                    .findFirst().orElse(null);
        }

        public static Direction[] any() {
            return Direction.values();
        }

        public Direction getOppositeDirection() {
            return getDirectionFromDegree(degree + 180);
        }

        public static TerrainPosition toRelativeDistance(Direction direction) {
            switch (direction) {
                case WEST:
                    return new TerrainPosition(0, -1);
                case NORTH:
                    return new TerrainPosition(1, 0);
                case EAST:
                    return new TerrainPosition(0, 1);
                case SOUTH:
                    return new TerrainPosition(-1, 0);
                default:
                    return new TerrainPosition(0, 0);
            }
        }

        public static TerrainPosition toRelativeDistance(Direction direction, int multiple) {
            switch (direction) {
                case WEST:
                    return new TerrainPosition(0, -multiple);
                case NORTH:
                    return new TerrainPosition(multiple, 0);
                case EAST:
                    return new TerrainPosition(0, multiple);
                case SOUTH:
                    return new TerrainPosition(-multiple, 0);
                default:
                    return new TerrainPosition(0, 0);
            }
        }
    }
}
