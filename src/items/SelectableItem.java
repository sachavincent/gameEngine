package items;

import models.TexturedModel;

public interface SelectableItem {

    void setSelectionBox(TexturedModel selectionBox);

    TexturedModel getSelectionBox();
}
