package org.pradale.dailynotes.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.pradale.dailynotes.events.SaveNotesEntryEvent;
import org.pradale.dailynotes.model.NotesEntry;
import org.pradale.dailynotes.model.NotesEntryType;
import org.pradale.dailynotes.model.entry.MarkDownNotesEntryDetailsImpl;
import org.pradale.dailynotes.model.entry.RichTextNotesEntryDetailsImpl;
import org.pradale.dailynotes.model.entry.TaskDescNotesEntryDetailsImpl;
import org.pradale.dailynotes.model.entry.TaskNotesEntryDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class NotesServiceImpl implements NotesService {

    @Value("${dailynotes.root.directory}")
    private String notesDirectory;

    @Value("${dailynotes.autosave.period.inseconds:5}")
    private int autosavePeriod;

    @Autowired
    private EventBus eventBus;

    @PostConstruct
    public void initialize() {
        initializeHomeDirectory();
        eventBus.register(this);
    }

    private void initializeHomeDirectory() {
        File directory = new File(notesDirectory);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    @Override
    public void save(NotesEntry entry) {
        File file = new File(notesDirectory + File.separator + entry.getId());
        entry.setLastModified(new Date());
        try (FileWriter writer = new FileWriter(file, false)) {
            ObjectMapper mapper = new ObjectMapper();
            String content = mapper.writeValueAsString(entry);
            writer.write(content);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public List<NotesEntry> loadAll() {
        List<NotesEntry> entries = new ArrayList<>();
        File dir = new File(notesDirectory + File.separator);
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return Stream.of(NotesEntryType.values()).anyMatch(e -> name.endsWith(e.getExtension()));
            }
        });

        for (File file : files) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                switch (FilenameUtils.getExtension(file.getName())) {
                    case "pdmn":
                        NotesEntry entry = mapper.readValue(file, MarkDownNotesEntryDetailsImpl.class);
                        entries.add(entry);
                        break;
                    case "pdrn":
                        entry = mapper.readValue(file, RichTextNotesEntryDetailsImpl.class);
                        entries.add(entry);
                        break;
                    case "pdtn":
                        entry = mapper.readValue(file, TaskNotesEntryDetailsImpl.class);
                        entries.add(entry);
                        break;
                    case "pdtdn":
                        entry = mapper.readValue(file, TaskDescNotesEntryDetailsImpl.class);
                        entries.add(entry);
                        break;
                }
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
            }
        }

        return entries;
    }

    @Subscribe
    public void save(SaveNotesEntryEvent event) {
        save(event.getEntry());
    }
}
