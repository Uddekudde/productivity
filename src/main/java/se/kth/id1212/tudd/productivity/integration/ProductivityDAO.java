/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.tudd.productivity.integration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.bean.ApplicationScoped;

/**
 * A Facade to the database
 *
 * @author udde
 */
@ApplicationScoped
@Stateless
public class ProductivityDAO {

    private static final String NAME_COLUMN = "name";
    private static final String PROCESS_ID_COLUMN = "process_id";
    private static final String PROCESS_TABLE = "process";
    private static final String TIME_COLUMN = "process";
    
    private static final String dbms = "derby";
    private static final String databaseName = "ProductivityDatabase";
    private PreparedStatement insertProcessesStmt;
    private PreparedStatement createCategoryStmt;
    private PreparedStatement updateProcessStmt;
    private PreparedStatement findAllProcesses;
    private PreparedStatement findProcessByName;
    
    
    private Connection connection;

    public ProductivityDAO() throws Exception {
        try {
            Connection connection = createDatasource();
            this.connection = connection;
            prepareStatements(connection);
        } catch (ClassNotFoundException | SQLException exception) {
            System.out.println(exception.getMessage());
            throw new Exception("Could not connect to datasource.", exception);
        }
    }

    /**
     * Connects to the database.
     * 
     * @param dbms
     * @param datasource
     * @return A connection to the database.
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws Exception 
     */
    private Connection connectToRecruitmentDB(String dbms, String datasource)
            throws ClassNotFoundException, SQLException, Exception {
        if (dbms.equalsIgnoreCase("derby")) {
            Class.forName("org.apache.derby.jdbc.ClientXADataSource");
            return DriverManager.getConnection(
                    "jdbc:derby://localhost:1527/" + datasource + ";create=true");
        } else {
            throw new Exception("Unable to create datasource, unknown dbms.");
        }
    }
    
    private boolean tablesExist(Connection connection) throws SQLException {
        int tableNameColumn = 3;
        DatabaseMetaData dbm = connection.getMetaData();
        try (ResultSet rs = dbm.getTables(null, null, null, null)) {
            for (; rs.next();) {
                if (rs.getString(tableNameColumn).equalsIgnoreCase(PROCESS_TABLE)) {
                    return true;
                }
            }
            return false;
        }
    }

    private Connection createDatasource() throws
            ClassNotFoundException, SQLException, Exception {
        Connection connection = connectToRecruitmentDB(dbms, databaseName);
        if (!tablesExist(connection)) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE "+PROCESS_TABLE+" ("+NAME_COLUMN+" VARCHAR(255) PRIMARY KEY, "+TIME_COLUMN+" BIGINT)");
            createCategories(connection);
        }
        return connection;
    }
    
    private void createCategories(Connection connection) throws SQLException{
        createCategoryStmt = connection.prepareStatement("INSERT INTO "
                + PROCESS_TABLE + " VALUES ( ?, ?)");
        int initialTime = 0;
        try {
            createCategoryStmt.setString(1,"gaming");
            createCategoryStmt.setInt(2,initialTime);
            createCategoryStmt.executeUpdate();
            
            createCategoryStmt.setString(1,"productive");
            createCategoryStmt.setInt(2,initialTime);
            createCategoryStmt.executeUpdate();
            
            createCategoryStmt.setString(1,"browsing");
            createCategoryStmt.setInt(2,initialTime);
            createCategoryStmt.executeUpdate();
        } catch (SQLException sqle) {
            throw sqle;
        }
    }
    
    public void updateCategoryTime(String categoryName, int categoryTime){
        try {
            updateProcessStmt.setString(2,categoryName);
            updateProcessStmt.setInt(1, categoryTime);
            updateProcessStmt.execute();
        } catch (Exception e) {
            System.err.println("Couldn't update times");
        }
    }
    
    public int getCategoryTime(String categoryName) throws Exception{
        ResultSet resultSet = null;
        try {
            findProcessByName.setString(1, categoryName);
            resultSet = findProcessByName.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt(TIME_COLUMN);
            }
        } catch (Exception e) {
            System.out.println("could not get category time");
        } finally {
            try {
                resultSet.close();
            } catch (Exception e) {
                System.out.println("could not get category time");
            }
        }
        throw new Exception();
    }
        
    public HashMap<String, Integer> getCategories() throws Exception{
        HashMap<String, Integer> result = new HashMap<>();
        String failureMsg = "error xd";
        ResultSet resultSet = null;
        try {
            resultSet = findAllProcesses.executeQuery();
            while(resultSet.next()) {
                result.put(resultSet.getString(NAME_COLUMN), resultSet.getInt(TIME_COLUMN));
            }
        } catch (SQLException sqle) {
            throw new Exception(failureMsg, sqle);
        } finally {
            try {
                resultSet.close();
            } catch (Exception e) {
                System.out.println(failureMsg);
            }
        }
        return result;
    }
    
    private void prepareStatements(Connection connection) throws SQLException {
        insertProcessesStmt = connection.prepareStatement("INSERT INTO "
                + PROCESS_TABLE + " VALUES ( ?, ?)");
        updateProcessStmt = connection.prepareStatement("UPDATE "+PROCESS_TABLE+" SET "+TIME_COLUMN+" = (?) WHERE "+NAME_COLUMN+" = (?)");
        findAllProcesses = connection.prepareStatement("SELECT "+NAME_COLUMN+", "+TIME_COLUMN+" FROM "+PROCESS_TABLE+" ");
        findProcessByName = connection.prepareStatement("SELECT "+TIME_COLUMN+" FROM "+PROCESS_TABLE+" WHERE "+NAME_COLUMN+" = (?) ");
    }
}
