package org.pradale.dailynotes.component;

import javafx.scene.control.TreeCell;

public class TreeCellImpl extends TreeCell<String> {

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if(getItem() == null) {
            return;
        }

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(getItem());
            setGraphic(getTreeItem().getGraphic());
            setContextMenu(((AbstractTreeItem) getTreeItem()).getMenu());
        }
    }
}
