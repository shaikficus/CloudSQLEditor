package DB;

import java.sql.*;

public class QueryDB {

    public static String sql = "SELECT COUNT(1) CONN_COUNT from CLOUD_SQL_CONNECTIONS where CONN_NAME = ";
            //"select ID, CONN_NAME, CLOUD_USER, CLOUD_PASS, BI_REPORT_PATH, BI_URL from CLOUD_SQL_CONNECTIONS";

    public static void main(String[] args) throws SQLException {
        Connection conn = (Connection) DriverManager.getConnection(CreateDB.JDBC_URL);
        Statement stmt = conn.createStatement();
        sql = sql+"'DEV1'";
        System.out.println(sql);
        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData rsm = rs.getMetaData();
        int columns = rsm.getColumnCount();
        for (int x = 1; x <= columns; x++) System.out.format("%30s", rsm.getColumnName(x) + " | ");
        while (rs.next()){
            System.out.println("");
            for (int x = 1; x <= columns; x++) System.out.format("%30s", rs.getString(x) + " | ");
        }
        if (stmt != null) stmt.close();
        if(conn!=null) conn.close();
    }
}