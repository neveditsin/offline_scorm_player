package db;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import Utils.FileHelper;


public class DbUtils{


	private static final String URL = "jdbc:derby:" + FileHelper.GetRootFolder() + "/.derby;create=false";
	private static final String DBHOME = FileHelper.GetRootFolder() + "/.derby";
	@SuppressWarnings("unused")
	private static final String DBSHUTDW = "jdbc:derby:;shutdown=true";
	private static final String QUERY_CLEAR_DB = "DELETE FROM scorm";
	
	private static AtomicReference<Connection> CON = new AtomicReference<Connection>();

	
	
	private synchronized static Connection getConnection() throws SQLException {
		synchronized(URL) {
			if(CON.get() == null) {
				CON.set(DriverManager.getConnection(URL));
			}
		}
		
	

		return CON.get();
	}
	
    private enum ExecType {
    	SELECT,
    	DMLDDL
    }
	
    
    @SuppressWarnings("unused")
	private static void bootstrap() throws SQLException {
		final String createScormStorage = "CREATE TABLE scorm\r\n" + 
    			"(\r\n" + 
    			"id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),\r\n" +
    			"dt TIMESTAMP,\r\n" + 
    			"version VARCHAR(64),\r\n" + 
    			"api_call VARCHAR(32),\r\n" + 
    			"module VARCHAR(1024),\r\n" + 
    			"wname VARCHAR(1024),\r\n" + 
    			"cmi VARCHAR(32672),\r\n" + 
    			"CONSTRAINT primary_key PRIMARY KEY (id)\r\n" + 
    			")";
    	

		final String deleteScormStorage = "DROP TABLE scorm";
    	
    	//System.out.println(execute(getConnection(), null, deleteScormStorage, ExecType.DMLDDL));
    	//System.out.println(execute(getConnection(), null, createScormStorage, ExecType.DMLDDL));
    	System.out.println(execute(getConnection(), null, QUERY_CLEAR_DB, ExecType.DMLDDL));
    }
    
	public static void main(String[] args) throws SQLException {
		
		//bootstrap();
		//putScormData(new java.util.Date(), "SCORM 1.2", "call", "module", "wname", "cmi{}");

		//System.out.println(execute(getConnection(), null, "SELECT * FROM scorm", ExecType.SELECT));

	}
	
	@SuppressWarnings("unchecked")
	public static List<HashMap<String,Object>> getScormReport() throws SQLException {	
		return (List<HashMap<String, Object>>) execute(getConnection(), null, "SELECT * FROM scorm", ExecType.SELECT);
	}
	
	
	
	public static int clearData() throws SQLException {
		Connection con = getConnection();
		return (Integer) execute(getConnection(), null, QUERY_CLEAR_DB, ExecType.DMLDDL);
	}
	
	
	public static int putScormData(java.util.Date dt, String ver, String api_call, String module, String wname, String cmi) throws SQLException {
		Connection con = getConnection();
		PreparedStatement pst = con.prepareStatement("INSERT INTO scorm (dt, version, api_call, module, wname, cmi) VALUES (?, ?, ?, ?, ? ,?)");	
		pst.setTimestamp(1, new Timestamp(dt.getTime()));
		pst.setString(2, ver);
		pst.setString(3, api_call);
		pst.setString(4, module);
		pst.setString(5, wname);
		pst.setString(6, cmi);
		
		execute(con, pst, null, ExecType.DMLDDL);
		return 0;
	}
	
	
	
	private static Object execute(Connection con, PreparedStatement pst, String query, ExecType et) throws SQLException {	
		Statement st = null;			
		Object result = null;
		
		try {
			
			System.setProperty("derby.system.home", DBHOME);

			st = con.createStatement();
			if (et.equals(ExecType.DMLDDL)) {				
				result = pst != null? pst.executeUpdate() : st.executeUpdate(query);
			} else {
				result = convertResultSetToList(st.executeQuery(query));
			}
				

			//DriverManager.getConnection(DBSHUTDW);

		} catch (SQLException ex) {

			if (((ex.getErrorCode() == 50000) && ("XJ015".equals(ex.getSQLState())))) {

				// System.out.println("Derby shut down normally");

			} else {
				throw ex;
			}

		} finally {
			try {
				if (st != null) {
					st.close();
				}				
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					//con.close();
				}

			} catch (SQLException ex) {
				System.err.println(ex);
			}
		}
		
		return result;
	}
	
	
	//https://stackoverflow.com/questions/7507121/efficient-way-to-handle-resultset-in-java
	private static List<HashMap<String,Object>> convertResultSetToList(ResultSet rs) throws SQLException {
	    ResultSetMetaData md = rs.getMetaData();
	    int columns = md.getColumnCount();
	    List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();

	    while (rs.next()) {
	        HashMap<String,Object> row = new HashMap<String, Object>(columns);
	        for(int i=1; i<=columns; ++i) {
	            row.put(md.getColumnName(i),rs.getObject(i));
	        }
	        list.add(row);
	    }

	    return list;
	}
}