package Controllers;

import DB.SQLConnectionDB;
import Highlight.SQLHighlight;
import Models.Connection;
import Models.FileTab;
import com.opencsv.CSVWriter;
import com.sun.javafx.scene.control.behavior.TextInputControlBehavior;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import ficus.service.facade.CloudSQLEditorServiceFacade;
import ficus.service.proxy.AccessDeniedException;
import ficus.service.proxy.InvalidParametersException;
import ficus.service.proxy.OperationFailedException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.util.Callback;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.reactfx.Subscription;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileTabController implements Initializable {
    private FileTab fileTab;
    private FileTabPaneController fileTabPaneController;
    private static final String SELECTLISTSQL = "select ID, CONN_NAME, CLOUD_USER, CLOUD_PASS, BI_REPORT_PATH, BI_URL from CLOUD_SQL_CONNECTIONS";
    private final ObservableList<Connection> connnameList = FXCollections.observableArrayList(Connection.extractor);
    private String selectedConnectionName;
    private Connection selectedConnection;
    private SQLConnectionDB db = new SQLConnectionDB();
    private CloudSQLEditorServiceFacade repService;
    private ArrayList<String[]> rowdata;
    private  String[] csvCols;

    private Subscription subscription;
    private static final String parseQuery = "SELECT * FROM ( #query ) WHERE ROWNUM <= #row";

    //Initialize all Properties
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Add a listener to the text area to set unsaved state on input.
        rows.setMaxWidth(75.0);
        rows.setEditable(true);
        textArea.setDisable(true);
        textArea.setOpacity(0.00);
        run.setDisable(true);
        disconnect.setDisable(true);
        try {
            db.executeQuery(SELECTLISTSQL,connnameList);
            connlist.setItems(connnameList);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //service = new CloudSQLEditorServiceFacade();

        // Overrides the tab closing with our closeTabIfSaved() method so we
        // can listen for saved state.
        subscription = new SQLHighlight(textArea).highlight();
        tab.setOnCloseRequest(removeSelectedTabIfSavedListener());
    }

    /**
     *
     * @return the file tab pane controller.
     */
    public FileTabPaneController getFileTabPaneController() {
        return fileTabPaneController;
    }
    /**
     * Sets the file tab pane window controller on this file tab controller.
     * @param fileTabPaneController
     */
    public void setFileTabPaneController(FileTabPaneController fileTabPaneController) {
        this.fileTabPaneController = fileTabPaneController;
    }

    /**
     * Will save the contents of the TextArea to the associated File
     * if it is valid. If it is not valid, a prompt will ask for
     * file selection.
     * @throws java.io.IOException
     */
    public void saveToFile() throws IOException {
        File file = fileTab.getFile();
        if (file != null && file.isFile()) {
            writeToFile();
            updateFileTab();
        }
        else {
            promptSaveToFile();
        }
    }

    /**
     * Prompts the user to select a file to which we can write the textarea.
     * @throws java.io.IOException
     */
    public void promptSaveToFile() throws IOException {
        FileChooser fileChooser = fileTabPaneController.getFileTabPane().getFileChooser();
        File file = fileChooser.showSaveDialog(fileTabPaneController.getWindow());
        if (file != null) {
            fileTab.setFile(file);
            writeToFile();
            updateFileTab();
        }
    }

    /**
     *
     * @return this controller file tab.
     */
    public FileTab getFileTab() {
        return fileTab;
    }

    /**
     * @param fileTab to be set as the controller file tab.
     * @throws java.io.IOException
     */
    public void setFileTab(FileTab fileTab) throws IOException {
        this.fileTab = fileTab;
        updateFileTab();
    }

    /**
     * Sets the file associated with the FileTab and updates its name in the
     * tab.
     * @param file to be opened on the tab.
     * @throws java.io.IOException
     */
    public void setFile(File file) throws IOException {
        fileTab.setFile(file);
        updateFileTab();
    }

    /**
     * Undo the last change to the text area.
     */

    public void undo() {
      // ((TextInputControlBehavior)((BehaviorSkinBase)textArea.getSkin()).getBehavior()).callAction("Undo"); //Commented by Mustafa

    }




    /**
     * Redo the last change to the text area.
     */

    public void redo() {
       // ((TextInputControlBehavior)((BehaviorSkinBase)textArea.getSkin()).getBehavior()).callAction("Redo"); //Commented by Mustafa
    }



    /**
     * Cut the current selection in the text area.
     */
    public void cut() {
        textArea.cut();
    }

    /**
     * Copy the current selection in the text area.
     */
    public void copy() {
        textArea.copy();
    }

    /**
     * Paste the currently cut/copied text to the text area.
     */
    public void paste() {
        textArea.paste();
    }

    /**
     * Select all the text in the text area.
     */
    public void selectAll() {
        textArea.selectAll();
    }

    /**
     *
     * @return the Tab associated with this controller.
     */
    public Tab getTab() {
        return tab;
    }

    /**
     *
     * @return the TextArea associated with this controller.
     */
    /*Commented by Mustafa
    public TextArea getTextArea() {
        return textArea;
    }
*/
    public CodeArea getTextArea() {
        return textArea;
    }
    // PRIVATE METHODS

    /**
     * Updates the text area to match the file text and the
     * tab text to match the file name.
     */
    private void updateFileTab() throws IOException {
        File file = fileTab.getFile();
        if (file.isFile()) {
            tab.setText(file.getName());
            //Commented by Mustafa remove below lines
//            textArea.setText(getFileOutputAsString(file));

            textArea.replaceText(getFileOutputAsString(file)); //Added by Mustafa.
//
            fileTab.setSavedState(true);
        }
    }

    /**
     * Saves the current text in the text area to the file and sets
     * the current saved state to true.
     */
    private void writeToFile() throws IOException {
        File file = fileTab.getFile();
        if (file != null) {
            try(FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                Writer writer = new BufferedWriter(osw)) {
                writer.write(textArea.getText());
                // Set saved state to true.
                fileTab.setSavedState(true);
            }
        }
    }

    /**
     *
     * @param file to be returned as a String.
     * @return the File output as a String.
     * @throws FileNotFoundException
     * @throws IOException
     */
    private String getFileOutputAsString(File file) throws FileNotFoundException, IOException {
        try(FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);) {
            StringBuilder sb = new StringBuilder();
            while (bis.available() > 0) {
                sb.append((char) bis.read());
            }
            return sb.toString();
        }
    }

    /**
     *
     * @return an event handler used to override the normal tab closing
     * so we can prompt the user to save the file if the state is unsaved.
     */
    private EventHandler<Event> removeSelectedTabIfSavedListener() {
        return (final Event event) -> {
            try {
                Tab tab1 = (Tab) event.getSource();
                event.consume();
                fileTabPaneController.closeFile(tab1);
            }catch (Exception ex) {
                Logger.getLogger(FileTab.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
    }

    /**
     * @return a saved state listener which sets a file tab saved state
     * to false.
     */
    private ChangeListener<String> savedStateListener() {
        return (new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                fileTab.setSavedState(false);
            }
        });
    }
    public void executeQueryKey(KeyEvent event){

        boolean isCtrlDown = event.isControlDown();
        String returnKey = event.getCode().toString();
        if(isCtrlDown && returnKey.equalsIgnoreCase("ENTER") ){
            getQueryResults();
        }


    }
    public void executeQuery(ActionEvent actionEvent) {
        getQueryResults();
    }

    public void getQueryResults(){
        Alert msgPopUp = new Alert(Alert.AlertType.NONE);
        //Popup msgPopUp = new Popup();
        StringBuilder errMsg = new StringBuilder();
        String query = textArea.getSelectedText().replace(";","");
        if (query.length() == 0) {
            query = textArea.getText().replace(";","").trim();
        }
        if(query.length()!=0) {
            String out = null;
//        System.out.println("Number of Rows: "+rows.getValueFactory().getValue().intValue());
            System.out.println(textArea.getText());
            System.out.println(textArea.getSelectedText());
            StringBuilder logtext = new StringBuilder();
            logtext.append("Connected to " + selectedConnection.getConnectionName() + " logged in as " + selectedConnection.getUserName() + " and Instance URL " + selectedConnection.getBIURL());
            logtext.append(System.getProperty("line.separator"));
            logtext.append("-----------------------------------------------------------------------------------------------------------------");
            logtext.append(System.getProperty("line.separator"));
            logtext.append(parseQuery.replace("#query", query).replace("#row", rows.getValueFactory().getValue().toString()));
            String encodedString = null;
            try {
                Instant start = Instant.now();
                encodedString = Base64.getEncoder().withoutPadding().encodeToString(query.getBytes());
                repService.setSqlQuery(encodedString);
                out = repService.executeCloudSql();
                Instant end = Instant.now();
                Duration timeElapsed = Duration.between(start, end);
                //logtext.append(System.getProperty("line.separator"));
                //logtext.append(out);
                //Clear Table
                result.getItems().clear();
                result.getColumns().clear();
                log.setText(String.valueOf(logtext));
                Document xmlOut = convertStringtoXML("<" + out);
                xmlOut.getDocumentElement().normalize();
//        System.out.println("Root element :" + xmlOut.getDocumentElement().getNodeName());
                NodeList nList = xmlOut.getElementsByTagName("ROW");
//        System.out.println("Row Count :"+nList.getLength());
//        for (int i = 0; i < nList.getLength(); i++) {
                NodeList childList = nList.item(0).getChildNodes();
//            System.out.println("Col Count : "+childList.getLength());
                List<String> listCols = new ArrayList<String>();
                listCols.add("SNO");
                 csvCols = new String[childList.getLength()];
                csvCols[0] = "SNO";
                int counter = 1;
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        listCols.add(childNode.getNodeName().toString());
                        csvCols[counter] = childNode.getNodeName().toString();
                        counter++;
                    }
                }


                for (int i = 1; i <= listCols.size(); i++) {
                    final int j = i;
                    TableColumn<String[], String> column = new TableColumn<>();
                    column.setText(listCols.get(i - 1));
                    column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<String[], String> p) {
                            String[] x = p.getValue();
                            if (x != null && x.length > 0) {
                                return new SimpleStringProperty(x[j - 1]);
                            } else {
                                return new SimpleStringProperty("");
                            }
                        }
                    });
                    result.getColumns().add(column);
                }

                rowdata = new ArrayList<String[]>();
                for (int i = 0; i < nList.getLength(); i++) {
                    NodeList childListval = nList.item(i).getChildNodes();
                    String[] rowarray = new String[(listCols.size()+1)];
                    rowarray[0] = String.valueOf(i+1);
                    int colCount = 1;
                    for (int j = 0; j < childListval.getLength(); j++) {
                        Node childNode = childListval.item(j);
                        if (childNode.getNodeType() == Node.ELEMENT_NODE) {

                            rowarray[colCount] = childNode.getTextContent();
                            //System.out.println("Col Value: "+j+": "+childNode.getTextContent());
                            colCount++;
                        }
                    }
                    rowdata.add(rowarray);
                }
                result.getItems().addAll(rowdata);
                elapsedtime.setText(timeElapsed.toString());
                rowcount.setText(String.valueOf(rowdata.size()));

            } catch (AccessDeniedException e) {

                errMsg.append(e.getMessage());
                errMsg.append(System.getProperty("line.separator"));
                //e.printStackTrace();
            } catch (InvalidParametersException e) {

                errMsg.append(e.getMessage());
                errMsg.append(System.getProperty("line.separator"));
                // e.printStackTrace();
            } catch (OperationFailedException e) {

                errMsg.append(e.getMessage());
                errMsg.append(System.getProperty("line.separator"));
                //e.printStackTrace();
            } catch (UnsupportedEncodingException e) {

                errMsg.append(e.getMessage());
                errMsg.append(System.getProperty("line.separator"));
                // e.printStackTrace();
            } catch (Exception e) {

                errMsg.append(e.getMessage());
                errMsg.append(System.getProperty("line.separator"));
                //e.printStackTrace();
            }
            if (errMsg.length() != 0) {
                msgPopUp.setAlertType(Alert.AlertType.ERROR);
                msgPopUp.setResizable(true);
                msgPopUp.setTitle("Error Message");
                msgPopUp.setHeaderText("Error in processing query!!");
                msgPopUp.setContentText(errMsg.toString());
                msgPopUp.show();
            }
        }
    }
    //FXML VARIABLES: DO NOT CHANGE.
    @FXML
    private Tab tab;
    //@FXML private TextArea textArea;
    @FXML private CodeArea textArea;
    @FXML private Spinner<Integer> rows;
    @FXML private TextArea log;
    @FXML private ChoiceBox connlist;
    @FXML private Button run;
    @FXML private Button connect;
    @FXML private Button disconnect;
    @FXML private TextField rowcount;
    @FXML private TextField elapsedtime;




    @FXML private TableView result;
    // END OF FXML VARIABLES

    public void setConnection(ActionEvent actionEvent) {
        Alert connMsg = new Alert(Alert.AlertType.NONE);
        Object selectedItem = connlist.getSelectionModel().getSelectedItem();
        if (selectedItem!=null){
        selectedConnectionName = connlist.getValue().toString();

        System.out.println("   ChoiceBox.getValue(): " + connlist.getValue());
        String selectedConnDetailsQuery = SELECTLISTSQL+" WHERE CONN_NAME = '"+selectedConnectionName+"'";
        System.out.println("Get Connection Query: "+selectedConnDetailsQuery);
        ObservableList<Connection> selectedConn = FXCollections.observableArrayList(Connection.extractor);;
        try {
            db.executeQuery(selectedConnDetailsQuery, selectedConn);
            selectedConnection = selectedConn.get(0);
        } catch (SQLException throwables) {
            connMsg.setAlertType(Alert.AlertType.ERROR);
            connMsg.setResizable(true);
            connMsg.setTitle("Error Message");
            connMsg.setHeaderText("SQL Error in getting connection list from DB!!");
            connMsg.setContentText(throwables.getMessage());
            connMsg.show();
        }catch(Exception e){
            connMsg.setAlertType(Alert.AlertType.ERROR);
                connMsg.setResizable(true);
                connMsg.setTitle("Error Message");
                connMsg.setHeaderText("Error in getting connection list from DB!!");
                connMsg.setContentText(e.getMessage());
                connMsg.show();
            }

            try {
                repService = new CloudSQLEditorServiceFacade(selectedConnection.getUserName(), selectedConnection.getPassword(), selectedConnection.getReportPath(), selectedConnection.getBIURL());
                repService.reportLogin();
                connMsg.setAlertType(Alert.AlertType.INFORMATION);
                connMsg.setTitle("Information");
                connMsg.setHeaderText("Connected to "+selectedConnection.getConnectionName()+"!!");
                connMsg.show();
            } catch (MalformedURLException e) {
                connMsg.setAlertType(Alert.AlertType.ERROR);
                connMsg.setResizable(true);
                connMsg.setTitle("Error Message");
                connMsg.setHeaderText("Malformed URL Error in connecting Report Service!!");
                connMsg.setContentText(e.getMessage());
                connMsg.show();
            } catch (AccessDeniedException e) {
                connMsg.setAlertType(Alert.AlertType.ERROR);
                connMsg.setResizable(true);
                connMsg.setTitle("Error Message");
                connMsg.setHeaderText("Access Error in connecting Report Service!!");
                connMsg.setContentText(e.getMessage());
                connMsg.show();
            }
            tab.setText(selectedConnectionName+"-Untitled Tab");
        tab.setTooltip(new Tooltip("User: "+selectedConnection.getUserName()+" URL: "+selectedConnection.getBIURL()));
        run.setDisable(false);
        connect.setDisable(true);
        connlist.setDisable(true);
        disconnect.setDisable(false);
        textArea.setDisable(false);

            textArea.textProperty().addListener(savedStateListener());
            textArea.setParagraphGraphicFactory(LineNumberFactory.get(textArea));
            textArea.setOpacity(100.00);
            connlist.setStyle("");
        }else{
            connlist.setTooltip(new Tooltip("Select Connection"));
            connlist.setStyle("-fx-border-color:red; -fx-border-width: 1; -fx-border-style: solid;");
            Alert selectConn = new Alert(Alert.AlertType.WARNING);
            selectConn.setHeaderText("Select Connection from the List!!");
            selectConn.showAndWait();
        }

    }

    public void resetConnection(ActionEvent actionEvent) {
        Alert resetconnMsg = new Alert(Alert.AlertType.NONE);
        try {
            repService.reportLogout();
            connlist.setValue(null);
            run.setDisable(true);
            connect.setDisable(false);
            connlist.setDisable(false);
            disconnect.setDisable(true);
        } catch (AccessDeniedException e) {
            resetconnMsg.setAlertType(Alert.AlertType.ERROR);
            resetconnMsg.setResizable(true);
            resetconnMsg.setTitle("Error Message");
            resetconnMsg.setHeaderText("Access Error in connecting Report Service!!");
            resetconnMsg.setContentText(e.getMessage());
            resetconnMsg.show();
        } catch (InvalidParametersException e) {
            resetconnMsg.setAlertType(Alert.AlertType.ERROR);
            resetconnMsg.setResizable(true);
            resetconnMsg.setTitle("Error Message");
            resetconnMsg.setHeaderText("Invalid Parameter Requested in connecting Report Service!!");
            resetconnMsg.setContentText(e.getMessage());
            resetconnMsg.show();
        }


    }

    public void validateConnection(ActionEvent actionEvent) {
//        if (connlist.getValue()==null||connlist.getValue().toString().equalsIgnoreCase("")){
//            run.setDisable(true);
//            connect.setDisable(true);
//            disconnect.setDisable(true);
//        }
        System.out.println("Selected Connection Listner "+connlist.getValue());
    }
   private Document convertStringtoXML(String strOut){
       //Parser that produces DOM object trees from XML content
       Alert stringToXmlMsg = new Alert(Alert.AlertType.NONE);
       DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
       DocumentBuilder builder = null;
       try {
           builder = factory.newDocumentBuilder();
           Document doc = builder.parse(new InputSource(new StringReader(strOut)));
           return doc;
       } catch (ParserConfigurationException e) {
           stringToXmlMsg.setAlertType(Alert.AlertType.ERROR);
           stringToXmlMsg.setResizable(true);
           stringToXmlMsg.setTitle("Error Message");
           stringToXmlMsg.setHeaderText("Parse Exception in converting String to XML!!");
           stringToXmlMsg.setContentText(e.getMessage());
           stringToXmlMsg.show();
       } catch (SAXException e) {
           stringToXmlMsg.setAlertType(Alert.AlertType.ERROR);
           stringToXmlMsg.setResizable(true);
           stringToXmlMsg.setTitle("Error Message");
           stringToXmlMsg.setHeaderText("SAX Exception in converting String to XML!!");
           stringToXmlMsg.setContentText(e.getMessage());
           stringToXmlMsg.show();
       } catch (IOException e) {
           stringToXmlMsg.setAlertType(Alert.AlertType.ERROR);
           stringToXmlMsg.setResizable(true);
           stringToXmlMsg.setTitle("Error Message");
           stringToXmlMsg.setHeaderText("IO Exception in converting String to XML!!");
           stringToXmlMsg.setContentText(e.getMessage());
           stringToXmlMsg.show();
       }
       return null;
   }


    public void exportData(ActionEvent actionEvent) {
//        DirectoryChooser directoryChooser = new DirectoryChooser();
//        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
//        File selectedDirectory = directoryChooser.showDialog(rowcount.getScene().getWindow());
//        System.out.println(selectedDirectory.getAbsolutePath());

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String strDate = dateFormat.format(date);
        fileChooser.setInitialFileName("Export_"+strDate+".csv");
        File selectedFile = fileChooser.showSaveDialog(rowcount.getScene().getWindow());
        if (selectedFile != null) {
        String csv = selectedFile.getAbsolutePath();

            System.out.println(csv);
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(csv));
                writer.writeNext(csvCols);
                writer.writeAll(rowdata);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
