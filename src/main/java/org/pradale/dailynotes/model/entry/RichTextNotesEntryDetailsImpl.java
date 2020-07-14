package org.pradale.dailynotes.model.entry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.pradale.dailynotes.model.NotesEntryType;

@Data
public class RichTextNotesEntryDetailsImpl extends AbstractNotesEntryDetails {

    @JsonProperty
    private String content;

    @Override
    public NotesEntryType getType() {
        return NotesEntryType.RICH_TEXT;
    }
}
