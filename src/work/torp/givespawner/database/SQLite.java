package work.torp.givespawner.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import work.torp.givespawner.Main;

public class SQLite extends Database{
    String dbname;
    public SQLite(Main instance){
        super(instance);
        dbname = plugin.getConfig().getString("SQLite.Filename", "Spawners"); 
    }

    public String broken_spawner = "CREATE TABLE IF NOT EXISTS broken_spawner (" + 
    		"`broken_spawner_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
    		"`entity_type` VARCHAR(100) NOT NULL, " +
    		"`broken_by_uuid` varchar(50) NOT NULL, " +
			"`world` varchar (100) NOT NULL, " +
			"`x` INTEGER NOT NULL, " +
			"`y` INTEGER NOT NULL, " +
			"`z` INTEGER NOT NULL, " +
			"`broken_dtime`	timestamp NOT NULL " +
			");";
    
    public String placed_spawner = "CREATE TABLE IF NOT EXISTS placed_spawner (" + 
    		"`placed_spawner_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
    		"`entity_type` VARCHAR(100) NOT NULL, " +
    		"`placed_by_uuid` varchar(50) NOT NULL, " +
    		"`world` varchar(100) NOT NULL, " +
    		"`x` INTEGER NOT NULL, " +
    		"`y` INTEGER NOT NULL, " +
    		"`z` INTEGER NOT NULL, " +
    		"`placed_dtime`	timestamp NOT NULL, " +
    		"`is_removed` boolean NOT NULL " +
    		");";
    

    // SQL creation stuff, You can leave the blow stuff untouched.
    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(broken_spawner);
            s.executeUpdate(placed_spawner);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}
 