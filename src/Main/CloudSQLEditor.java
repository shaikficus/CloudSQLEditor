package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class CloudSQLEditor extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Views/MainPane.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        String appName = "Cloud SQL Editor Beta 1.0";
        stage.setTitle(appName);
        stage.setScene(scene);
        stage.getIcons().add(new Image("/Styling/CloudEditor.png"));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
