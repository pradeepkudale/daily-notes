package org.pradale.dailynotes.service;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import org.pradale.dailynotes.controller.RichNotesViewController;
import org.pradale.dailynotes.controller.TaskNotesViewController;
import org.pradale.dailynotes.model.NotesEntry;
import org.pradale.dailynotes.model.entry.RichTextNotesEntryDetailsImpl;
import org.pradale.dailynotes.model.entry.TaskNotesEntryDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Objects;

@Slf4j
@Component
public class JavafxStageImpl implements JavafxStage {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private EventBus eventBus;

    @Autowired
    private RichNotesViewController richNotesViewController;

    @Autowired
    private TaskNotesViewController taskNotesViewController;

    public void loadView(Pane parent, NotesEntry entry) {
        Objects.requireNonNull(parent, "Parent pane cannot be null.");
        Objects.requireNonNull(entry, "NotesEntry cannot be null.");

        try {
            URL url = new ClassPathResource(entry.getType().viewFile()).getURL();
            FXMLLoader fxmlLoader = new FXMLLoader(url);

            switch (entry.getType()) {
                case MARK_DOWN:
                    break;
                case RICH_TEXT:
                    fxmlLoader.setController(richNotesViewController);
                    richNotesViewController.setNotesEntryDetails((RichTextNotesEntryDetailsImpl) entry);
                    break;
                case TASK:
                    fxmlLoader.setController(taskNotesViewController);
                    taskNotesViewController.setNotesEntryDetails((TaskNotesEntryDetailsImpl) entry);
                    break;
                default:
                    throw new IllegalArgumentException("Notes Entry not implemented.");
            }

            Pane pane = fxmlLoader.load();

            AnchorPane.setTopAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
            AnchorPane.setBottomAnchor(pane, 0.0);

            parent.getChildren().clear();
            parent.getChildren().add(pane);

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Daily Notes - Error");
            alert.setHeaderText("Loading view");
            alert.setContentText(ex.getMessage());

            alert.showAndWait();
            log.error(ex.getMessage(), ex);
        }
    }
}
