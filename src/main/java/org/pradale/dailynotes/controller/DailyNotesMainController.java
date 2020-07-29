package org.pradale.dailynotes.controller;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.textfield.CustomTextField;
import org.pradale.dailynotes.component.NotesTreeItem;
import org.pradale.dailynotes.component.TreeCellImpl;
import org.pradale.dailynotes.events.UpdateNotesEntryEvent;
import org.pradale.dailynotes.model.NotesEntry;
import org.pradale.dailynotes.model.entry.MarkDownNotesEntryDetailsImpl;
import org.pradale.dailynotes.model.entry.RichTextNotesEntryDetailsImpl;
import org.pradale.dailynotes.model.entry.TaskDescNotesEntryDetailsImpl;
import org.pradale.dailynotes.model.entry.TaskNotesEntryDetailsImpl;
import org.pradale.dailynotes.service.JavafxStage;
import org.pradale.dailynotes.service.NotesService;
import org.pradale.dailynotes.util.ComponentUtils;
import org.pradale.dailynotes.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private FilteredList<NotesEntry> filteredData = new FilteredList(listViewMasterData);

    @FXML
    private AnchorPane dailyNotesPane;

    @FXML
    private CustomTextField textFieldTaskSearch;

    @FXML
    private SplitPane splitPaneParent;

    @FXML
    private AnchorPane splitPaneParentLeftPane;

    @FXML
    private AnchorPane splitPaneNotesLeftPane;

    @FXML
    private TreeView treeViewMaster;

    @FXML
    public void initialize() {
        eventBus.register(this);
        initializeComponents();
        initializeTree();
    }

    private void initializeComponents() {
        List<NotesEntry> entries = notesService.loadAll();
        listViewMasterData.addAll(entries);
        sortNotesEntry();

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

        splitPaneParentLeftPane.maxWidthProperty().bind(splitPaneParent.widthProperty().multiply(0.15));
        splitPaneNotesLeftPane.maxWidthProperty().bind(splitPaneParent.widthProperty().multiply(0.15));
    }

    public void initializeTree() {
        TreeItem rootItem = new TreeItem("Root");

        int nTotals = listViewMasterData.size();
        int nRecents = 0;
        int nArchived = 0;
        int nTrash = 0;
        int nUnTags = 0;

        Set<String> tags = new HashSet<>();
        for (NotesEntry entry : listViewMasterData) {
            if (CollectionUtils.isNotEmpty(entry.getTags())) {
                tags.addAll(entry.getTags());
            } else {
                nUnTags++;
            }
        }

        int nTags = tags.size();

        NotesTreeItem allNotes = new NotesTreeItem(getNodeName("All Notes", nTotals), eventBus);
        NotesTreeItem recentItems = new NotesTreeItem(getNodeName("Recent", nRecents), eventBus);
        NotesTreeItem archivedItems = new NotesTreeItem(getNodeName("Archived", nArchived), eventBus);
        NotesTreeItem trashItems = new NotesTreeItem(getNodeName("Trash", nTrash), eventBus);
        NotesTreeItem tagItems = new NotesTreeItem(getNodeName("Tags", nTags), eventBus);
        NotesTreeItem unTagItems = new NotesTreeItem(getNodeName("Un-Tags", nUnTags), eventBus);

        for (String tag : tags) {
            tagItems.getChildren().add(new NotesTreeItem(tag, eventBus));
        }

        rootItem.getChildren().add(allNotes);
        rootItem.getChildren().add(recentItems);
        rootItem.getChildren().add(archivedItems);
        rootItem.getChildren().add(trashItems);
        rootItem.getChildren().add(new TreeItem(null));
        rootItem.getChildren().add(tagItems);
        rootItem.getChildren().add(unTagItems);

        treeViewMaster.setRoot(rootItem);
        treeViewMaster.setShowRoot(false);
        treeViewMaster.setCellFactory(param -> {
            return new TreeCellImpl();
        });
        treeViewMaster.refresh();
        MultipleSelectionModel msm = treeViewMaster.getSelectionModel();
        msm.select(allNotes);

        treeViewMaster.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<NotesTreeItem>() {

            @Override
            public void changed(ObservableValue<? extends NotesTreeItem> observable, NotesTreeItem oldValue, NotesTreeItem newValue) {
                if(newValue.getValue() != null) {
                    String selectedValue = newValue.getValue().toString();

                    if(selectedValue.startsWith("All Notes")) {
                        filteredData.setPredicate(null);
                    }else if(selectedValue.startsWith("Tags")) {
                        filteredData.setPredicate(data -> {
                            if (data.getTags() != null && data.getTags().size() > 0) {
                                return true;
                            }
                            return false;
                        });

                    }else if(selectedValue.startsWith("Un-Tags")) {
                        filteredData.setPredicate(data -> {
                            if (data.getTags() == null || data.getTags().size() == 0) {
                                return true;
                            }
                            return false;
                        });

                    }

                    listViewMaster.setItems(filteredData);
                    listViewMaster.refresh();
                }
            }
        });
    }

    private String getNodeName(String node, int size) {
        return String.format("%s (%d)", node, size);
    }

    @Subscribe
    public void updateNotesEntry(UpdateNotesEntryEvent event) {
        sortNotesEntry();
        listViewMaster.refresh();
    }

    public void sortNotesEntry() {
//        Collections.sort(listViewMasterData, new Comparator<NotesEntry>() {
//            @Override
//            public int compare(NotesEntry obj1, NotesEntry obj2) {
//
//                if (obj1.getType() == NotesEntryType.TASK && obj2.getType() == NotesEntryType.TASK) {
//                    TaskNotesEntryDetailsImpl entry1 = (TaskNotesEntryDetailsImpl) obj1;
//                    TaskNotesEntryDetailsImpl entry2 = (TaskNotesEntryDetailsImpl) obj2;
//
//                    return Boolean.compare(entry1.isCompleted(), entry2.isCompleted());
//                } else {
//                    return 1;
//                }
//            }
//        });
    }

    public void addNewBasicNote(ActionEvent actionEvent) {
        NotesEntry entry = new MarkDownNotesEntryDetailsImpl();
        entry.setId(FileUtils.getNewFileName(entry.getType()));
        entry.setName("New Note");
        javafxStage.loadView(dailyNotesPane, entry);
        listViewMasterData.add(entry);
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

    public void addNewTaskWithDescNote(ActionEvent actionEvent) {
        NotesEntry entry = new TaskDescNotesEntryDetailsImpl();
        entry.setId(FileUtils.getNewFileName(entry.getType()));
        entry.setName("New Note");
        javafxStage.loadView(dailyNotesPane, entry);
        listViewMasterData.add(entry);
    }
}
