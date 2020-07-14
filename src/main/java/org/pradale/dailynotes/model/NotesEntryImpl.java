package org.pradale.dailynotes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.File;
import java.util.Date;
import java.util.List;

@Data
public abstract class NotesEntryImpl implements NotesEntry {

    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private NotesEntryType type;

    @JsonProperty
    private List<String> tags;

    @JsonIgnore
    private File file;

    @JsonIgnore
    private String fileName;

    @JsonProperty
    private Date lastModified;

    @JsonProperty
    private Date createdOn;

    @JsonProperty
    private String summary;

    @JsonProperty
    private boolean deleted;

}
