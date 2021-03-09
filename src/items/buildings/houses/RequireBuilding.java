package items.buildings.houses;

import items.abstractItem.AbstractItem;
import java.util.Map;
import pathfinding.RouteFinder.Route;

public interface RequireBuilding {

    Map<AbstractItem, Integer> getRequirements();

    void meetRequirement(AbstractItem abstractItem, Route routeToItem);

    boolean doesMeetRequirement(AbstractItem abstractItem);

    Route getRouteToItem(AbstractItem abstractItem);

    boolean doesMeetAllRequirements();
}
