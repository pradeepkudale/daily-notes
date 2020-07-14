package org.pradale.dailynotes.events;

import lombok.Data;
import org.pradale.dailynotes.model.NotesEntry;

@Data
public abstract class AbstractNotesEntryEvent {

    private NotesEntry entry;

    public AbstractNotesEntryEvent(NotesEntry entry) {
        this.entry = entry;
    }
}
