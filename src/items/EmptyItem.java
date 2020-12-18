package items;

public class EmptyItem extends Item {

    public final static String NAME = "EmptyItem";

    EmptyItem(boolean f) {
        super(null, NAME, 0, 0, 0, 0, 0);
    }

    public EmptyItem() {
        super(Items.EMPTY, null);
    }
}
