package org.pradale.dailynotes.model.entry;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.pradale.dailynotes.model.NotesEntryType;

public class BasicNotesEntryDetailsImpl extends AbstractNotesEntryDetails {
    @JsonProperty
    private String content;

    @Override
    public NotesEntryType getType() {
        return NotesEntryType.MARK_DOWN;
    }
}
