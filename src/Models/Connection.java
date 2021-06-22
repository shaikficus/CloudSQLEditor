package Models;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

import java.util.Objects;

public class Connection {

    private final StringProperty ConnectionName = new SimpleStringProperty(this, "ConnectionName", "");
    private final StringProperty UserName = new SimpleStringProperty(this, "UserName", "");
    private final StringProperty Password = new SimpleStringProperty(this, "Password", "*********");
    private final StringProperty ReportPath = new SimpleStringProperty(this, "ReportPath", "");
    private final StringProperty BIURL = new SimpleStringProperty(this, "BIURL", "");

    public Connection() {
        this.ConnectionName.set("<New Connection>");
    }

    public Connection(String ConnectionName, String UserName, String Password, String ReportPath, String BIURL) {
        this.ConnectionName.set(ConnectionName);
        this.UserName.set(UserName);
        this.Password.set(Password);
        this.ReportPath.set(ReportPath);
        this.BIURL.set(BIURL);
    }

    public String getConnectionName() {
        return ConnectionName.get();
    }

    public StringProperty ConnectionNameProperty() {
        return ConnectionName;
    }

    public void setConnectionName(String ConnectionName) {
        this.ConnectionName.set(ConnectionName);
    }

    public String getUserName() {
        return UserName.get();
    }

    public StringProperty UserNameProperty() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName.set(UserName);
    }

    public String getPassword() {
        return Password.get();
    }

    public StringProperty PasswordProperty() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password.set(Password);
    }

    public String getReportPath() {
        return ReportPath.get();
    }

    public StringProperty ReportPathProperty() {
        return ReportPath;
    }

    public void setReportPath(String ReportPath) {
        this.ReportPath.set(ReportPath);
    }

    public String getBIURL() {
        return BIURL.get();
    }

    public StringProperty BIURLProperty() {
        return BIURL;
    }

    public void setBIURL(String BIURL) {
        this.BIURL.set(BIURL);
    }

    @Override
    public String toString() {
        return ConnectionName.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Connection connection = (Connection) obj;
        return Objects.equals(ConnectionName, connection.ConnectionName) &&
                Objects.equals(UserName, connection.UserName) &&
                Objects.equals(Password, connection.Password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ConnectionName, UserName, Password);
    }

    public static Callback<Connection, Observable[]> extractor = p -> new Observable[]
            {p.UserNameProperty(), p.ConnectionNameProperty()};
}
