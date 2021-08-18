package scene.gameObjects;

import engineTester.Game;
import models.AbstractModel;
import models.SimpleModel;
import renderEngine.SunRenderer;
import renderEngine.Vao;
import scene.components.*;
import textures.ModelTexture;
import util.ResourceFile;
import util.math.Vector3f;

import static entities.Camera.Direction;

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

        private static final float[] POSITIONS = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
        private final static GameObjectData MODEL;

        static {
            Vao vao = Vao.create();
            vao.bind();
            vao.createAttribute(0, POSITIONS, 2);
            vao.unbind();

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
            Vector3f defaultPosition = center.add(Direction.
                    toRelativeDistance(Direction.EAST, distanceFromCenter));
            addComponent(new PositionComponent(defaultPosition));

            addComponent(new SingleModelComponent(MODEL.getTexture()));
        }
    }
}