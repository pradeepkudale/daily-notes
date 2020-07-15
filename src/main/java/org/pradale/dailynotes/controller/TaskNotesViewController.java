package org.pradale.dailynotes.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pradale.dailynotes.events.SaveNotesEntryEvent;
import org.pradale.dailynotes.events.UpdateNotesEntryEvent;
import org.pradale.dailynotes.model.NotesEntryPriority;
import org.pradale.dailynotes.model.entry.AbstractNotesEntryDetails;
import org.pradale.dailynotes.model.entry.SubTaskEntry;
import org.pradale.dailynotes.model.entry.TaskNotesEntryDetailsImpl;
import org.pradale.dailynotes.util.AppUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class TaskNotesViewController extends AbstractDailyNotesController {

    private TaskNotesEntryDetailsImpl taskNotesEntryDetails;

    @FXML
    private ListView<SubTaskEntry> listViewSubTasks;

    @FXML
    private CheckBox checkCompleted;

    @FXML
    private Button buttonClose;

    @FXML
    private Button buttonAddSubTask;

    @FXML
    private ListView listViewSubTask;

    @FXML
    private DatePicker dateStart;

    @FXML
    private DatePicker dateEnd;

    @FXML
    private SplitMenuButton buttonPriority;

    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public void setNotesEntryDetails(AbstractNotesEntryDetails notesEntry) {
        if (!(notesEntry instanceof TaskNotesEntryDetailsImpl)) {
            throw new IllegalArgumentException("Required TaskNotesEntryDetailsImpl object");
        }

        this.taskNotesEntryDetails = (TaskNotesEntryDetailsImpl) notesEntry;
    }

    @Override
    public AbstractNotesEntryDetails getEntryDetails() {
        return taskNotesEntryDetails;
    }

    @FXML
    public void initialize() {
        super.initialize();
        initializeSubTaskView();
        eventBus.register(this);
    }

    private void initializeSubTaskView() {
        ObservableList<SubTaskEntry> listViewMasterData = FXCollections.observableList(taskNotesEntryDetails.getSubTaskEntries());

        // complete checkbox
        if (taskNotesEntryDetails.isCompleted()) {
            checkCompleted.setSelected(true);
            eventBus.post(new SaveNotesEntryEvent(taskNotesEntryDetails));
            eventBus.post(new UpdateNotesEntryEvent(taskNotesEntryDetails));
        }
        checkCompleted.setOnAction(value -> {
            taskNotesEntryDetails.setCompleted(checkCompleted.isSelected());
            eventBus.post(new SaveNotesEntryEvent(taskNotesEntryDetails));
            eventBus.post(new UpdateNotesEntryEvent(taskNotesEntryDetails));
        });

        buttonClose.setOnAction(action -> {
            taskNotesEntryDetails.setDeleted(true);
            eventBus.post(new SaveNotesEntryEvent(taskNotesEntryDetails));
            eventBus.post(new UpdateNotesEntryEvent(taskNotesEntryDetails));
        });

        buttonAddSubTask.setOnAction(action -> {
            addSubTask();
        });

        taskNotesEntryDetails.setSubTaskEntries(listViewMasterData);
        listViewSubTasks.setItems(listViewMasterData);
        listViewSubTasks.setCellFactory(listView -> new ListCell<SubTaskEntry>() {
            @Override
            protected void updateItem(SubTaskEntry subTaskEntry, boolean empty) {
                super.updateItem(subTaskEntry, empty);

                if (empty) {
                    setGraphic(null);
                } else {

                    HBox bBox = new HBox();
                    bBox.setId(subTaskEntry.getId());

                    CheckBox completeCheck = new CheckBox();
                    HBox.setMargin(completeCheck, new Insets(5, 0, 0, 0));
                    completeCheck.setOpaqueInsets(new Insets(0, 0, 0, 0));

                    if (subTaskEntry.isCompleted()) {
                        completeCheck.setSelected(true);
                    }

                    completeCheck.setOnAction(value -> {
                        subTaskEntry.setCompleted(completeCheck.isSelected());
                        eventBus.post(new SaveNotesEntryEvent(taskNotesEntryDetails));
                        //TODO : Check if all tasks are completed, if yes then mark the parent task as completed
                    });

                    Button cancelButton = new Button();
                    cancelButton.setText("X");
                    HBox.setMargin(cancelButton, new Insets(5, 5, 0, 2));
                    cancelButton.setFont(Font.font("System Regular", FontWeight.BOLD, 9.5));

                    TextField description = new TextField();
                    description.setText(subTaskEntry.getDescription());

                    description.focusedProperty().addListener((ov, oldV, newV) -> {
                        if (!newV) {
                            subTaskEntry.setDescription(description.getText());
                            eventBus.post(new SaveNotesEntryEvent(taskNotesEntryDetails));
                        }
                    });

                    cancelButton.setOnAction(click -> {
                        listViewSubTasks.getItems().remove(subTaskEntry);
                        eventBus.post(new SaveNotesEntryEvent(taskNotesEntryDetails));
                    });

                    HBox.setHgrow(description, Priority.ALWAYS);
                    bBox.getChildren().addAll(completeCheck, cancelButton, description);
                    setGraphic(bBox);
                }
            }
        });

        buttonPriority.setText(taskNotesEntryDetails.getPriority().name());

        if(StringUtils.isNotBlank(taskNotesEntryDetails.getDateStart())) {
            dateStart.setValue(LocalDate.parse(taskNotesEntryDetails.getDateStart(),DATE_FORMATTER));
        }

        if(StringUtils.isNotBlank(taskNotesEntryDetails.getDateEnd())) {
            dateEnd.setValue(LocalDate.parse(taskNotesEntryDetails.getDateEnd(),DATE_FORMATTER));
        }

        dateStart.setOnAction(action -> {
            taskNotesEntryDetails.setDateStart(dateStart.getValue().format(DATE_FORMATTER));
            eventBus.post(new SaveNotesEntryEvent(taskNotesEntryDetails));
        });

        dateEnd.setOnAction(action -> {
            taskNotesEntryDetails.setDateEnd(dateEnd.getValue().format(DATE_FORMATTER));
            eventBus.post(new SaveNotesEntryEvent(taskNotesEntryDetails));
        });
    }

    public void addSubTask() {
        SubTaskEntry taskEntry = new SubTaskEntry();
        String id = AppUtils.generateTaskId();
        taskEntry.setId(id);
        listViewSubTasks.getItems().add(taskEntry);
        eventBus.post(new SaveNotesEntryEvent(taskNotesEntryDetails));
    }

    public void setPriority(ActionEvent actionEvent) {
        MenuItem menuItem = (MenuItem) actionEvent.getSource();

        if(menuItem.getText().equalsIgnoreCase(NotesEntryPriority.LOW.name())) {
            taskNotesEntryDetails.setPriority(NotesEntryPriority.LOW);
        }else if (menuItem.getText().equalsIgnoreCase(NotesEntryPriority.MEDIUM.name())) {
            taskNotesEntryDetails.setPriority(NotesEntryPriority.MEDIUM);
        }else {
            taskNotesEntryDetails.setPriority(NotesEntryPriority.HIGH);
        }
    }
}