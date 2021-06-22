package Models;

import Controllers.FileTabPaneController;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

public class SaveDialog {
    private final Stage stage;
    private final Tab tab;
    private final FileTabPaneController fileTabPaneController;

    /**
     * Constructs a new save dialog.
     * @param stage to be associated with the save dialog.
     * @param tab to be associated with the save dialog.
     * @param fileTabPaneController to be associated with the save dialog.
     */
    public SaveDialog(Stage stage, Tab tab, FileTabPaneController fileTabPaneController) {
        this.fileTabPaneController = fileTabPaneController;
        this.stage = stage;
        this.tab = tab;
    }

    /**
     *
     * @return the FileTabPaneController.
     */
    public FileTabPaneController getFileTabPaneController() {
        return fileTabPaneController;
    }

    /**
     *
     * @return the stage for the save dialog.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     *
     * @return the tab which called the save dialog.
     */
    public Tab getTab() {
        return tab;
    }
}

