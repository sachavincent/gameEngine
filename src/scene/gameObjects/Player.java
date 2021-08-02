package scene.gameObjects;

import entities.Camera.Direction;

public class Player {

    private static Class<? extends GameObject> selectedGameObject;
    private static Direction                   direction = Direction.defaultDirection();

    public static Class<? extends GameObject> getSelectedGameObject() {
        return Player.selectedGameObject;
    }

    public static Direction getDirection() {
        return direction;
    }

    public static void setDirection(Direction direction) {
        Player.direction = direction;
    }

    public static void removeSelectedGameObject() {
        Player.selectedGameObject = null;
    }

    public static boolean hasSelectedGameObject() {
        return Player.selectedGameObject != null;
    }

    public static void setSelectedGameObject(Class<? extends GameObject> selectedGameObject) {
        Player.selectedGameObject = selectedGameObject;
    }
}
