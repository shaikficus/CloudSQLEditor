package DB;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CreateDB {
    public static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
public static final String JDBC_URL = "jdbc:derby:cloudsqldb;create=true";
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER);
        Connection conn = (Connection) DriverManager.getConnection(JDBC_URL);
        conn.createStatement().execute("CREATE TABLE CLOUD_SQL_CONNECTIONS (\n" +
                "ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,\n" +
                "CONN_NAME VARCHAR(100),\n" +
                "CLOUD_USER VARCHAR(100),\n" +
                "CLOUD_PASS VARCHAR(100),\n" +
                "BI_REPORT_PATH VARCHAR(1000),\n" +
                "BI_URL VARCHAR(1000),\n" +
                "ATTRIBUTE1 VARCHAR(100),\n" +
                "ATTRIBUTE2 VARCHAR(100),\n" +
                "ATTRIBUTE3 VARCHAR(100),\n" +
                "ATTRIBUTE4 VARCHAR(100),\n" +
                "ATTRIBUTE5 VARCHAR(100),\n" +
                "ATTRIBUTE6 VARCHAR(100),\n" +
                "ATTRIBUTE7 VARCHAR(100),\n" +
                "ATTRIBUTE8 VARCHAR(100),\n" +
                "ATTRIBUTE9 VARCHAR(100),\n" +
                "ATTRIBUTE10 VARCHAR(100),\n" +
                "PRIMARY KEY (ID)\n" +
                ")");
        conn.createStatement().execute("INSERT INTO CLOUD_SQL_CONNECTIONS (CONN_NAME, CLOUD_USER,CLOUD_PASS,BI_REPORT_PATH,BI_URL)VALUES \n" +
                "('DEV1', 'TESTUSER1','TESTPASS','/Custom/Query/Report/Query.xdo','https://dev1.oracle.cloud.com/publicreportservice.wsdl')\n" +
                ",('DEV2', 'TESTUSER2','TESTPASS','/Custom/Query/Report/Query.xdo','https://dev1.oracle.cloud.com/publicreportservice.wsdl')\n" +
                ",('DEV3', 'TESTUSER2','TESTPASS','/Custom/Query/Report/Query.xdo','https://dev1.oracle.cloud.com/publicreportservice.wsdl')\n");
        System.out.println("Table Created");

    }
}
