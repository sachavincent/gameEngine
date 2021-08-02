package resources;

import static util.Utils.RES_PATH;

import guis.prefabs.GuiHouseDetails.GuiHouseDetails;
import guis.presets.Background;
import java.io.File;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import language.Words;
import scene.Scene;
import scene.components.requirements.ResourceRequirement;
import scene.components.requirements.ResourceRequirementComponent;
import util.math.Maths;

public class ResourceManager {

    private static final Map<Resource, Stock> RESOURCES = new EnumMap<>(Resource.class);

    static {
        RESOURCES.put(Resource.WHEAT, new Stock(200));
        RESOURCES.put(Resource.FISH, new Stock(200));
        RESOURCES.put(Resource.BREAD, new Stock(200));
//        RESOURCES.put(Resource.FRUIT, new Stock(Integer.MAX_VALUE));
//        RESOURCES.put(Resource.VEGETABLE, new Stock(Integer.MAX_VALUE));
//        RESOURCES.put(Resource.MEAT, new Stock(Integer.MAX_VALUE));
        RESOURCES.put(Resource.GOLD, new Stock(Integer.MAX_VALUE));
    }

    public static Stock getStock(Resource resource) {
        return RESOURCES.get(resource);
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

        Scene.getInstance().getGameObjectsForComponent(ResourceRequirementComponent.class, false)
                .forEach(gameObject -> {
                    ResourceRequirementComponent resourceRequirementComponent = gameObject.getComponent(
                            ResourceRequirementComponent.class);
                    Set<ResourceRequirement> resourceRequirements = resourceRequirementComponent
                            .getRequirementsOfType(ResourceRequirement.class);
                    for (ResourceRequirement resourceRequirement : resourceRequirements)
                        resourceRequirementComponent.clearRequirement(resourceRequirement);

                    for (ResourceRequirement resourceRequirement : resourceRequirements) {
                        if (resourceRequirement.isRequirementMet(RESOURCES))
                            resourceRequirementComponent.meetRequirement(resourceRequirement);
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
//        FRUIT(FruitResource.TEXTURE, FruitResource.NAME, ResourceType.FOOD),
//        VEGETABLE(VegetableResource.TEXTURE, VegetableResource.NAME, ResourceType.FOOD),
//        MEAT(MeatResource.TEXTURE, MeatResource.NAME, ResourceType.FOOD),

        GOLD(GoldResource.TEXTURE, GoldResource.NAME, ResourceType.MONEY);

        Resource(Background<File> texture, Words name, ResourceType resourceType) {
            this.texture = texture;
            this.name = name;
            this.resourceType = resourceType;
        }

        private final Background<File> texture;
        private final Words           name;
        private final ResourceType     resourceType;

        public ResourceType getResourceType() {
            return this.resourceType;
        }

        public Words getName() {
            return this.name;
        }

        public Background<File> getBackgroundTexture() {
            return this.texture;
        }

        public static List<Resource> getResourceOfType(ResourceType resourceType) {
            return Stream.of(Resource.values()).filter(resource -> resource.getResourceType() == resourceType)
                    .collect(Collectors.toList());
        }
    }

    public static class WheatResource {

        final static Words            NAME    = Words.WHEAT;
        final static Background<File> TEXTURE = new Background<>(new File(RES_PATH + "/" + NAME.getString() + ".png"));
    }

    public static class FishResource {

        final static Words            NAME    = Words.FISH;
        final static Background<File> TEXTURE = new Background<>(new File(RES_PATH + "/" + NAME.getString() + ".png"));
    }

    public static class BreadResource {

        final static Words            NAME    = Words.BREAD;
        final static Background<File> TEXTURE = new Background<>(new File(RES_PATH + "/" + NAME.getString() + ".png"));
    }

//    public static class FruitResource {
//
//        final static Words            NAME    = Words.FRUIT;
//        final static Background<File> TEXTURE = new Background<>(new File(RES_PATH + "/" + NAME.getString() + ".png"));
//    }

//    public static class VegetableResource {
//
//        final static Words            NAME    = Words.VEGETABLE;
//        final static Background<File> TEXTURE = new Background<>(new File(RES_PATH + "/" + NAME.getString() + ".png"));
//    }
//
//    public static class MeatResource {
//
//        final static Words            NAME    = Words.MEAT;
//        final static Background<File> TEXTURE = new Background<>(new File(RES_PATH + "/" + NAME.getString() + ".png"));
//    }

    public static class GoldResource {

        final static Words            NAME    = Words.GOLD;
        final static Background<File> TEXTURE = new Background<>(new File(RES_PATH + "/" + NAME.getString() + ".png"));
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

        public boolean isFull() {
            return this.amount == this.maxAmount;
        }
    }
}
