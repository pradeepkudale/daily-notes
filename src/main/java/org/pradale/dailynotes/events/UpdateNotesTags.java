package org.pradale.dailynotes.events;

import org.pradale.dailynotes.model.NotesEntry;

public class UpdateNotesTags extends AbstractNotesEntryEvent {

    private String tag;
    private boolean isNewEntry;

    public UpdateNotesTags(NotesEntry entry, boolean isNewEntry, String tag) {
        super(entry);
        this.isNewEntry = isNewEntry;
        this.tag = tag;
    }

    public boolean isNewEntry() {
        return isNewEntry;
    }

    public String getTag() {
        return tag;
    }
}
