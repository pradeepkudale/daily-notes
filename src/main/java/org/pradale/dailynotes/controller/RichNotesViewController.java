package org.pradale.dailynotes.controller;

import com.google.common.eventbus.EventBus;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.HTMLEditor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.pradale.dailynotes.events.SaveNotesEntryEvent;
import org.pradale.dailynotes.events.UpdateNotesEntryEvent;
import org.pradale.dailynotes.model.entry.AbstractNotesEntryDetails;
import org.pradale.dailynotes.model.entry.RichTextNotesEntryDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RichNotesViewController extends AbstractDailyNotesController {

    private RichTextNotesEntryDetailsImpl richTextNotesEntryDetails;

    @Autowired
    private EventBus eventBus;

    @FXML
    private HTMLEditor editorNotes;

    @Override
    public void setNotesEntryDetails(AbstractNotesEntryDetails notesEntry) {
        if (!(notesEntry instanceof RichTextNotesEntryDetailsImpl)) {
            throw new IllegalArgumentException("Required RichTextNotesEntryDetailsImpl object");
        }

        this.richTextNotesEntryDetails = (RichTextNotesEntryDetailsImpl) notesEntry;
    }

    @Override
    public AbstractNotesEntryDetails getEntryDetails() {
        return richTextNotesEntryDetails;
    }

    @FXML
    public void initialize() {
        super.initialize();

        // editor
        if (StringUtils.isNotBlank(richTextNotesEntryDetails.getContent())) {
            editorNotes.setHtmlText(richTextNotesEntryDetails.getContent());
        }

        editorNotes.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                richTextNotesEntryDetails.setContent(editorNotes.getHtmlText());

                String summary = Jsoup.parse(richTextNotesEntryDetails.getContent()).text();
                richTextNotesEntryDetails.setSummary(summary.substring(0, Math.min(20, summary.length())));

                if (event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.PERIOD) {
                    eventBus.post(new SaveNotesEntryEvent(richTextNotesEntryDetails));
                    eventBus.post(new UpdateNotesEntryEvent(richTextNotesEntryDetails));
                }
            }
        });
        }
}
