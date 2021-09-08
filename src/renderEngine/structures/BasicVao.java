package renderEngine.structures;

public class BasicVao extends Vao {

    public BasicVao() {
        super();
    }

    public int getDataLength() {
        if (this.attributes.size() == 0)
            return 0;

        AttributeData<?> firstAttribute = this.attributes.get(0);

        return firstAttribute.getData().length / firstAttribute.getAttributeSize();
    }
}