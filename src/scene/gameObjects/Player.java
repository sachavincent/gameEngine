package scene.gameObjects;

public class Player {

    private static Class<? extends GameObject> selectedGameObject;

    public static Class<? extends GameObject> getSelectedGameObject() {
        return Player.selectedGameObject;
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
