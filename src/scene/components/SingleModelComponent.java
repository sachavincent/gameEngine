package scene.components;

import entities.ModelEntity;
import models.AbstractModel;

public class SingleModelComponent extends Component {

    protected ModelEntity modelEntity;

    public SingleModelComponent(ModelEntity modelEntity) {
        this.modelEntity = modelEntity;
    }

    public SingleModelComponent(AbstractModel model) {
        this(model.toModelEntity());
    }

    public ModelEntity getModel() {
        return this.modelEntity;
    }

    public void setModel(ModelEntity modelEntity) {
        this.modelEntity = modelEntity;
    }
}
