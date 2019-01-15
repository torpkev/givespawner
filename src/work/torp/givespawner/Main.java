package work.torp.givespawner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import work.torp.givespawner.alerts.Alert;
import work.torp.givespawner.classes.PlacedSpawner;

import work.torp.givespawner.commands.GiveSpawner;
import work.torp.givespawner.Main;
import work.torp.givespawner.database.Database;
import work.torp.givespawner.database.SQLite;
import work.torp.givespawner.events.BlockEvents;
import work.torp.givespawner.helpers.Convert;
import work.torp.givespawner.helpers.IconMenu;
import work.torp.givespawner.helpers.Provide;


public class Main extends JavaPlugin {
	
	// Hashmaps
	public static HashMap<UUID, UUID> CommandUUID = new HashMap<UUID, UUID>();
	
	// Main
	private static Main instance;
    public static Main getInstance() {
		return instance;
	}
    
    // Database
	private Database db;
    public Database getDatabase() {
        return this.db;
    }
    
    // DebugFile
	private boolean debugfile;
    public boolean getDebugFile() {
        return this.debugfile;
    }
    public void setDebugFile(boolean debugfile) {
    	this.debugfile = debugfile;
    }

    // Lists
    private List<PlacedSpawner> placedSpawner;
    public List<PlacedSpawner> GetPlacedSpawner() {
    	return placedSpawner;
    }
    public void AddPlacedSpawner(PlacedSpawner ps)
    {
    	try {
	    	if (placedSpawner == null) {
	    		placedSpawner = new ArrayList<PlacedSpawner>();
	    	}
	    	if (ps != null)
	    	{
	    		Alert.DebugLog("Main", "AddPlacedSpawner", "Adding " + ps.getEntityType().name() + " Spawner to " + Convert.LocationToReadableString(ps.getPlacedLocation()));
	    		placedSpawner.add(ps);
	    	}
    	} catch (Exception ex) {
			Alert.DebugLog("Main", "AddPlacedSpawner", "Unexpected Error - " + ex.getMessage());  
		}
    } 
    public void RemovePlacedSpawner(PlacedSpawner ps)
    {
    	try {
	    	List<PlacedSpawner> lstPS = new ArrayList<PlacedSpawner>();
	    	if (ps != null)
	    	{
	    		if (placedSpawner != null) {
	    			for (PlacedSpawner i : placedSpawner)
		    		{
	    				
	    				if (i.getPlacedSpawnerID() != ps.getPlacedSpawnerID())
						{
	    					lstPS.add(ps);
						} else {
							Alert.DebugLog("Main", "RemovePlacedSpawner", "Removing " + ps.getEntityType().name() + " Spawner from " + Convert.LocationToReadableString(ps.getPlacedLocation()));
						}
		    		}
	    		}
	    	}
	    	placedSpawner = lstPS;
    	} catch (Exception ex) {
			Alert.DebugLog("Main", "RemovePlacedSpawner", "Unexpected Error - " + ex.getMessage());  
		}
    }

    // Config
    private boolean console_only = false;
    public boolean getConsoleOnly() {
    	return this.console_only;
    }
    
    // Configuration
    public void loadConfig() {
    	try {
    		
    		String s_console_only = Main.getInstance().getConfig().getString("console_only");
    		console_only = false; // default to false
	    	if (s_console_only != null) {
	    		if (s_console_only.equalsIgnoreCase("true")) {
	    			console_only = true;
				} else if (s_console_only.equalsIgnoreCase("true")) {
					console_only = false;
				} else {
					Alert.Log("Main.loadConfig", "console_only value is invalid, using default of false");
				}
	    	} else {
	    		Alert.Log("Main.loadConfig", "console_only value not found, using default of false");
	    	}	
    	}
    	catch (Exception ex) {
    		Alert.Log("Load Configuration", "Unexpected Error - " + ex.getMessage());  	
    	}
    }
    
    public void loadEventListeners() {
		Alert.VerboseLog("Main", "Starting Event Listeners");	
		try {	
			Bukkit.getPluginManager().registerEvents(new BlockEvents(), this);
		} catch (Exception ex) {
			Alert.Log("Load Event Listeners", "Unexpected Error - " + ex.getMessage());  
		}
    }
    
    // Commands
    public void loadCommands() {
    	Alert.DebugLog("Main", "loadCommands", "Activating /givespawner");
		try {
		    	getCommand("givespawner").setExecutor(new GiveSpawner());
		} catch (Exception ex) {
			Alert.Log("Load Commands", "Unexpected Error - " + ex.getMessage());  
		}
    }

    // GUI
    public IconMenu egglist;
    public void setupGUI() {
    	
    	egglist = new IconMenu("Spawner Type", 54, new IconMenu.OptionClickEventHandler() {
	        @Override
	        public void onOptionClick(IconMenu.OptionClickEvent event) {
	    		Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
	    		Player givePlayer = Bukkit.getPlayer(event.getPlayer().getUniqueId());
	    		if (CommandUUID.containsKey(player.getUniqueId()))
	    		{
	    			if (Bukkit.getPlayer(CommandUUID.get(player.getUniqueId())) != null)
	    			{
	    				givePlayer = Bukkit.getPlayer(CommandUUID.get(player.getUniqueId()));
	    			}
	    		}
	    		if (givePlayer != null)
	    		{
	    			CommandUUID.remove(player.getUniqueId());
		    		EntityType etype = EntityType.valueOf(event.getName());
		    		if (etype != null)
		    		{
		    			ItemStack istack = Provide.getSpawner(givePlayer, etype, 1, true, true);
		    			if (givePlayer.getInventory().firstEmpty() >= 0)
		    			{
		    				givePlayer.getInventory().addItem(istack);
		    				if (player.getUniqueId().equals(givePlayer.getUniqueId()))
		    				{
		    					Alert.Player("You have been given a " + etype.name() + " spawner", givePlayer, true);
		    				} else {
		    					Alert.Player("You have given a " + etype.name() + " spawner to " + givePlayer.getDisplayName(), player, true);
		    					Alert.Player("You have been given a " + etype.name() + " spawner by " + player.getDisplayName(), givePlayer, true);
		    				}
		    			} else {
		    				if (player.getUniqueId().equals(givePlayer.getUniqueId()))
		    				{
		    					Alert.Player("You do not have an open inventory slot to get the spawner", givePlayer, true);
		    				} else {
		    					Alert.Player(givePlayer.getDisplayName() + " does not have an open inventory slot to get the spawner", player, true);
		    				}
		    			}
		    		}
	    		}
	            event.setWillClose(true);	  
	        }
	    }, this);
    	
    	
    	int i = 0;
    	List<EntityType> lstEntity = Provide.getEntityTypes();
    	if (lstEntity != null)
    	{
    		for (EntityType et : lstEntity)
    		{
    			if (i < 54)
    			{
    				Alert.DebugLog("Main", "setupGUI", "Adding " + et.name());
	        		ItemStack istack = new ItemStack(Provide.getEggFromEntityType(et), 1);
	        		if (istack != null)
	        		{
		        		ItemMeta imeta = istack.getItemMeta();
		        		if (imeta != null)
		        		{
		        			imeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		        			istack.setItemMeta(imeta);
		        			egglist.setOption(i, istack, et.name(), "Give 1x " + et.name() + " spawner");
		        			i++;
		        		}
	        		}
    			}
    			
    		}
    	}
	}
    
    // Load from Database
    public void loadFromDatabase() {
    	Alert.Log("Main.loadFromDatabase", "Getting Placed Spawners");
    	this.db.getPlacedSpawner();
    	if (placedSpawner != null)
    	{
    		Alert.DebugLog("Main", "loadFromDatabase", "# Placed Spawners: " + Integer.toString(placedSpawner.size()));
    	} else {
    		Alert.DebugLog("Main", "loadFromDatabase", "Placed Spawner list is null");
    	}
    }
    
    // Enable
    @Override
	public void onEnable() {
    	
    	try {
			instance = this;

			saveDefaultConfig();
			Alert.Log("Main", "Starting Give Spawner");
			this.db = new SQLite(this); // New SQLite
	        this.db.load(); // Run load
	        this.db.initialize(); // Run initialize
	        loadConfig();
	        loadEventListeners();
	        loadCommands();
			loadFromDatabase();
			
			setupGUI();

    	} catch (Exception ex) {
			Alert.DebugLog("Main", "onEnable", "Unexpected Error - " + ex.getMessage());  
		}
	}

    // Disable
    @Override
    public void onDisable() {
    	
	}
}
