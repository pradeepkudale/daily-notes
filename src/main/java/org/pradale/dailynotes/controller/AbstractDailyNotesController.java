package org.pradale.dailynotes.controller;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.textfield.CustomTextField;
import org.pradale.dailynotes.events.SaveNotesEntryEvent;
import org.pradale.dailynotes.events.UpdateNotesEntryEvent;
import org.pradale.dailynotes.events.UpdateNotesTags;
import org.pradale.dailynotes.model.NotesEntry;
import org.pradale.dailynotes.model.entry.AbstractNotesEntryDetails;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractDailyNotesController {

    @FXML
    protected TextField textFieldName;

    @FXML
    protected CustomTextField textFieldTags;

    @Autowired
    protected EventBus eventBus;

    public abstract void setNotesEntryDetails(AbstractNotesEntryDetails notesEntry);

    public abstract AbstractNotesEntryDetails getEntryDetails();

    public void initialize() {
        // name
        if (StringUtils.isNotBlank(getEntryDetails().getName())) {
            textFieldName.setText(getEntryDetails().getName());
        }

        textFieldName.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV) {
                getEntryDetails().setName(textFieldName.getText());
                eventBus.post(new SaveNotesEntryEvent(getEntryDetails()));
                eventBus.post(new UpdateNotesEntryEvent(getEntryDetails()));
            }
        });

        // tags
        if (CollectionUtils.isNotEmpty(getEntryDetails().getTags())) {
            HBox hbox = new HBox(2);
            hbox.setCursor(Cursor.DEFAULT);
            textFieldTags.setLeft(hbox);

            updateTags(hbox, getEntryDetails(), getEntryDetails().getTags());
        }

        textFieldTags.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV) {
                if (StringUtils.isNotBlank(textFieldTags.getText())) {
                    String newTag = textFieldTags.getText();
                    List<String> tags = Stream.of(newTag.split(",", -1)).collect(Collectors.toList());

                    HBox hbox = (HBox) textFieldTags.getLeft();

                    if (hbox == null) {
                        hbox = new HBox(2);
                        hbox.setCursor(Cursor.DEFAULT);
                        textFieldTags.setLeft(hbox);
                    }

                    updateTags(hbox, getEntryDetails(), tags);

                    textFieldTags.clear();
                    getEntryDetails().setTags(tags);
                    eventBus.post(new SaveNotesEntryEvent(getEntryDetails()));
                    eventBus.post(new UpdateNotesTags(getEntryDetails(), true,newTag));
                }
            }
        });
    }

    public void updateTags(HBox hbox, NotesEntry entry, List<String> tags) {
        for (String value : tags) {
            Label label = new Label(value);
            Label close = new Label(" X");
            close.setFont(new Font(14));
            close.setTextFill(Color.valueOf("9d0208"));

            Button button = new Button();
            HBox box = new HBox(2);
            box.getChildren().addAll(label, close);
            button.setGraphic(box);

            button.setOnAction(event -> {

                HBox gHbox = (HBox) button.getGraphic();
                Label tagLabelToRemove = (Label) gHbox.getChildren().get(0);

                entry.getTags().remove(tagLabelToRemove.getText());
                hbox.getChildren().remove(button);
                eventBus.post(new SaveNotesEntryEvent(entry));
                eventBus.post(new UpdateNotesTags(getEntryDetails(), false, tagLabelToRemove.getText()));
            });

            hbox.getChildren().add(button);
        }
    }
}
