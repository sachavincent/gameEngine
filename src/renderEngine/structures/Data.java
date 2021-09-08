package renderEngine.structures;

public class Data extends AbstractData<BasicVao> {

    private Data() {
        super(BasicVao.class);
    }

    public static Data createData(AttributeData<?>... attributesData) {
        Data data = new Data();
        for (AttributeData<?> attributeData : attributesData)
            data.addAttributeData(attributeData);

        return data;
    }
}
