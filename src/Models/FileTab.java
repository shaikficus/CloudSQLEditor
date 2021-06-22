package Models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;

public class FileTab {
    private final ObjectProperty<File> file;
    private final SimpleBooleanProperty savedState;

    /**
     * Constructs a new FileTab.
     * @param file to be associated with the file tab.
     */
    public FileTab(File file) {
        this.savedState = new SimpleBooleanProperty(true);
        this.file = new SimpleObjectProperty<>(file);
    }

    /**
     *
     * @return the saved state of this file tab. A saved state returns false
     * if any changes were made in the editor, and where not written to the
     * file.
     */
    public boolean getSavedState() {
        return savedState.get();
    }

    /**
     * Set the saved state of this file tab.
     * @param value
     */
    public void setSavedState(boolean value) {
        savedState.set(value);
    }

    /**
     *
     * @return the saved state property.
     */
    public SimpleBooleanProperty savedStateProperty() {
        return savedState;
    }

    /**
     * Set the file to be associated with this file tab.
     * @param value to be set as file.
     */
    public void setFile(File value) {
        file.set(value);
    }

    /**
     *
     * @return the file property.
     */
    public ObjectProperty<File> fileProperty() {
        return file;
    }

    /**
     *
     * @return the current file.
     */
    public File getFile() {
        return file.get();
    }
}

