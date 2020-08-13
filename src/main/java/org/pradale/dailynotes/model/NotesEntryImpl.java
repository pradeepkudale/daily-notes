package org.pradale.dailynotes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.File;
import java.util.Date;
import java.util.Set;

@Data
public abstract class NotesEntryImpl implements NotesEntry {

    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private NotesEntryType type;

    @JsonProperty
    private Set<String> tags;

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


    public Date getLastModified() {
        if(lastModified == null) {
            lastModified = new Date();
        }
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Date getCreatedOn() {
        if(createdOn == null) {
            createdOn = new Date();
        }
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}
