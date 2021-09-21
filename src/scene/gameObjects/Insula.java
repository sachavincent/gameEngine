package scene.gameObjects;

import engineTester.Rome;
import entities.Camera;
import guis.prefabs.GuiHouseDetails.GuiHouseDetails;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.lwjgl.glfw.GLFW;
import pathfinding.Road;
import people.Person;
import renderEngine.BuildingRenderer;
import resources.ResourceManager;
import scene.components.*;
import scene.components.callbacks.ObjectPlacedCallback;
import scene.components.requirements.BuildingRoadConnectionRequirement;
import scene.components.requirements.Requirement;
import scene.components.requirements.ResourceRequirement;
import scene.components.requirements.ResourceRequirementComponent;

public class Insula extends GameObject {

    private static final int MAX_PEOPLE_CAPACITY = 10;

    public static final int X_POSITIVE_OFFSET = 3;
    public static final int X_NEGATIVE_OFFSET = 3;
    public static final int Z_POSITIVE_OFFSET = 3;
    public static final int Z_NEGATIVE_OFFSET = 3;

    public Insula() {
        addComponent(new IconComponent(GameObjectPreviews.INSULA));
        DirectionComponent directionComponent = new DirectionComponent(Camera.Direction.NORTH);
        addComponent(directionComponent);
        addComponent(new SingleModelComponent(GameObjectDatas.INSULA.getTexture()));
//        addComponent(new AnimatedModelComponent(new Model(OBJGameObjects.INSULA.getTexture())));
        addComponent(new PreviewComponent(GameObjectDatas.INSULA.getPreviewTexture()));
        addComponent(new OffsetsComponent(Z_NEGATIVE_OFFSET, X_POSITIVE_OFFSET, Z_POSITIVE_OFFSET, X_NEGATIVE_OFFSET));

        addComponent(new ConnectionsComponent<>(Road.class, new ObjectPlacedCallback() {
            @Override
            public void onObjPlaced(GameObject gameObject) {
                Rome.getGame().getScene().addBuildingRequirement(gameObject);
            }

            @Override
            public boolean isForEach() {
                return false;
            }
        }));

        BuildingRoadConnectionRequirement marketRequirement = new BuildingRoadConnectionRequirement(Market.class, 0,
                value -> {
                    GuiHouseDetails.getInstance().update();
                });

        ResourceRequirementComponent resourceRequirementComponent = new ResourceRequirementComponent(new HashSet<>(),
                new HashSet<>(Collections.singletonList(marketRequirement)), new HashSet<>(), new HashSet<>());
        addComponent(resourceRequirementComponent);

        ResourceProductionComponent resourceProductionComponent = new ResourceProductionComponent();
        resourceProductionComponent.addResource(ResourceManager.Resource.GOLD, 0);
        addComponent(resourceProductionComponent);

        ResidenceComponent residenceComponent = new ResidenceComponent(MAX_PEOPLE_CAPACITY);
        residenceComponent.setOnUpdateComponentCallback(gameObject -> {
            Map<ResourceManager.Resource, Integer> resourcesNeeded = Person.getResourcesNeeded(
                    residenceComponent.getPersons());
            Set<Requirement<?, ?>> tier2Requirements = resourcesNeeded.entrySet().stream()
                    .map(entry -> new ResourceRequirement(entry.getKey(), entry.getValue() / 2, value -> {
                        GuiHouseDetails.getInstance().update();
                    })).collect(Collectors.toSet());
            Set<Requirement<?, ?>> tier3Requirements = resourcesNeeded.entrySet().stream()
                    .map(entry -> new ResourceRequirement(entry.getKey(), entry.getValue(), value -> {
                        GuiHouseDetails.getInstance().update();
                    })).collect(Collectors.toSet());
            resourceRequirementComponent.setTier2Requirements(tier2Requirements);
            resourceRequirementComponent.setTier3Requirements(tier3Requirements);

            double goldProductionRate = Person.getResourcesProduced(residenceComponent.getPersons())
                    .get(ResourceManager.Resource.GOLD);
            resourceProductionComponent.setProductionRate(ResourceManager.Resource.GOLD, goldProductionRate);

            ResourceManager.updateRequirements();
        });

        addComponent(residenceComponent);

        addComponent(new BoundingBoxComponent(GameObjectDatas.INSULA.getBoundingBox(), directionComponent));
        addComponent(new SelectableComponent(button -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                GuiHouseDetails.getInstance().setHouseObject(this);
                GuiHouseDetails.getInstance().setDisplayed(true);
                return true;
            }
            return false;
        }));
        addComponent(new RendererComponent(BuildingRenderer.getInstance()));
//        addComponent(new RendererComponent(AnimatedBuildingRenderer.getInstance()));
    }
}