package scene.components;

public class PedestrianComponent extends Component {

    private Behavior behavior;

    public PedestrianComponent(Behavior behavior) {
        this.behavior = behavior;
    }

    public void setBehavior(Behavior behavior) {
        this.behavior = behavior;
    }

    public Behavior getBehavior() {
        return this.behavior;
    }

    public enum Behavior {
        WALKING(0.08f),
        RUNNING(0.5f),
        TESTING(4f);

        protected float speed; // Distance/second

        Behavior(float speed) {
            this.speed = speed;
        }

        public float getSpeed() {
            return this.speed;
        }
    }
}
