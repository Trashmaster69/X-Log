package com.xlog.app.data;

import com.xlog.app.models.StatType;
import com.xlog.app.models.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
  private static final List<Task> completedSession = new ArrayList<>();

  public List<Task> getPending() {
    List<Task> out = new ArrayList<>();
    try (Connection c = Database.getConnection();
         PreparedStatement ps = c.prepareStatement("SELECT id,name,stat FROM tasks ORDER BY id DESC");
         ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        out.add(new Task(
            rs.getLong(1),
            rs.getString(2),
            StatType.valueOf(rs.getString(3))
        ));
      }
    } catch (SQLException e) { e.printStackTrace(); }
    return out;
  }

  public List<Task> getCompletedSession() { return completedSession; }

  public void add(String name, StatType stat) {
    try (Connection c = Database.getConnection();
         PreparedStatement ps = c.prepareStatement("INSERT INTO tasks(name,stat) VALUES(?,?)")) {
      ps.setString(1, name);
      ps.setString(2, stat.name());
      ps.executeUpdate();
    } catch (SQLException e) { e.printStackTrace(); }
  }

  public void complete(long id) {
    // Move to session list (for display) then delete from DB
    try (Connection c = Database.getConnection();
         PreparedStatement get = c.prepareStatement("SELECT id,name,stat FROM tasks WHERE id=?")) {
      get.setLong(1, id);
      try (ResultSet rs = get.executeQuery()) {
        if (rs.next()) {
          completedSession.add(new Task(
              rs.getLong(1),
              rs.getString(2),
              StatType.valueOf(rs.getString(3))
          ));
        }
      }
    } catch (SQLException e) { e.printStackTrace(); }

    try (Connection c = Database.getConnection();
         PreparedStatement del = c.prepareStatement("DELETE FROM tasks WHERE id=?")) {
      del.setLong(1, id);
      del.executeUpdate();
    } catch (SQLException e) { e.printStackTrace(); }
  }
}
