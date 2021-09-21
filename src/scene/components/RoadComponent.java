package scene.components;

import engineTester.Rome;

public class RoadComponent extends Component {

    public RoadComponent() {
        super((gameObject) -> Rome.getGame().getScene().getRoadGraph()
                .addRoad(gameObject.getPosition().toTerrainPosition()));
    }
}