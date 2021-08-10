package renderEngine.shaders.structs;

public interface StructElement {

    Class<? extends StructLocation> getStructure();

    Object[] getValues();
}
