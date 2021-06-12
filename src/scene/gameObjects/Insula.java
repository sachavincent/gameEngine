package scene.gameObjects;

import entities.Camera.Direction;
import guis.prefabs.GuiHouseDetails.GuiHouseDetails;
import items.GameObjectPreviews;
import items.OBJGameObjects;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.lwjgl.glfw.GLFW;
import people.Person;
import renderEngine.BuildingRenderer;
import resources.ResourceManager;
import resources.ResourceManager.Resource;
import scene.Scene;
import scene.components.*;
import scene.components.callbacks.AddComponentCallback;
import scene.components.requirements.BuildingRequirement;
import scene.components.requirements.Requirement;
import scene.components.requirements.RequirementComponent;
import scene.components.requirements.ResourceRequirement;
import util.math.Vector3f;

public class Insula extends GameObject {

    private final static int MAX_PEOPLE_CAPACITY = 10;

    public final static int X_POSITIVE_OFFSET = 2;
    public final static int X_NEGATIVE_OFFSET = 3;
    public final static int Z_POSITIVE_OFFSET = 2;
    public final static int Z_NEGATIVE_OFFSET = 3;

    public Insula() {
        addComponent(new IconComponent(GameObjectPreviews.INSULA));
        DirectionComponent directionComponent = new DirectionComponent(Direction.NORTH);
        addComponent(directionComponent);
        addComponent(new TextureComponent(OBJGameObjects.INSULA.getTexture()));
        addComponent(new PreviewComponent(OBJGameObjects.INSULA.getPreviewTexture()));
        addComponent(new OffsetsComponent(Z_NEGATIVE_OFFSET, X_POSITIVE_OFFSET, Z_POSITIVE_OFFSET, X_NEGATIVE_OFFSET));

        addComponent(new RoadConnectionsComponent(new AddComponentCallback() {
            @Override
            public void onAddComponent(GameObject gameObject, Vector3f position) {
                Scene.getInstance().addBuildingRequirement(gameObject);
            }

            @Override
            public boolean isForEach() {
                return false;
            }
        }));

        BuildingRequirement marketRequirement = new BuildingRequirement(Market.class, 0, value -> {
            GuiHouseDetails.getInstance().update();
        });

        RequirementComponent requirementComponent = new RequirementComponent(
                new HashSet<>(Collections.singletonList(marketRequirement)), new HashSet<>(), new HashSet<>());
        addComponent(requirementComponent);

        ResidenceComponent residenceComponent = new ResidenceComponent(MAX_PEOPLE_CAPACITY,
                (person, place) -> {
                    Map<Resource, Integer> resourcesNeeded = Person.getResourcesNeeded(place.getPersons());
                    Set<Requirement<?, ?>> tier2Requirements = resourcesNeeded.entrySet().stream()
                            .map(entry -> new ResourceRequirement(entry.getKey(), entry.getValue() / 2, value -> {
                                GuiHouseDetails.getInstance().update();
                            })).collect(Collectors.toSet());
                    Set<Requirement<?, ?>> tier3Requirements = resourcesNeeded.entrySet().stream()
                            .map(entry -> new ResourceRequirement(entry.getKey(), entry.getValue(), value -> {
                                GuiHouseDetails.getInstance().update();
                            })).collect(Collectors.toSet());
                    requirementComponent.setTier2Requirements(tier2Requirements);
                    requirementComponent.setTier3Requirements(tier3Requirements);
                    ResourceManager.updateRequirements();
                });

        addComponent(residenceComponent);
        addComponent(new BoundingBoxComponent(OBJGameObjects.INSULA.getBoundingBox(), directionComponent));
        addComponent(new SelectableComponent(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                GuiHouseDetails.getInstance().setHouseObject(this);
                GuiHouseDetails.getInstance().setDisplayed(true);
            }
        }));
        addComponent(new RendererComponent(BuildingRenderer.getInstance()));
    }
}