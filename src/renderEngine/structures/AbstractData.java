package renderEngine.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractData<VaoType extends Vao> {

    private final Class<VaoType> vaoType;

    private final List<AttributeData<?>> attributesData;

    protected AbstractData(Class<VaoType> vaoType) {
        this.vaoType = vaoType;

        this.attributesData = new ArrayList<>();
    }

    public final void addAttributeData(AttributeData<?> attributeData) {
        this.attributesData.add(attributeData);
    }

    /**
     * @return unmodifiable list containing the attributes
     */
    public final List<AttributeData<?>> getAttributesData() {
        return Collections.unmodifiableList(this.attributesData);
    }

    public boolean isEmpty() {
        for (AttributeData<?> attributeData : this.attributesData) {
            Number[] data = attributeData.getData();
            if (data != null && data.length > 0)
                return false;
        }

        return true;
    }

    public Class<VaoType> getVaoType() {
        return this.vaoType;
    }
}