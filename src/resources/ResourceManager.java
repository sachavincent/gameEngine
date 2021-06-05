package resources;

import guis.prefabs.GuiHouseDetails.GuiHouseDetails;
import guis.presets.Background;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import scene.Scene;
import scene.components.requirements.RequirementComponent;
import scene.components.requirements.ResourceRequirement;

public class ResourceManager {

    private static final Map<Resource, Stock> RESOURCES = new EnumMap<>(Resource.class);

    static {
        RESOURCES.put(Resource.FISH, new Stock(Integer.MAX_VALUE));
        RESOURCES.put(Resource.BREAD, new Stock(Integer.MAX_VALUE));
        RESOURCES.put(Resource.FRUIT, new Stock(Integer.MAX_VALUE));
        RESOURCES.put(Resource.VEGETABLE, new Stock(Integer.MAX_VALUE));
        RESOURCES.put(Resource.MEAT, new Stock(Integer.MAX_VALUE));
    }

    public static void addToResource(Resource resource) {
        addToResource(resource, 1);
    }

    public static void addToResource(Resource resource, double amount) {
        if (amount > 0) {
            RESOURCES.get(resource).add(amount);

            updateRequirements();
        }
    }

    public static void removeFromResource(Resource resource) {
        removeFromResource(resource, 1);
    }

    public static void removeFromResource(Resource resource, double amount) {
        if (amount > 0) {
            RESOURCES.get(resource).remove(amount);

            updateRequirements();
        }
    }

    public static void updateRequirements() {
        GuiHouseDetails.getInstance().update();

        Scene.getInstance().getGameObjectsForComponent(RequirementComponent.class, false)
                .forEach(gameObject -> {
                    RequirementComponent requirementComponent = gameObject.getComponent(RequirementComponent.class);
                    Set<ResourceRequirement> resourceRequirements = requirementComponent
                            .getRequirementsOfType(ResourceRequirement.class);
                    for (ResourceRequirement resourceRequirement : resourceRequirements)
                        requirementComponent.clearRequirement(resourceRequirement);

                    for (ResourceRequirement resourceRequirement : resourceRequirements) {
                        if (RESOURCES.get(resourceRequirement.getKey()).amount > resourceRequirement.getValue())
                            requirementComponent.meetRequirement(resourceRequirement);
                    }
                });
    }

    public static Map<Resource, Stock> getResources() {
        return RESOURCES;
    }

    public enum Resource {
        FISH(FishResource.TEXTURE, FishResource.NAME, ResourceType.FOOD),
        BREAD(BreadResource.TEXTURE, BreadResource.NAME, ResourceType.FOOD),
        FRUIT(FruitResource.TEXTURE, FruitResource.NAME, ResourceType.FOOD),
        VEGETABLE(VegetableResource.TEXTURE, VegetableResource.NAME, ResourceType.FOOD),
        MEAT(MeatResource.TEXTURE, MeatResource.NAME, ResourceType.FOOD);

        Resource(Background<String> texture, String name, ResourceType resourceType) {
            this.texture = texture;
            this.name = name;
            this.resourceType = resourceType;
        }

        private final Background<String> texture;
        private final String             name;
        private final ResourceType       resourceType;

        public ResourceType getResourceType() {
            return this.resourceType;
        }

        public String getName() {
            return this.name;
        }

        public Background<String> getBackgroundTexture() {
            return this.texture;
        }

    }

    public static class FishResource {

        final static String             NAME    = "Fish";
        final static Background<String> TEXTURE = new Background<>(NAME + ".png");
    }

    public static class BreadResource {

        final static String             NAME    = "Bread";
        final static Background<String> TEXTURE = new Background<>(NAME + ".png");
    }

    public static class FruitResource {

        final static String             NAME    = "Fruit";
        final static Background<String> TEXTURE = new Background<>(NAME + ".png");
    }

    public static class VegetableResource {

        final static String             NAME    = "Vegetable";
        final static Background<String> TEXTURE = new Background<>(NAME + ".png");
    }

    public static class MeatResource {

        final static String             NAME    = "Meat";
        final static Background<String> TEXTURE = new Background<>(NAME + ".png");
    }

    public static class Stock {

        protected int    maxAmount;
        protected double amount;

        public Stock(int maxAmount) {
            this.maxAmount = maxAmount;
        }

        public void setMaxAmount(int maxAmount) {
            this.maxAmount = maxAmount;
        }

        public int getMaxAmount() {
            return this.maxAmount;
        }

        public double getAmount() {
            return this.amount;
        }

        private void add(double amount) {
            this.amount = Math.min(this.maxAmount, this.amount + amount);
        }

        private void remove(double amount) {
            this.amount = Math.max(0, this.amount - amount);
        }
    }
}
