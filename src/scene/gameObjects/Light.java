package scene.gameObjects;

import static entities.Camera.Direction;

import engineTester.Game;
import models.AbstractModel;
import models.SimpleModel;
import renderEngine.SunRenderer;
import renderEngine.structures.AttributeData;
import renderEngine.structures.AttributeData.DataType;
import renderEngine.structures.BasicVao;
import renderEngine.structures.Data;
import renderEngine.structures.Vao;
import scene.components.Component;
import scene.components.LightComponent;
import scene.components.RendererComponent;
import scene.components.ScaleComponent;
import scene.components.SingleModelComponent;
import scene.components.SunComponent;
import textures.ModelTexture;
import util.ResourceFile;
import util.math.Vector3f;

public class Light extends GameObject {

    public Light(Vector3f position, Vector3f color) {
        this(position, color, new Vector3f(1, 0, 0));
    }

    public Light(Vector3f position, Vector3f color, Vector3f attenuation) {
        this(position, new LightComponent(color, attenuation), new RendererComponent(SunRenderer.getInstance()));
    }

    public Light() {
    }

    protected Light(Vector3f position, Component... components) {
        for (Component component : components)
            addComponent(component);

        placeAt(position);
    }

    public static class Sun extends Light {

        private static final Float[]        POSITIONS = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
        private static final GameObjectData MODEL;

        static {
            AttributeData<Float> positionsAttribute = new AttributeData<>(0, 2, POSITIONS, DataType.FLOAT);
            Data data = Data.createData(positionsAttribute);
            BasicVao vao = Vao.createVao(data, data.getVaoType());

            AbstractModel model = new SimpleModel(vao, ModelTexture.createTexture(new ResourceFile("sun.png")));
            MODEL = new GameObjectData();
            MODEL.setTexture(model);
        }

        public Sun(Vector3f color, float distanceFromCenter) {
            this(color, new Vector3f(1, 0, 0), distanceFromCenter);
        }

        public Sun() {
            super();
        }

        public Sun(Vector3f color, Vector3f attenuation, float distanceFromCenter) {
            this(color, attenuation, distanceFromCenter,
                    new Vector3f(Game.TERRAIN_WIDTH / 2f, 0, Game.TERRAIN_DEPTH / 2f));
        }

        private Sun(Vector3f color, Vector3f attenuation, float distanceFromCenter, Vector3f center) {
            super(Vector3f.add(center, Direction.toRelativeDistance(Direction.EAST, distanceFromCenter), null),
                    new LightComponent(color, attenuation), new RendererComponent(SunRenderer.getInstance()),
                    new ScaleComponent(30), new SunComponent(center, distanceFromCenter),
                    new SingleModelComponent(MODEL.getTexture()));
        }
    }
}