package com.xlog.app.data;

import java.io.File;
import java.sql.*;

public final class Database {
  private static String DB_URL;

  public static void init() throws SQLException {
    File dir = new File(System.getProperty("user.home"), ".xlog");
    if (!dir.exists()) dir.mkdirs();
    File db = new File(dir, "zenith.db");
    DB_URL = "jdbc:sqlite:" + db.getAbsolutePath();

    try (Connection c = getConnection(); Statement st = c.createStatement()) {
      // Ensure DB exists and at least one tasks table exists (legacy or new)
      st.execute(
        "CREATE TABLE IF NOT EXISTS tasks (" +
        " id INTEGER PRIMARY KEY AUTOINCREMENT," +
        " name TEXT NOT NULL," +
        " stat TEXT," +                  // nullable for now, we’ll enforce later
        " created_at INTEGER NOT NULL DEFAULT (strftime('%s','now'))" +
        ")"
      );

      // Detect legacy column 'tag' and presence of 'stat'
      boolean hasTag = hasColumn(c, "tasks", "tag");
      boolean hasStat = hasColumn(c, "tasks", "stat");

      // If old DB has only 'tag', add 'stat' and backfill from 'tag'
      if (hasTag && !hasStat) {
        st.execute("ALTER TABLE tasks ADD COLUMN stat TEXT");
        // Map legacy values to enum names (uppercased); default to STRENGTH if unknown
        st.execute(
          "UPDATE tasks SET stat = CASE UPPER(tag) " +
          " WHEN 'STRENGTH' THEN 'STRENGTH' " +
          " WHEN 'INTELLIGENCE' THEN 'INTELLIGENCE' " +
          " WHEN 'CHARISMA' THEN 'CHARISMA' " +
          " WHEN 'CHI' THEN 'CHI' " +
          " ELSE 'STRENGTH' END " +
          " WHERE stat IS NULL OR TRIM(stat)=''"
        );
      }

      // If the legacy 'tag' column still exists (and is often NOT NULL),
      // rebuild the table WITHOUT 'tag' to stop future NOT NULL failures.
      if (hasTag) {
        st.execute("BEGIN");
        try {
          st.execute(
            "CREATE TABLE IF NOT EXISTS tasks_new (" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " name TEXT NOT NULL," +
            " stat TEXT NOT NULL," +
            " created_at INTEGER NOT NULL DEFAULT (strftime('%s','now'))" +
            ")"
          );
          st.execute(
            "INSERT INTO tasks_new(id,name,stat,created_at) " +
            "SELECT id, name, COALESCE(NULLIF(stat,''),'STRENGTH'), created_at FROM tasks"
          );
          st.execute("DROP TABLE tasks");
          st.execute("ALTER TABLE tasks_new RENAME TO tasks");
          st.execute("COMMIT");
        } catch (SQLException ex) {
          st.execute("ROLLBACK");
          throw ex;
        }
      }

      // Final safety: ensure every row has a valid stat
      st.execute("UPDATE tasks SET stat='STRENGTH' WHERE stat IS NULL OR TRIM(stat)=''");
    }
  }

  public static Connection getConnection() throws SQLException {
    if (DB_URL == null) throw new SQLException("Database not initialized. Call Database.init() first.");
    return DriverManager.getConnection(DB_URL);
  }

  /** Utility: does a table have a given column? */
  private static boolean hasColumn(Connection c, String table, String col) {
    try (PreparedStatement ps = c.prepareStatement("PRAGMA table_info(" + table + ")")) {
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          if (col.equalsIgnoreCase(rs.getString("name"))) return true;
        }
      }
    } catch (SQLException ignored) {}
    return false;
  }

  private Database() {}
}
