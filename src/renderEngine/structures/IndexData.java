package renderEngine.structures;

import java.util.Collection;

public class IndexData extends AbstractData<IndexBufferVao> {

    private IndexData() {
        super(IndexBufferVao.class);
    }

    public static IndexData createData(AttributeData<?>... attributesData) {
        IndexData data = new IndexData();
        for (AttributeData<?> attributeData : attributesData)
            data.addAttributeData(attributeData);

        return data;
    }

    public static IndexData createData(Collection<AttributeData<?>> attributesData) {
        IndexData data = new IndexData();
        for (AttributeData<?> attributeData : attributesData)
            data.addAttributeData(attributeData);

        return data;
    }
}
