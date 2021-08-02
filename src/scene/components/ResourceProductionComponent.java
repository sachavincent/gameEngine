package scene.components;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import resources.ResourceManager;
import resources.ResourceManager.Resource;
import util.TimeSystem;

public class ResourceProductionComponent extends Component {

    private final Map<Resource, Double> productionRates;

    public ResourceProductionComponent() {
        this.productionRates = new HashMap<>();
        setOnTickElapsedCallback((gameObject, nbTicks) -> {
            if (nbTicks < TimeSystem.TICK_RATE)
                return false;

            this.productionRates.forEach(ResourceManager::addToResource);
            return true;
        });
    }

    public Map<Resource, Double> getProductionRates() {
        return Collections.unmodifiableMap(this.productionRates);
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
     * Adds rate to the current one
     *
     * @param resource resource already in the system
     * @param addRate amount to be added to the current value
     */
    public void addToProductionRate(Resource resource, double addRate) {
        if (resource == null)
            return;

        if (this.productionRates.containsKey(resource))
            this.productionRates.merge(resource, addRate, Double::sum);
    }

    /**
     * Removes rate from the current one
     *
     * @param resource resource already in the system
     * @param removeRate amount to be removed from the current value
     */
    public void removeFromProductionRate(Resource resource, double removeRate) {
        if (resource == null)
            return;

        if (this.productionRates.containsKey(resource))
            this.productionRates.merge(resource, -removeRate, Double::sum);
    }

    /**
     * Replaces current rate with the new one
     *
     * @param resource resource already in the system
     * @param newRate new rate
     */
    public void setProductionRate(Resource resource, double newRate) {
        if (resource == null)
            return;

        if (this.productionRates.containsKey(resource))
            this.productionRates.put(resource, newRate);
    }
}