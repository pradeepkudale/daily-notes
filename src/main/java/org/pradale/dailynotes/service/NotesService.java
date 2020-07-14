package org.pradale.dailynotes.service;

import org.pradale.dailynotes.model.NotesEntry;

import java.util.List;

public interface NotesService {
    void save(NotesEntry entry);
    List<NotesEntry> loadAll();
}
