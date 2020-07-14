package org.pradale.dailynotes.model;

public enum NotesEntryType {
    RICH_TEXT("views/entry/rich-notes-view.fxml", "pdrn"),
    MARK_DOWN("views/entry/mark-down-notes-view.fxml", "pdmn"),
    TASK("views/entry/task-notes-view.fxml", "pdtn"),
    TASK_WITH_DESC("views/entry/task-desc-notes-view.fxml", "pdtdn");

    private String file;
    private String extension;

    NotesEntryType(String file, String extension) {
        this.file = file;
        this.extension = extension;
    }

    public String viewFile() {
        return file;
    }

    public String getExtension() {
        return extension;
    }
}