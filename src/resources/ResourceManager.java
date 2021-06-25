package resources;

import guis.prefabs.GuiHouseDetails.GuiHouseDetails;
import guis.presets.Background;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import scene.Scene;
import scene.components.requirements.RequirementComponent;
import scene.components.requirements.ResourceRequirement;
import util.math.Maths;

public class ResourceManager {

    private static final Map<Resource, Stock> RESOURCES = new EnumMap<>(Resource.class);

    static {
        RESOURCES.put(Resource.WHEAT, new Stock(200));
        RESOURCES.put(Resource.FISH, new Stock(200));
        RESOURCES.put(Resource.BREAD, new Stock(200));
        RESOURCES.put(Resource.FRUIT, new Stock(Integer.MAX_VALUE));
        RESOURCES.put(Resource.VEGETABLE, new Stock(Integer.MAX_VALUE));
        RESOURCES.put(Resource.MEAT, new Stock(Integer.MAX_VALUE));
    }

    public static void addToResource(Resource resource) {
        addToResource(resource, 1);
    }

    /**
     * Adds given resource to the Stock
     *
     * @param resource resource to be added
     * @param quantity quantity of the resource, rounded down to 2 decimals
     */
    public static void addToResource(Resource resource, double quantity) {
        quantity = Maths.roundDown(quantity, 2);
        if (quantity > 0) {
            RESOURCES.get(resource).add(quantity);

//            System.out.println("Added " + quantity + " to " + resource.name);
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
                        if (resourceRequirement.isRequirementMet(RESOURCES))
                            requirementComponent.meetRequirement(resourceRequirement);
                    }
                });
    }

    public static Map<Resource, Stock> getResources() {
        return RESOURCES;
    }

    public enum Resource {
        FISH(FishResource.TEXTURE, FishResource.NAME, ResourceType.FOOD),
        WHEAT(WheatResource.TEXTURE, WheatResource.NAME, ResourceType.INGREDIENT),
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

    public static class WheatResource {

        final static String             NAME    = "Wheat";//TODO: Name through Word class
        final static Background<String> TEXTURE = new Background<>(NAME + ".png");
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
