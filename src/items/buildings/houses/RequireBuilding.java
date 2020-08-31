package items.buildings.houses;

import items.buildings.BuildingItem;
import java.util.Map;

public interface RequireBuilding {

    Map<BuildingItem, Integer> getRequirements();

    void meetRequirements();

    boolean doesMeetRequirements();
}
