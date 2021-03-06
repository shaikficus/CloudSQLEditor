package Controllers;

import DB.SQLConnectionDB;
import Models.Connection;
import Models.SampleData;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ConnectionsController implements Initializable {


    @FXML
    private TextField ConnectionName;
    @FXML
    private TextField UserName;
    @FXML
    private TextField Password;
    @FXML
    private TextField ReportPath;
    @FXML
    private TextField BIURL;
    @FXML
    private Button removeButton;
    @FXML
    private Button createButton;
    @FXML
    private Button updateButton;
    @FXML
    private ListView<Connection> listView;
    @FXML
    private TextArea conntest;
    private static final String SELECTSQL = "select ID, CONN_NAME, CLOUD_USER, CLOUD_PASS, BI_REPORT_PATH, BI_URL from CLOUD_SQL_CONNECTIONS";

    public ObservableList<Connection> getConnectionList() {
        return connectionList;
    }

    private final ObservableList<Connection> connectionList = FXCollections.observableArrayList(Connection.extractor);
    // Observable objects returned by extractor (applied to each list element) are listened for changes and
    // transformed into "update" change of ListChangeListener.

    private Connection selectedConnection;
    private final BooleanProperty modifiedProperty = new SimpleBooleanProperty(false);
    private ChangeListener<Connection> personChangeListener;
    private SQLConnectionDB db = new SQLConnectionDB();


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Disable the Remove/Edit buttons if nothing is selected in the ListView control
        removeButton.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull());
        updateButton.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull()
                .or(modifiedProperty.not())
                .or(ConnectionName.textProperty().isEmpty()
                        .or(UserName.textProperty().isEmpty())));

        //SampleData.fillSampleData(connectionList); Commented by Mustafa
        //SQLConnectionDB db = new SQLConnectionDB();
        try {
            db.executeQuery(SELECTSQL,connectionList);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // Use a sorted list; sort by lastname; then by firstname
        SortedList<Connection> sortedList = new SortedList<>(connectionList);

        // sort by lastname first, then by firstname; ignore notes
        sortedList.setComparator((p1, p2) -> {
            int result = p1.getUserName().compareToIgnoreCase(p2.getUserName());
            if (result == 0) {
                result = p1.getConnectionName().compareToIgnoreCase(p2.getConnectionName());
            }
            return result;
        });
        listView.setItems(sortedList);

        listView.getSelectionModel().selectedItemProperty().addListener(
                personChangeListener = (observable, oldValue, newValue) -> {
                    System.out.println("Selected item: " + newValue);
                    // newValue can be null if nothing is selected
                    selectedConnection = newValue;
                    modifiedProperty.set(false);
                    if (newValue != null) {
                        // Populate controls with selected Connection
                        ConnectionName.setText(selectedConnection.getConnectionName());
                        UserName.setText(selectedConnection.getUserName());
                        Password.setText(selectedConnection.getPassword());
                        ReportPath.setText(selectedConnection.getReportPath());
                        BIURL.setText(selectedConnection.getBIURL());
                    } else {
                        ConnectionName.setText("");
                        UserName.setText("");
                        Password.setText("");
                        ReportPath.setText("");
                        BIURL.setText("");
                    }
                });

        // Pre-select the first item
        listView.getSelectionModel().selectFirst();

    }



    @FXML
    private void handleKeyAction(KeyEvent keyEvent) {
        modifiedProperty.set(true);
    }

    @FXML
    private void createButtonAction(ActionEvent actionEvent) {
        System.out.println("Create");
        Connection connection = new Connection();
        connectionList.add(connection);

        // and select it
        listView.getSelectionModel().select(connection);
    }

    @FXML
    private void removeButtonAction(ActionEvent actionEvent) {
        System.out.println("Remove " + selectedConnection);
        try {
            db.checkConnection(selectedConnection,"DELETE");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        connectionList.remove(selectedConnection);
    }

    @FXML
    private void updateButtonAction(ActionEvent actionEvent) {
        System.out.println("Update " + selectedConnection);
        Connection p = listView.getSelectionModel().getSelectedItem();
        listView.getSelectionModel().selectedItemProperty().removeListener(personChangeListener);
        p.setConnectionName(ConnectionName.getText());
        p.setUserName(UserName.getText());
        p.setPassword(Password.getText());
        p.setReportPath(ReportPath.getText());
        p.setBIURL(BIURL.getText());
        System.out.println(p.getConnectionName()+"-"+p.getUserName());
        try {
              db.checkConnection(p,"UPDATE");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        listView.getSelectionModel().selectedItemProperty().addListener(personChangeListener);
        modifiedProperty.set(false);
    }

    public void testButtonAction(ActionEvent actionEvent) {
        System.out.println("Test " + selectedConnection);
        conntest.setText("Test Connection: " + selectedConnection);
    }
}
