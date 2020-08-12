package org.pradale.dailynotes.component;

import javafx.scene.control.TreeCell;

public class TreeCellImpl<T> extends TreeCell<String> {

    public TreeCellImpl() {
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            graphicProperty().unbind();
            setGraphic(null);
        } else {
            setText(getItem()); // Really only works if item is a String. Change as needed.
            graphicProperty().bind(getTreeItem().graphicProperty());
        }
    }
}
