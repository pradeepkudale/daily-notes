package org.pradale.dailynotes.component;

import com.google.common.eventbus.EventBus;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class NotesTreeItem extends AbstractTreeItem {

    private EventBus eventBus;

    public NotesTreeItem(String name, EventBus eventBus) {
        this.setValue(name);
        this.eventBus = eventBus;
    }

    @Override
    public ContextMenu getMenu() {
        MenuItem newNote = new MenuItem("New Note");
        MenuItem newMarkDown = new MenuItem("New MarkDown");
        MenuItem newTask = new MenuItem("New Task");
        MenuItem newTaskWithDesc = new MenuItem("New Task With Description");

        newNote.setOnAction(event -> {
            //eventBus.post(new CreateBasicEditorEvent());

        });
        newMarkDown.setOnAction(event -> {
            //eventBus.post(new CreateMarkDownEditorEvent());

        });
        newTask.setOnAction(event -> {
            //eventBus.post(new CreateTaskEditorEvent());
        });

        newTaskWithDesc.setOnAction(event -> {
            //eventBus.post(new CreateTaskWithDescriptionEditorEvent());
        });

        return new ContextMenu(newNote, newTask);
    }
}