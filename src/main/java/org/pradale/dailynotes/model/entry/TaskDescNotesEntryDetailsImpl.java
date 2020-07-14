package org.pradale.dailynotes.model.entry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.pradale.dailynotes.model.NotesEntryType;

@Data
public class TaskDescNotesEntryDetailsImpl extends AbstractNotesEntryDetails {
    @JsonProperty
    private boolean completed;

    @Override
    public NotesEntryType getType() {
        return NotesEntryType.TASK_WITH_DESC;
    }
}
