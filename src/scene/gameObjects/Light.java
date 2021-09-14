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
import scene.components.LightComponent;
import scene.components.PositionComponent;
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
        addComponent(new PositionComponent(position));
        addComponent(new LightComponent(color, attenuation));

        addComponent(new RendererComponent(SunRenderer.getInstance()));
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

        public Sun(Vector3f color, Vector3f attenuation, float distanceFromCenter) {
            super(new Vector3f(), color, attenuation);

            addComponent(new ScaleComponent(30));

            Vector3f center = new Vector3f(Game.TERRAIN_WIDTH / 2f, 0, Game.TERRAIN_DEPTH / 2f);
            addComponent(new SunComponent(center, distanceFromCenter));
            Vector3f defaultPosition = Vector3f.add(center, Direction.
                    toRelativeDistance(Direction.EAST, distanceFromCenter), null);
            getComponent(PositionComponent.class).setPosition(defaultPosition);

            addComponent(new SingleModelComponent(MODEL.getTexture()));
        }
    }
}