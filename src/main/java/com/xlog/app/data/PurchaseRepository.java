
package com.xlog.app.data; import java.sql.*; import java.util.*;
public class PurchaseRepository {
  public boolean isOwned(String key){ try(Connection c=Database.getConnection(); PreparedStatement ps=c.prepareStatement("SELECT 1 FROM purchases WHERE item_key=? LIMIT 1")){ ps.setString(1,key); try(ResultSet rs=ps.executeQuery()){ return rs.next(); } } catch(SQLException e){ e.printStackTrace(); } return false; }
  public boolean buy(String key){ try(Connection c=Database.getConnection(); PreparedStatement ps=c.prepareStatement("INSERT OR IGNORE INTO purchases(item_key) VALUES(?)")){ ps.setString(1,key); return ps.executeUpdate()>0; } catch(SQLException e){ e.printStackTrace(); } return false; }
  public Set<String> ownedKeys(){ Set<String> s=new HashSet<>(); try(Connection c=Database.getConnection(); PreparedStatement ps=c.prepareStatement("SELECT item_key FROM purchases"); ResultSet rs=ps.executeQuery()){ while(rs.next()) s.add(rs.getString(1)); } catch(SQLException e){ e.printStackTrace(); } return s; }
}
