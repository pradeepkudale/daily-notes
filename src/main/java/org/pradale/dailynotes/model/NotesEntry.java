package org.pradale.dailynotes.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface NotesEntry {

    String getId();

    void setId(String id);

    String getName();

    void setName(String name);

    default NotesEntryType getType() {
        return NotesEntryType.MARK_DOWN;
    }

    void setType(NotesEntryType type);

    String getSummary();

    void setSummary(String summary);

    default List<String> getTags() {
        return new ArrayList<>();
    }

    void setTags(List<String> tags);

    File getFile();

    void setFile(File file);

    String getFileName();

    void setFileName(String fileName);

    Date getLastModified();

    void setLastModified(Date lastModified);

    Date getCreatedOn();

    void setCreatedOn(Date createdOn);

    boolean isDeleted();

    void setDeleted(boolean deleted);
}
