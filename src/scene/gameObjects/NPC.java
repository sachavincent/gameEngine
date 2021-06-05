package scene.gameObjects;

import items.OBJGameObjects;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import renderEngine.NPCRenderer;
import scene.Scene;
import scene.components.LayerableComponent;
import scene.components.OffsetComponent;
import scene.components.PathComponent;
import scene.components.PedestrianComponent;
import scene.components.PedestrianComponent.Behavior;
import scene.components.RendererComponent;
import scene.components.TextureComponent;
import util.math.Vector3f;

public class NPC extends GameObject {

    public NPC() {
        addComponent(new TextureComponent(OBJGameObjects.NPC.getTexture()));
        addComponent(new OffsetComponent(new Vector3f(.5f, 0, .5f)));
        addComponent(new RendererComponent(NPCRenderer.getInstance()));
        addComponent(new LayerableComponent(new HashSet<>(Collections.singletonList(DirtRoad.class))));
        addComponent(new PedestrianComponent(Behavior.TESTING));
        addComponent(new PathComponent());
    }

    public static void updatePositions() {
        Set<GameObject> gameObjectsPathComponent = Scene.getInstance()
                .getGameObjectsForComponent(PathComponent.class, false);
        gameObjectsPathComponent.stream()
                .filter(Objects::nonNull)
                .filter(gameObject -> gameObject.getClass() == NPC.class)
                .map(gameObject -> gameObject.getComponent(PathComponent.class))
                .filter(Objects::nonNull).forEach(PathComponent::moveForward);
    }
}
