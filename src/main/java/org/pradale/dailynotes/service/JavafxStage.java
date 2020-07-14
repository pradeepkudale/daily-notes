package org.pradale.dailynotes.service;

import javafx.scene.layout.Pane;
import org.pradale.dailynotes.model.NotesEntry;

public interface JavafxStage {
    void loadView(Pane parent, NotesEntry entry);
}
