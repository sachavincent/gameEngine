package scene.components;

import java.util.HashMap;
import java.util.Map;
import resources.ResourceManager;
import resources.ResourceManager.Resource;

public class ProductionComponent extends Component {

    private final Map<Resource, Double> productionRates;

    public ProductionComponent() {
        this.productionRates = new HashMap<>();
        setOnTickElapsedCallback((gameObject, nbTicks) -> {
            this.productionRates.forEach(ResourceManager::addToResource);
            return true;
        });
    }

    public Map<Resource, Double> getProductionRates() {
        return this.productionRates;
    }

    /**
     * Adds resource to the production
     *
     * @param resource resource to be added
     * @param rate amount to be added each tick
     */
    public void addResource(Resource resource, double rate) {
        if (resource == null || rate < 0)
            return;

        this.productionRates.put(resource, rate);
    }

    /**
     * Adds new rate to the current one
     *
     * @param resource resource already in the system
     * @param addRate new amount to be added to the current value
     */
    public void addToProductionRate(Resource resource, double addRate) {
        if (resource == null)
            return;

        if (this.productionRates.containsKey(resource))
            this.productionRates.merge(resource, addRate, Double::sum);
    }
}
