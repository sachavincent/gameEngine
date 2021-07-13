package scene.components;

import entities.ModelEntity;
import models.Model;

public class SingleModelComponent extends Component {

    protected ModelEntity modelEntity;

    public SingleModelComponent(ModelEntity modelEntity) {
        this.modelEntity = modelEntity;
    }

    public SingleModelComponent(Model model) {
        this(model.toModelEntity());
    }

    public ModelEntity getModel() {
        return this.modelEntity;
    }

    public void setModel(ModelEntity modelEntity) {
        this.modelEntity = modelEntity;
    }
}
