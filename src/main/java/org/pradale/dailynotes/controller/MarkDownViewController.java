package org.pradale.dailynotes.controller;

import com.google.common.eventbus.EventBus;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.pradale.dailynotes.events.SaveNotesEntryEvent;
import org.pradale.dailynotes.events.UpdateNotesEntryEvent;
import org.pradale.dailynotes.model.entry.AbstractNotesEntryDetails;
import org.pradale.dailynotes.model.entry.MarkDownNotesEntryDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MarkDownViewController extends AbstractDailyNotesController {

    private MarkDownNotesEntryDetailsImpl markDownNotesEntryDetails;

    @Autowired
    private EventBus eventBus;

    @FXML
    private TextArea textViewNotes;

    @Override
    public void setNotesEntryDetails(AbstractNotesEntryDetails notesEntry) {
        if (!(notesEntry instanceof MarkDownNotesEntryDetailsImpl)) {
            throw new IllegalArgumentException("Required RichTextNotesEntryDetailsImpl object");
        }

        this.markDownNotesEntryDetails = (MarkDownNotesEntryDetailsImpl) notesEntry;
    }

    @Override
    public AbstractNotesEntryDetails getEntryDetails() {
        return markDownNotesEntryDetails;
    }

    @FXML
    public void initialize() {
        super.initialize();

        // editor
        if (StringUtils.isNotBlank(markDownNotesEntryDetails.getContent())) {
            textViewNotes.setText(markDownNotesEntryDetails.getContent());
        }

        textViewNotes.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                markDownNotesEntryDetails.setContent(textViewNotes.getText());

                String summary = Jsoup.parse(markDownNotesEntryDetails.getContent()).text();
                markDownNotesEntryDetails.setSummary(summary.substring(0, Math.min(20, summary.length())));

                if (event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.PERIOD) {
                    eventBus.post(new SaveNotesEntryEvent(markDownNotesEntryDetails));
                    eventBus.post(new UpdateNotesEntryEvent(markDownNotesEntryDetails));
                }
            }
        });
    }

}
