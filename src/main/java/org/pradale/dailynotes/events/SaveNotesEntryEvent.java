package org.pradale.dailynotes.events;

import org.pradale.dailynotes.model.NotesEntry;

public class SaveNotesEntryEvent extends AbstractNotesEntryEvent {

    public SaveNotesEntryEvent(NotesEntry entry) {
        super(entry);
    }
}
