import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.List;

import com.mysql.jdbc.Connection;

public class DBHelper {

	private Connection conn;
	private static DBHelper db;

	private DBHelper() {
		initializeDBConn();
	}
	
	public synchronized static DBHelper getDBInstance(){
		if(db == null){
			db = new DBHelper();
		}
		return db;
	}

	private void initializeDBConn() {
		try {
			System.out.println("Connecting to database");
			Class.forName("com.mysql.jdbc.Driver");
			this.conn = (Connection) DriverManager.getConnection(
					Configuration.dbURL, Configuration.dbUser,
					Configuration.dbPassword);
			System.out.println("Connected to database");
		} catch (SQLException e) {
			System.out.println("Cannot connect the database!");
			e.printStackTrace();
		} catch (ClassNotFoundException ex) {
			System.out.println("Cannot connect the database!");
			ex.printStackTrace();
		}
	}

	public void insertTweetIntoDB(TweetNode node) {
		String SQL = "insert into tweets values (?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);
			stmt.setObject(1, node.getId(), java.sql.Types.BIGINT);
			stmt.setObject(2, node.getUsername(), java.sql.Types.VARCHAR);
			stmt.setObject(3, node.getText(), java.sql.Types.VARCHAR);
			stmt.setObject(4, node.getLatitude(), java.sql.Types.DOUBLE);
			stmt.setObject(5, node.getLongitude(), java.sql.Types.DOUBLE);
			stmt.setObject(6, node.getTimestamp(), java.sql.Types.TIMESTAMP);
			stmt.setObject(7, node.getSentiment(), java.sql.Types.VARCHAR);
			stmt.setObject(8, node.getType(), java.sql.Types.VARCHAR);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Error while inserting tweet into database");
			e.printStackTrace();
		}
	}

	public List<TweetNode> getAllTweetsFromDB(String word, String type) {
		List<TweetNode> list = new LinkedList<TweetNode>();
		String SQL = "select * from tweets";
		if (word != null && word.length() > 0) {
			SQL += " where text like '%@" + word + "%'";
		}
		if (type != null && type.length() > 0) {
			SQL += (word != null && word.length() > 0) ? " and " : " where ";
			SQL += " type = '" + type + "'";
		}
		System.out.println("SQL: " + SQL);
		try {
			ResultSet rs = conn.createStatement().executeQuery(SQL);
			while (rs.next()) {
				TweetNode node = new TweetNode(rs.getObject(1, long.class),
						rs.getObject(2, String.class), rs.getObject(3,
								String.class), rs.getObject(4, double.class),
						rs.getObject(5, double.class), rs.getObject(6,
								Date.class), rs.getObject(7, String.class),
								rs.getObject(8, String.class));
				list.add(node);
			}
			if(type != null && type.equals("live")){
				deleteAllTweetsFromDB(type);
			}
		} catch (SQLException e) {
			System.out.println("Error while fetching tweets from database");
			e.printStackTrace();
		}
		System.out.println("Fetched " + list.size() + " records");
		return list;
	}

	public void deleteAllTweetsFromDB(String type) {
		try {
			System.out.println("Deleting all the tweets from database");
			if(type != null && type.length() > 0){
				conn.createStatement().executeUpdate("delete from tweets where type = '" + type + "'");
			} else {
				conn.createStatement().executeUpdate("delete from tweets");
			}
		} catch (SQLException e) {
			System.out.println("Deletion of all the tweets failed");
			e.printStackTrace();
		}
	}

	public void deleteTweetWithStatusId(LinkedList<Long> list) {
		StringBuilder ids = new StringBuilder();
		ids.append("(");
		for (long no : list) {
			ids.append(no).append(", ");
		}
		ids.setLength(ids.length() - 2);
		ids.append(")");
		// System.out.println(ids);

		String SQL = "delete from tweets where id in " + ids.toString();
		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<String> getListOfKeywords() {
		List<String> list = new LinkedList<String>();
		String SQL = "Select text from tweets where text like '%@%'";
		ResultSet rs;
		try {
			rs = conn.createStatement().executeQuery(SQL);
			while (rs.next()) {
				String word = rs.getString(1);
				if (word.contains("@")) {
					word = word.substring(word.indexOf("@") + 1);
					int index = word.indexOf(" ");
					if (index > 0) {
						word = word.substring(0, index);
					} else {
						word = word.substring(0);
					}
					if (!word.contains(" "))
						list.add(word);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	public int getTweetCount() {
		String SQL = "select count(*) from tweets";
		try {
			ResultSet rs = conn.createStatement().executeQuery(SQL);
			rs.next();
			return rs.getObject(1, int.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
