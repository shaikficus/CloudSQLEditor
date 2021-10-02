package Main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class CloudSQLEditor extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Views/MainPane.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        String appName = "Developer Sidekick Beta 1.0";
        stage.setTitle(appName);
        stage.setScene(scene);
        stage.getIcons().add(new Image("/Styling/118633-84.png"));
        stage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to close this Window?", ButtonType.YES, ButtonType.NO);
            ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
            if (ButtonType.NO.equals(result)) {
                event.consume();
            }else{
            Platform.exit();
            System.exit(0);}

        });
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
