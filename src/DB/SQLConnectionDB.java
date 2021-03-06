package DB;


import javafx.collections.ObservableList;

import java.sql.*;

public class SQLConnectionDB {
    public static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String JDBC_URL = "jdbc:derby:cloudsqldb;create=true";



    private Connection createConnection(){
        Connection conn = null;
        try {
            Class.forName(DRIVER);
            conn = (Connection) DriverManager.getConnection(JDBC_URL);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return conn;
    }

    private void closeConnesction(Connection conn){
            try {
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
    }

    public void executeQuery(String query, ObservableList<Models.Connection> connectionList) throws SQLException {
        ResultSet rs = null;
        Connection conn = createConnection();
        if (conn != null){
            Statement stmt = conn.createStatement();
             rs = stmt.executeQuery(query);
            while (rs.next()){
                connectionList.add(new Models.Connection(rs.getString("CONN_NAME"),rs.getString("CLOUD_USER"),rs.getString("CLOUD_PASS"),rs.getString("BI_REPORT_PATH"),rs.getString("BI_URL")));
            }
            if (stmt!=null) stmt.close();
            closeConnesction(conn);
        }
    }

    public void addConnection(Models.Connection connection,  Connection conn, Statement stmt) throws SQLException {
        String insertQuery = "INSERT INTO CLOUD_SQL_CONNECTIONS (CONN_NAME, CLOUD_USER,CLOUD_PASS,BI_REPORT_PATH,BI_URL) VALUES (#VALUES)";
        String colValues = "'"+connection.getConnectionName()+"','"+connection.getUserName()+"','"+connection.getPassword()+"','"+connection.getReportPath()+"','"+connection.getBIURL()+"'";

        if (conn != null){
            stmt = conn.createStatement();
            stmt.execute(insertQuery.replace("#VALUES",colValues));

        }
    }

    public void updateConnection(Models.Connection connection,  Connection conn, Statement stmt) throws SQLException {
        String updateQuery = "UPDATE CLOUD_SQL_CONNECTIONS SET";
        updateQuery = updateQuery+" CLOUD_USER='"+connection.getUserName()+"',CLOUD_PASS='"+connection.getPassword()+"',BI_REPORT_PATH='"+connection.getReportPath()+"',BI_URL='"+connection.getBIURL()+"' WHERE CONN_NAME ='"+connection.getConnectionName()+"'";
        System.out.println("Update Query: "+updateQuery);
        if (conn != null){
             stmt = conn.createStatement();
            int num = stmt.executeUpdate(updateQuery);
            System.out.println("Number of records updated: "+num);

        }
    }

    public void removeConnection(Models.Connection connection,  Connection conn, Statement stmt) throws SQLException {
        String deleteQuery = "DELETE FROM CLOUD_SQL_CONNECTIONS where CONN_NAME = '"+connection.getConnectionName()+"'";
        if (conn != null){
            stmt = conn.createStatement();
            int num = stmt.executeUpdate(deleteQuery);
            System.out.println("Number of records updated: "+num);
        }
    }

    public void checkConnection(Models.Connection connection, String action) throws SQLException {
        String connExistsQuery = "SELECT COUNT(1) CONN_COUNT from CLOUD_SQL_CONNECTIONS where CONN_NAME = ";
        ResultSet rs = null;
        int connCount =0;
        Connection conn = createConnection();
        if (conn != null){
            Statement stmt = conn.createStatement();
            connExistsQuery = connExistsQuery+"'"+connection.getConnectionName()+"'";
            System.out.println("Check Query: "+connExistsQuery);
            rs = stmt.executeQuery(connExistsQuery);
            while (rs.next()){
            connCount = rs.getInt("CONN_COUNT");
            }
            if (connCount > 0) {
                if (action.equalsIgnoreCase("UPDATE")) {
                    updateConnection(connection, conn, stmt);
                    System.out.println("Updating Connection: " + connection.getConnectionName());
                }else{

                        removeConnection(connection, conn, stmt);

                }
            } else {
            if (action.equalsIgnoreCase("UPDATE")) {
                addConnection(connection, conn, stmt);
                System.out.println("Inserting Connection: "+connection.getConnectionName());
            }
            }
            if (stmt!=null) stmt.close();
            closeConnesction(conn);
        }

    }



}
