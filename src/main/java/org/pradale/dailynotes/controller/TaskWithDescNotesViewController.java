package org.pradale.dailynotes.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.HTMLEditor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pradale.dailynotes.events.SaveNotesEntryEvent;
import org.pradale.dailynotes.events.UpdateNotesEntryEvent;
import org.pradale.dailynotes.model.NotesEntryPriority;
import org.pradale.dailynotes.model.entry.AbstractNotesEntryDetails;
import org.pradale.dailynotes.model.entry.SubTaskEntry;
import org.pradale.dailynotes.model.entry.TaskDescNotesEntryDetailsImpl;
import org.pradale.dailynotes.util.AppUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class TaskWithDescNotesViewController extends AbstractDailyNotesController {

    private TaskDescNotesEntryDetailsImpl taskDescNotesEntryDetails;

    @FXML
    private ListView<SubTaskEntry> listViewSubTasks;

    @FXML
    private CheckBox checkCompleted;

    @FXML
    private Button buttonClose;

    @FXML
    private Button buttonAddSubTask;

    @FXML
    private HTMLEditor htmlEditorDesc;

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
        if (!(notesEntry instanceof TaskDescNotesEntryDetailsImpl)) {
            throw new IllegalArgumentException("Required TaskNotesEntryDetailsImpl object");
        }

        this.taskDescNotesEntryDetails = (TaskDescNotesEntryDetailsImpl) notesEntry;
    }

    @Override
    public AbstractNotesEntryDetails getEntryDetails() {
        return taskDescNotesEntryDetails;
    }

    @FXML
    public void initialize() {
        super.initialize();
        initializeSubTaskView();
        eventBus.register(this);
    }

    private void initializeSubTaskView() {
        ObservableList<SubTaskEntry> listViewMasterData = FXCollections.observableList(taskDescNotesEntryDetails.getSubTaskEntries());

        // complete checkbox
        if (taskDescNotesEntryDetails.isCompleted()) {
            checkCompleted.setSelected(true);
            eventBus.post(new SaveNotesEntryEvent(taskDescNotesEntryDetails));
            eventBus.post(new UpdateNotesEntryEvent(taskDescNotesEntryDetails));
        }
        checkCompleted.setOnAction(value -> {
            taskDescNotesEntryDetails.setCompleted(checkCompleted.isSelected());
            eventBus.post(new SaveNotesEntryEvent(taskDescNotesEntryDetails));
            eventBus.post(new UpdateNotesEntryEvent(taskDescNotesEntryDetails));
        });

        buttonClose.setOnAction(action -> {
            taskDescNotesEntryDetails.setDeleted(true);
            eventBus.post(new SaveNotesEntryEvent(taskDescNotesEntryDetails));
            eventBus.post(new UpdateNotesEntryEvent(taskDescNotesEntryDetails));
        });

        buttonAddSubTask.setOnAction(action -> {
            addSubTask();
        });

        // editor
        if (StringUtils.isNotBlank(taskDescNotesEntryDetails.getDescription())) {
            htmlEditorDesc.setHtmlText(taskDescNotesEntryDetails.getDescription());
        }

        htmlEditorDesc.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                taskDescNotesEntryDetails.setDescription(htmlEditorDesc.getHtmlText());

                if (event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.PERIOD) {
                    eventBus.post(new SaveNotesEntryEvent(taskDescNotesEntryDetails));
                    eventBus.post(new UpdateNotesEntryEvent(taskDescNotesEntryDetails));
                }
            }
        });

        taskDescNotesEntryDetails.setSubTaskEntries(listViewMasterData);
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
                        eventBus.post(new SaveNotesEntryEvent(taskDescNotesEntryDetails));
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
                            eventBus.post(new SaveNotesEntryEvent(taskDescNotesEntryDetails));
                        }
                    });

                    cancelButton.setOnAction(click -> {
                        listViewSubTasks.getItems().remove(subTaskEntry);
                        eventBus.post(new SaveNotesEntryEvent(taskDescNotesEntryDetails));
                    });

                    HBox.setHgrow(description, Priority.ALWAYS);
                    bBox.getChildren().addAll(completeCheck, cancelButton, description);
                    setGraphic(bBox);
                }
            }
        });

        buttonPriority.setText(taskDescNotesEntryDetails.getPriority().name());

        if (StringUtils.isNotBlank(taskDescNotesEntryDetails.getDateStart())) {
            dateStart.setValue(LocalDate.parse(taskDescNotesEntryDetails.getDateStart(), DATE_FORMATTER));
        }

        if (StringUtils.isNotBlank(taskDescNotesEntryDetails.getDateEnd())) {
            dateEnd.setValue(LocalDate.parse(taskDescNotesEntryDetails.getDateEnd(), DATE_FORMATTER));
        }

        dateStart.setOnAction(action -> {
            taskDescNotesEntryDetails.setDateStart(dateStart.getValue().format(DATE_FORMATTER));
            eventBus.post(new SaveNotesEntryEvent(taskDescNotesEntryDetails));
        });

        dateEnd.setOnAction(action -> {
            taskDescNotesEntryDetails.setDateEnd(dateEnd.getValue().format(DATE_FORMATTER));
            eventBus.post(new SaveNotesEntryEvent(taskDescNotesEntryDetails));
        });
    }

    public void addSubTask() {
        SubTaskEntry taskEntry = new SubTaskEntry();
        String id = AppUtils.generateTaskId();
        taskEntry.setId(id);
        listViewSubTasks.getItems().add(taskEntry);
        eventBus.post(new SaveNotesEntryEvent(taskDescNotesEntryDetails));
    }

    public void setPriority(ActionEvent actionEvent) {
        MenuItem menuItem = (MenuItem) actionEvent.getSource();

        if (menuItem.getText().equalsIgnoreCase(NotesEntryPriority.LOW.name())) {
            taskDescNotesEntryDetails.setPriority(NotesEntryPriority.LOW);
        } else if (menuItem.getText().equalsIgnoreCase(NotesEntryPriority.MEDIUM.name())) {
            taskDescNotesEntryDetails.setPriority(NotesEntryPriority.MEDIUM);
        } else {
            taskDescNotesEntryDetails.setPriority(NotesEntryPriority.HIGH);
        }
    }
}