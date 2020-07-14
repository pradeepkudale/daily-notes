package org.pradale.dailynotes.events;

import org.pradale.dailynotes.model.NotesEntry;

public class UpdateNotesEntryEvent extends AbstractNotesEntryEvent {

    public UpdateNotesEntryEvent(NotesEntry entry) {
        super(entry);
    }
}
