package service;

import java.sql.*;
import java.util.*;

public class DBService {

   private Connection conn;
    private static DBService instance;

   private DBService() throws SQLException
   {
       //String url = "jdbc:postgresql://localhost:55432/" + ConnectionInfo.DBname;
       //conn = DriverManager.getConnection(url, ConnectionInfo.DBuser, ConnectionInfo.DBpass);
   }

   public static DBService getInstance() throws SQLException {
       if (instance == null) {
           instance = new DBService();
       }
       return instance;
   }


   public List<String> getTables(String schema) throws SQLException
   {
       List<String> tables = new ArrayList<>();

       DatabaseMetaData meta = conn.getMetaData();
       ResultSet rs = meta.getTables(null, schema, "%", new String[]{"TABLE"});

       while (rs.next())
       {
           tables.add(rs.getString("TABLE_NAME"));
       }
       return tables;
   }

   public List<List<Object>> getTableData(String schema, String table) throws SQLException
   {
       List<List<Object>> data = new ArrayList<>();

       ResultSet rs = (ResultSet) doSQL(conn, "SELECT * FROM " + schema + "." + table);

       ResultSetMetaData meta = rs.getMetaData();
       int cols = meta.getColumnCount();

       while (rs.next()) {
           List<Object> row = new ArrayList<>();
           for (int i = 1; i <= cols; i++) {
               row.add(rs.getObject(i));
           }
           data.add(row);
       }
       return data;
   }
   public List<Object> getColumnsNames(String schema, String table) throws SQLException
   {
       List<Object> columns = new ArrayList<>();
       ResultSet rs = (ResultSet) doSQL(conn, "SELECT * FROM " + schema + "." + table + " LIMIT 1");
       ResultSetMetaData meta = rs.getMetaData();
       int columnCount = meta.getColumnCount();
       for (int i = 1; i <= columnCount; i++)
       {
           columns.add(meta.getColumnName(i));
       }
       return columns;
   }

   public static Object doSQL(Connection conn, String command, Object... args) throws SQLException
   {
           PreparedStatement pst = conn.prepareStatement(command, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
           int i = 1;
           for (Object o: args)
           {
               if (o instanceof Integer)
               {
                   pst.setInt(i, (Integer) o);
               }
               else if (o instanceof String)
               {
                   pst.setString(i, (String) o);
               }
               else if (o instanceof Double)
               {
                   pst.setDouble(i, (Double) o);
               }
               i++;
           }
           boolean hasResultSet = pst.execute();
           if (hasResultSet)
           {
               return pst.getResultSet();
           }
           else
           {
               return pst.getUpdateCount();
           }
   }
}
