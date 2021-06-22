package Models;

import Controllers.FileTabController;
import Controllers.FileTabPaneController;
import javafx.stage.FileChooser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A model used to store data associated with the view FileTabPane.fxml
 * and FileTabPaneController.
 */
public class FileTabPane {
    private final Map<FileTab, FileTabController> fileTabControllerMap;
    private final FileChooser fileChooser;

    /**
     * Constructs a new FileTabPane.
     * @param controller of the file tab pane.
     */
    public FileTabPane(FileTabPaneController controller) {
        this.fileTabControllerMap = new HashMap<>();
        this.fileChooser = new FileChooser();
        // Set extension filter on the file chooser.
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
    }

    /**
     *
     * @return a FileChooser with txt extensions.
     */
    public FileChooser getFileChooser() {
        return fileChooser;
    }

    /**
     * Adds a file tab to the hash map.
     * @param fileTab to be added to the map.
     * @param fileTabController to be added to the map.
     */
    public void addFileTab(FileTab fileTab, FileTabController fileTabController) {
        fileTabControllerMap.put(fileTab, fileTabController);
    }

    /**
     * Removes a file tab from the hash map.
     * @param fileTab to be removed from the map.
     */
    public void removeFileTab(FileTab fileTab) {
        fileTabControllerMap.remove(fileTab);
    }

    /**
     *
     * @return the file tab controller map used to associate file tabs with
     * their respective controller.
     */
    public Map<FileTab, FileTabController> getFileTabControllerMap() {
        return Collections.unmodifiableMap(fileTabControllerMap);
    }
}
