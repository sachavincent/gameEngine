package scene.components;

import entities.ModelEntity;
import models.AbstractModel;

public class SingleModelComponent extends Component {

    protected AbstractModel model;

    public SingleModelComponent(ModelEntity model) {
        this.model = model.getModel();
    }

    public SingleModelComponent(AbstractModel model) {
        this(model.toModelEntity());
    }

    public AbstractModel getModel() {
        return this.model;
    }

    public void setModel(AbstractModel modelEntity) {
        this.model = modelEntity;
    }
}
