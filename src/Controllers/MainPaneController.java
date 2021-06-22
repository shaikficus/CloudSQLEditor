package Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPaneController implements Initializable {

    public FileTabPaneController getFileTabPaneController() {
        return fileTabPaneController;
    }

    /**
     * Removes the find dialog or replace dialog prompt, which ever is open.
     */
    public void removeFindOrReplaceDialog() {
        Node replaceDialog = vBox.lookup("#replaceDialogHBox");
        Node findDialog = vBox.lookup("#findDialogHBox");
        if (replaceDialog != null) {
            vBox.getChildren().remove(replaceDialog);
        } else if (findDialog != null) {
            vBox.getChildren().remove(findDialog);
        }
    }

    // FXML ACTIONS

    /**
     * Adds a new empty file tab.
     *
     * @throws java.io.IOException
     */
    @FXML
    public void newFile() throws IOException {
        fileTabPaneController.newFile();
    }

    /**
     * Opens a prompt to select and open a file tab.
     *
     * @throws IOException
     */
    @FXML
    public void openFile() throws IOException {
        fileTabPaneController.openFile();
    }

    /**
     * Saves the contents of the text area to the file.
     *
     * @throws IOException
     */
    @FXML
    public void saveFile() throws IOException {
        fileTabPaneController.saveSelectedFile();
    }

    /**
     * Save the contents of the text area to a file chosen in a prompt.
     *
     * @throws IOException
     */
    @FXML
    public void saveAsFile() throws IOException {
        fileTabPaneController.saveAsSelectedFile();
    }


    /**
     * Overrides the normal program quit to prompt for file saving.
     *
     * @throws IOException
     */
    @FXML
    public void quitApp() throws IOException {
        fileTabPaneController.closeTabsAndExit();


    }

    /**
     * Undo the most recent text change in the text area.
     */
    @FXML
    public void undo() {
        FileTabController fileTabController = fileTabPaneController.getCurrentFileTabController();
        if (fileTabController != null) {
            fileTabController.undo();
        }
    }

    /**
     * Redo the most recent text change in the text area.
     */
    @FXML
    public void redo() {
        FileTabController fileTabController = fileTabPaneController.getCurrentFileTabController();
        if (fileTabController != null) {
            fileTabController.redo();
        }
    }

    /**
     * Cuts the chosen text within the text area.
     */
    @FXML
    public void cut() {
        FileTabController fileTabController = fileTabPaneController.getCurrentFileTabController();
        if (fileTabController != null) {
            fileTabController.cut();
        }
    }

    /**
     * Copies the chosen text within the text area.
     */
    @FXML
    public void copy() {
        FileTabController fileTabController = fileTabPaneController.getCurrentFileTabController();
        if (fileTabController != null) {
            fileTabController.copy();
        }
    }

    /**
     * Pastes the curretly copied/cut text to the text area.
     */
    @FXML
    public void paste() {
        FileTabController fileTabController = fileTabPaneController.getCurrentFileTabController();
        if (fileTabController != null) {
            fileTabController.paste();
        }
    }

    /**
     * Selects all the text in the text area.
     */
    @FXML
    public void selectAll() {
        FileTabController fileTabController = fileTabPaneController.getCurrentFileTabController();
        if (fileTabController != null) {
            fileTabController.selectAll();
        }
    }


    public void replace(ActionEvent actionEvent) {
    }

    public void find(ActionEvent actionEvent) {
    }


    @FXML
    public void help(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/HelpDialog.fxml"));
        Parent node = loader.load();
        // Set the scene.
        Scene scene = new Scene(node);
        // Set the stage.
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.getIcons().add(new Image("/Styling/CloudEditor.png"));
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void manageConnection(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Connections.fxml"));
        Parent node = loader.load();
        // Set the scene.
        Scene scene = new Scene(node);
        // Set the stage.
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.getIcons().add(new Image("/Styling/CloudEditor.png"));
        stage.show();

    }

    // FXML VARIABLES: DO NOT CHANGE:
    @FXML
    private VBox vBox;
    @FXML
    private FileTabPaneController fileTabPaneController;


    // END OF FXML VARIABLES
}
