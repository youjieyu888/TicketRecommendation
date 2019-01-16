package db;

import db.mongodb.MongoDBConnection;
import db.mysql.MySQLConnection;

// so the method name is db independent, 
//if we need to change the db, just change the parameter
public class DBConnectionFactory {
	// This should change based on the pipeline.
	private static final String DEFAULT_DB = "mysql";
	
	public static DBconnection getConnection(String db) {
		switch (db) {
		case "mysql":
			return new MySQLConnection();
		case "mongodb":
			return new MongoDBConnection();
		default:
			throw new IllegalArgumentException("Invalid db:" + db);
		}

	}

	public static DBconnection getConnection() {
		return getConnection(DEFAULT_DB);
	}
}

