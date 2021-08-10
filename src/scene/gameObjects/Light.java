package scene.gameObjects;

import engineTester.Game;
import renderEngine.BuildingRenderer;
import scene.components.*;
import util.math.Vector3f;
import util.parsing.ModelType;
import util.parsing.objParser.ModelLoader;

import static entities.Camera.Direction;

public class Light extends GameObject {

    public Light(Vector3f position, Vector3f color) {
        this(position, color, new Vector3f(1, 0, 0));
    }

    public Light(Vector3f position, Vector3f color, Vector3f attenuation) {
        addComponent(new PositionComponent(position));
        addComponent(new LightComponent(color, attenuation));

        addComponent(new RendererComponent(BuildingRenderer.getInstance()));
    }

    public static class Sun extends Light {

        private final static GameObjectData MODEL = ModelLoader.loadModel("Sun", ModelType.DEFAULT);

        public Sun(Vector3f color, float distanceFromCenter) {
            this(color, new Vector3f(1, 0, 0), distanceFromCenter);
        }

        public Sun(Vector3f color, Vector3f attenuation, float distanceFromCenter) {
            super(new Vector3f(), color, attenuation);

            Vector3f center = new Vector3f(Game.TERRAIN_WIDTH / 2f, 0, Game.TERRAIN_DEPTH / 2f);
            addComponent(new SunComponent(center, distanceFromCenter));
            Vector3f defaultPosition = center.add(Direction.
                    toRelativeDistance(Direction.EAST, distanceFromCenter));
            addComponent(new PositionComponent(defaultPosition));

            addComponent(new SingleModelComponent(MODEL.getTexture()));
        }
    }
}