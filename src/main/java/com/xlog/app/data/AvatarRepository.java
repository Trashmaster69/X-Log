
package com.xlog.app.data;
import com.xlog.app.models.AvatarSelection; import com.xlog.app.models.AvatarSelection.BodyColor; import com.xlog.app.models.AvatarSelection.EyeType; import com.xlog.app.models.AvatarSelection.Displayable;
import java.sql.*;
public class AvatarRepository {
  public AvatarSelection load(){ try(Connection c=Database.getConnection(); PreparedStatement ps=c.prepareStatement("SELECT body,eyes,display FROM avatar_selection WHERE id=1"); ResultSet rs=ps.executeQuery()){ if(rs.next()){ BodyColor b=BodyColor.valueOf(rs.getString(1)); EyeType e=EyeType.valueOf(rs.getString(2)); String d=rs.getString(3); AvatarSelection.Displayable disp=(d==null||d.isEmpty())?AvatarSelection.Displayable.NONE:AvatarSelection.Displayable.valueOf(d); return new AvatarSelection(b,e,disp); } } catch(SQLException e){ e.printStackTrace(); } return new AvatarSelection(); }
  public void save(AvatarSelection sel){ String sql="INSERT INTO avatar_selection(id, body, eyes, display) VALUES(1,?,?,?) ON CONFLICT(id) DO UPDATE SET body=excluded.body, eyes=excluded.eyes, display=excluded.display"; try(Connection c=Database.getConnection(); PreparedStatement ps=c.prepareStatement(sql)){ ps.setString(1, sel.getBody().name()); ps.setString(2, sel.getEyes().name()); ps.setString(3, sel.getDisplay()==null? null : sel.getDisplay().name()); ps.executeUpdate(); } catch(SQLException e){ e.printStackTrace(); } }
}
