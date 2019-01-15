package work.torp.givespawner.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import work.torp.givespawner.alerts.Alert;
import work.torp.givespawner.classes.PlacedSpawner;
import work.torp.givespawner.helpers.Convert;
import work.torp.givespawner.Main;

public abstract class Database {
    Main plugin;
    Connection connection;

    public String SQLConnectionExecute = "Couldn't execute SQL statement: ";
    public String SQLConnectionClose = "Failed to close SQL connection: "; 
    public String NoSQLConnection = "Unable to retreive SQL connection: ";
    public String NoTableFound = "Database Error: No Table Found";
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
    
    public Database(Main instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize(){
        connection = getSQLConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM placed_spawner");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
   
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, NoSQLConnection, ex);
        }
    }

    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
        	DatabaseError.close(plugin, ex);
        }
    }   

    public void getPlacedSpawner() {
    	
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * from placed_spawner WHERE is_removed = 0;");  
            rs = ps.executeQuery();

            while(rs.next()){
            	try
            	{
            		Location loc = Convert.LocationFromXYZ(rs.getString("world"), Integer.parseInt(rs.getString("x")), Integer.parseInt(rs.getString("y")), Integer.parseInt(rs.getString("z")));
            		if (loc != null)
            		{
            			PlacedSpawner psp = new PlacedSpawner();
            			psp.setPlacedSpawnerID(Convert.IntegerFromString(rs.getString("placed_spawner_id")));
            			psp.setEntityType(EntityType.valueOf(rs.getString("entity_type")));
            			psp.setPlacedByUUID(rs.getString("placed_by_uuid"));
            			psp.setPlacedLocation(loc);
            			try {
                    	    
                    	    Date ftPlacedDateTime = dateFormat.parse(rs.getString("placed_dtime"));
                    	    Timestamp donateDateTime = new java.sql.Timestamp(ftPlacedDateTime.getTime());
                    	    psp.setPlacedDateTime(donateDateTime);
                    	} catch(Exception e) {
                    	    
                    	}
            			
            			Main.getInstance().AddPlacedSpawner(psp);
            		}
            	}
            	catch (Exception ex)
            	{
            		Alert.VerboseLog("Database.getPlacedSpawner", "Unexpected error getting placed spawners from database: " + ex.getMessage());
            	}
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, SQLConnectionExecute, ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, SQLConnectionClose, ex);
            }
        }
    }
    public int savePlacedSpawner(EntityType etype, String uuid, Location spawner_loc) {
    	int placed_spawner_id = -1;
        
    	Connection conn = null;
        PreparedStatement psDb = null;
        PreparedStatement psGetID = null;
        ResultSet rsGetID = null;
        String sql = "";
        try {
        	conn = getSQLConnection();

        	sql = "INSERT INTO placed_spawner (placed_spawner_id, entity_type, placed_by_uuid, world, x, y, z, placed_dtime, is_removed) VALUES (" +
        			"NULL, " + // Inserting null as it is an auto-incrementing value
        			"'" + etype.name() + "', " +
        			"'" + uuid + "', " +
        			"'" + spawner_loc.getWorld().getName() + "', " + 
        			Integer.toString(spawner_loc.getBlockX()) + ", " +
        			Integer.toString(spawner_loc.getBlockY()) + ", " +
        			Integer.toString(spawner_loc.getBlockZ()) + ", " +
        			"'" + new Timestamp(System.currentTimeMillis()) + "', " +
        			"0" +
        			");";

            psDb = conn.prepareStatement(sql);
            psDb.executeUpdate();

        	psGetID = conn.prepareStatement("select MAX(placed_spawner_id) AS seq from placed_spawner;");  
            rsGetID = psGetID.executeQuery();

            while(rsGetID.next()){
            	placed_spawner_id =  Integer.parseInt(rsGetID.getString("seq"));
            }  

    	} catch (Exception ex) {
    		Alert.VerboseLog("Database.savePlacedSpawner", "Unexpected error saving place_spawner");
    		Alert.DebugLog("Database", "savePlacedSpawner", "Unexpected error saving place_spawner: " + ex.getMessage() + " - SQL: " + sql);
        } finally {
            try {
                if (psDb != null)
                    psDb.close();
                if (psGetID != null)
                    psGetID.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, SQLConnectionClose, ex);
            }
        }
        return placed_spawner_id;
    } 
    public int saveBrokenSpawner(EntityType etype, String uuid, Location spawner_loc) {
    	int broken_spawner_id = -1;
        
    	Connection conn = null;
        PreparedStatement psDb = null;
        PreparedStatement psRem = null;
        PreparedStatement psGetID = null;
        ResultSet rsGetID = null;
        String sql = "";
        try {
        	conn = getSQLConnection();

        	sql = "UPDATE placed_spawner SET is_removed = 1 WHERE entity_type = '" + etype.name() + "' AND x = " + Integer.toString(spawner_loc.getBlockX()) + " AND y = " + Integer.toString(spawner_loc.getBlockY()) + " AND z = " + Integer.toString(spawner_loc.getBlockZ()) + "; ";
            psRem = conn.prepareStatement(sql);
            psRem.executeUpdate();
            
        	sql = "INSERT INTO broken_spawner (broken_spawner_id, entity_type, broken_by_uuid, world, x, y, z, broken_dtime) VALUES (" +
        			"NULL, " + // Inserting null as it is an auto-incrementing value
        			"'" + etype.name() + "', " +
        			"'" + uuid + "', " +
        			"'" + spawner_loc.getWorld().getName() + "', " + 
        			Integer.toString(spawner_loc.getBlockX()) + ", " +
        			Integer.toString(spawner_loc.getBlockY()) + ", " +
        			Integer.toString(spawner_loc.getBlockZ()) + ", " +
        			"'" + new Timestamp(System.currentTimeMillis()) + "'" +
        			");";

            psDb = conn.prepareStatement(sql);
            psDb.executeUpdate();

        	psGetID = conn.prepareStatement("select MAX(placed_spawner_id) AS seq from placed_spawner;");  
            rsGetID = psGetID.executeQuery();

            while(rsGetID.next()){
            	broken_spawner_id =  Integer.parseInt(rsGetID.getString("seq"));
            }  

    	} catch (Exception ex) {
    		Alert.VerboseLog("Database.saveBrokenSpawner", "Unexpected error saving broken_spawner");
    		Alert.DebugLog("Database", "saveBrokenSpawner", "Unexpected error saving broken_spawner: " + ex.getMessage() + " - SQL: " + sql);
        } finally {
            try {
                if (psRem != null)
                    psRem.close();
                if (psDb != null)
                    psDb.close();
                if (psGetID != null)
                    psGetID.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, SQLConnectionClose, ex);
            }
        }
        return broken_spawner_id;
    }     
   
}