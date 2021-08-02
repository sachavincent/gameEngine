package scene.components;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import renderEngine.DisplayManager;
import resources.ResourceManager;
import resources.ResourceManager.Resource;

public class ResourceTransformationComponent extends Component {

    private final Set<ResourceTransformation> resourceTransformations;

    public ResourceTransformationComponent() {
        this.resourceTransformations = new HashSet<>();
        setOnTickElapsedCallback((gameObject, nbTicks) -> {
            if (nbTicks < DisplayManager.TPS)
                return false;
            this.resourceTransformations.forEach(resourceTransformation -> {
                if (shouldTransformationOccur(resourceTransformation)) {
                    ResourceManager.removeFromResource(resourceTransformation.getSource(),
                            resourceTransformation.getConversionRate());
                    ResourceManager.addToResource(resourceTransformation.getResult());
                }
            });
            return true;
        });
    }

    private static boolean shouldTransformationOccur(ResourceTransformation resourceTransformation) {
        return isEnoughResourceAvailable(resourceTransformation) &&
                !ResourceManager.getStock(resourceTransformation.getResult()).isFull();
    }

    private static boolean isEnoughResourceAvailable(ResourceTransformation resourceTransformation) {
        Resource source = resourceTransformation.getSource();
        int conversionRate = resourceTransformation.getConversionRate();
        return ResourceManager.getStock(source).getAmount() >= conversionRate;
    }

    public void addResourceTransformation(ResourceTransformation resourceTransformation) {
        this.resourceTransformations.add(resourceTransformation);
    }

    public Set<ResourceTransformation> getResourceTransformations() {
        return Collections.unmodifiableSet(this.resourceTransformations);
    }

    public static class ResourceTransformation {

        private final Resource source;
        private final Resource result;

        /**
         * Number of sources per result
         */
        private final int conversionRate;

        public ResourceTransformation(Resource source, Resource result, int conversionRate) {
            this.source = source;
            this.result = result;
            this.conversionRate = conversionRate;

            if (this.conversionRate < 0)
                throw new IllegalArgumentException("Conversion rate must above 0");
        }

        public ResourceTransformation(Resource source, Resource result) {
            this(source, result, 1);
        }

        public Resource getSource() {
            return this.source;
        }

        public Resource getResult() {
            return this.result;
        }

        public int getConversionRate() {
            return this.conversionRate;
        }
    }
}
