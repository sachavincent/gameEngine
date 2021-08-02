package scene.components;

import scene.Scene;

public class RoadComponent extends Component {

    public RoadComponent() {
        super((gameObject, position) -> Scene.getInstance().getRoadGraph().addRoad(position.toTerrainPosition()));
    }
}
