package scene.components;

import terrain.TerrainPosition;

import java.util.HashMap;
import java.util.Map;

public class TerrainComponent extends Component {

    private final Map<TerrainPosition, Integer> focusPoints;

    public TerrainComponent() {
        this.focusPoints = new HashMap<>();

        setOnUpdateComponentCallback(gameObject -> this.focusPoints.clear());
    }

    public void addFocusPoint(TerrainPosition center, int radius) {
        this.focusPoints.put(center, radius);
    }

    public Map<TerrainPosition, Integer> getFocusPoints() {
        return this.focusPoints;
    }

}
