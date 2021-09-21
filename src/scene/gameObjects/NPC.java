package scene.gameObjects;

import engineTester.Rome;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import scene.components.LayerableComponent;
import scene.components.OffsetComponent;
import scene.components.PathComponent;
import scene.components.PedestrianComponent;
import scene.components.PedestrianComponent.Behavior;
import scene.components.SingleModelComponent;
import util.math.Vector3f;

public class NPC extends GameObject {

    public NPC() {
        addComponent(new SingleModelComponent(GameObjectDatas.NPC.getTexture()));
        addComponent(new OffsetComponent(new Vector3f(.5f, 0, .5f)));
        addComponent(new LayerableComponent(new HashSet<>(Collections.singletonList(DirtRoad.class))));
        addComponent(new PedestrianComponent(Behavior.TESTING));
        PathComponent pathComponent = new PathComponent();
        pathComponent.setOnTickElapsedCallback((gameObject, nbTicks) -> {
            pathComponent.moveForward();
            return true;
        });
        addComponent(pathComponent);
//        addComponent(new RendererComponent(NPCRenderer.getInstance()));
    }

    public static void updatePositions() {
        Set<GameObject> gameObjectsPathComponent = Rome.getGame().getScene()
                .getGameObjectsForComponent(PathComponent.class);
        gameObjectsPathComponent.stream()
                .filter(Objects::nonNull)
                .filter(gameObject -> gameObject.getClass() == NPC.class)
                .map(gameObject -> gameObject.getComponent(PathComponent.class))
                .filter(Objects::nonNull).forEach(PathComponent::moveForward);
    }
}
