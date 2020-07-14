package org.pradale.dailynotes.controller;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.textfield.CustomTextField;
import org.pradale.dailynotes.events.UpdateNotesEntryEvent;
import org.pradale.dailynotes.model.NotesEntry;
import org.pradale.dailynotes.model.NotesEntryType;
import org.pradale.dailynotes.model.entry.RichTextNotesEntryDetailsImpl;
import org.pradale.dailynotes.model.entry.TaskNotesEntryDetailsImpl;
import org.pradale.dailynotes.service.JavafxStage;
import org.pradale.dailynotes.service.NotesService;
import org.pradale.dailynotes.util.ComponentUtils;
import org.pradale.dailynotes.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class DailyNotesMainController {

    @Autowired
    private EventBus eventBus;

    @Autowired
    private JavafxStage javafxStage;

    @Autowired
    private NotesService notesService;

    @FXML
    private ListView<NotesEntry> listViewMaster;
    private ObservableList<NotesEntry> listViewMasterData = FXCollections.observableArrayList();

    @FXML
    private AnchorPane dailyNotesPane;

    @FXML
    private CustomTextField textFieldTaskSearch;

    @FXML
    public void initialize() {
        eventBus.register(this);
        initializeComponents();
    }

    private void initializeComponents() {
        List<NotesEntry> entries = notesService.loadAll();

        listViewMasterData.addAll(entries);
        sortNotesEntry();
        FilteredList<NotesEntry> filteredData = new FilteredList(listViewMasterData);
        listViewMaster.setItems(filteredData);
        listViewMaster.setCellFactory(listView -> new ListCell<NotesEntry>() {
            @Override
            protected void updateItem(NotesEntry entry, boolean empty) {
                super.updateItem(entry, empty);
                String HIGHLIGHTED_CONTROL_INNER_BACKGROUND = "derive(lightgreen, 80%)";
                String HIGHLIGHTED_CONTROL_INNER_DEFAULT_BACKGROUND = "derive(white, 100%)";

                if (empty) {
                    setGraphic(null);
                } else {
                    VBox vBox = new VBox(2);
                    Label labelName = new Label(entry.getName());
                    labelName.setFont(new Font(14));
                    Label labelDateTime = new Label(ObjectUtils.firstNonNull(entry.getSummary(), "") + "...");
                    vBox.getChildren().addAll(
                            labelName, labelDateTime
                    );
                    setGraphic(vBox);

                    vBox.setOnMouseClicked(event -> {
                        javafxStage.loadView(dailyNotesPane, entry);
                    });
//
//                    if (((NotesEntryDetailsImpl) entry).isCompleted()) {
//                        setStyle("-fx-control-inner-background: " + HIGHLIGHTED_CONTROL_INNER_BACKGROUND + ";");
//                    } else {
//                        setStyle("-fx-control-inner-background: " + HIGHLIGHTED_CONTROL_INNER_DEFAULT_BACKGROUND + ";");
//                    }
                }
            }
        });

        ComponentUtils.setupClearButtonField(textFieldTaskSearch, textFieldTaskSearch.rightProperty());
        textFieldTaskSearch.textProperty().addListener(((observable, oldValue, search) -> {
            filteredData.setPredicate(data -> {
                if (search == null || search.isEmpty()) {
                    return true;
                }

                if (StringUtils.containsIgnoreCase(data.getName(), search)) {
                    return true;
                }

                if (data.getTags().contains(search)) {
                    return true;
                }

                return false;
            });
        }));
    }

    @Subscribe
    public void updateNotesEntry(UpdateNotesEntryEvent event) {
        sortNotesEntry();
        listViewMaster.refresh();
    }

    public void sortNotesEntry() {
        Collections.sort(listViewMasterData, new Comparator<NotesEntry>() {
            @Override
            public int compare(NotesEntry obj1, NotesEntry obj2) {

                if (obj1.getType() == NotesEntryType.TASK && obj2.getType() == NotesEntryType.TASK) {
                    TaskNotesEntryDetailsImpl entry1 = (TaskNotesEntryDetailsImpl) obj1;
                    TaskNotesEntryDetailsImpl entry2 = (TaskNotesEntryDetailsImpl) obj2;

                    return Boolean.compare(entry1.isCompleted(), entry2.isCompleted());
                } else {
                    return 1;
                }
            }
        });
    }

    public void addNewBasicNote(ActionEvent actionEvent) {
    }

    public void addNewRichNote(ActionEvent actionEvent) {
        NotesEntry entry = new RichTextNotesEntryDetailsImpl();
        entry.setId(FileUtils.getNewFileName(entry.getType()));
        entry.setName("New Note");
        javafxStage.loadView(dailyNotesPane, entry);
        listViewMasterData.add(entry);
    }

    public void addNewTaskNote(ActionEvent actionEvent) {
        NotesEntry entry = new TaskNotesEntryDetailsImpl();
        entry.setId(FileUtils.getNewFileName(entry.getType()));
        entry.setName("New Note");
        javafxStage.loadView(dailyNotesPane, entry);
        listViewMasterData.add(entry);
    }

}
