package resources;

import guis.presets.Background;
import java.util.HashMap;
import java.util.Map;

public class ResourceManager {

    private static final Map<Resource, Stock> RESOURCES = new HashMap<>();

    static {
        RESOURCES.put(Resource.FISH, new Stock());
        RESOURCES.put(Resource.BREAD, new Stock());
        RESOURCES.put(Resource.FRUIT, new Stock());
        RESOURCES.put(Resource.VEGETABLE, new Stock());
        RESOURCES.put(Resource.MEAT, new Stock());
    }

    public static void addToResource(Resource resource) {
        RESOURCES.get(resource).add();
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

        public Background<String> getTexture() {
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

        private int maxAmount;
        private int amount;

        public void setMaxAmount(int maxAmount) {
            this.maxAmount = maxAmount;
        }

        public int getMaxAmount() {
            return this.maxAmount;
        }

        public int getAmount() {
            return this.amount;
        }

        public void add() {
            if (amount < maxAmount)
                amount++;
        }

        public void remove() {
            if (amount > 0)
                amount--;
        }
    }
}
