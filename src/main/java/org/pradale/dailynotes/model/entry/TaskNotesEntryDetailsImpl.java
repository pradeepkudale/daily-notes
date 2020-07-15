package org.pradale.dailynotes.model.entry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.pradale.dailynotes.model.NotesEntryPriority;
import org.pradale.dailynotes.model.NotesEntryType;

import java.util.ArrayList;
import java.util.List;

@Data
public class TaskNotesEntryDetailsImpl extends AbstractNotesEntryDetails {
    @JsonProperty
    private boolean completed;

    @JsonProperty
    private NotesEntryPriority priority = NotesEntryPriority.LOW;

    @JsonProperty
    private String dateStart;

    @JsonProperty
    private String dateEnd;

    @JsonProperty
    private List<SubTaskEntry> subTaskEntries = new ArrayList<>();

    @Override
    public NotesEntryType getType() {
        return NotesEntryType.TASK;
    }
}
