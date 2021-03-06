package Controllers;

import Models.SaveDialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SaveDialogController implements Initializable {

    private SaveDialog saveDialog;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    // FXML ACTIONS

    /**
     * Saves the prompted text area to the file.
     * @param event to be handled.
     * @throws IOException
     */
    @FXML
    private void saveFileByDialog(ActionEvent event) throws IOException {
        Tab tab = saveDialog.getTab();
        if (tab != null) {
            saveDialog.getFileTabPaneController().saveFile(tab);
            saveDialog.getStage().close();
        }

    }

    /**
     * Closes the prompted file tab.
     * @param event to be handled.
     * @throws IOException
     * @throws Exception
     */
    @FXML
    private void closeFileByDialog(ActionEvent event) throws IOException, Exception {
        Tab tab = saveDialog.getTab();
        if (tab != null) {
            saveDialog.getFileTabPaneController().forceCloseFile(tab);
            saveDialog.getStage().close();
        }
    }
    /**
     * Closes the save dialog and won't do any action.
     * @param event to be handled.
     */
    @FXML private void closeSaveDialog(ActionEvent event) {
        saveDialog.getStage().close();
    }
    /**
     * Sets the save dialog model.
     * WARNING: This must be set for each new SaveDialog.fxml view to collect
     * the correct references for the button actions.
     * @param saveDialog
     */
    public void setSaveDialog(SaveDialog saveDialog) {
        this.saveDialog = saveDialog;
    }
    /**
     *
     * @return the save dialog model.
     */
    public SaveDialog getSaveDialog() {
        return saveDialog;
    }
}
