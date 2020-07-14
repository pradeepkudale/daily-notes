package org.pradale.dailynotes.model.entry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class SubTaskEntry {

    @JsonProperty
    private String id;
    @JsonProperty
    private boolean isCompleted;
    @JsonProperty
    private String description;

}
