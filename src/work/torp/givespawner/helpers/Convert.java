package work.torp.givespawner.helpers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import work.torp.givespawner.alerts.Alert;

public class Convert {
	public static Material StringToMaterial(String material_name)
	{
		Material mat;
		
		if (material_name == null)
		{
			material_name = "<null>";
		}
		try 
		{
			mat = Material.getMaterial(material_name);
		}
		catch (Exception ex)
		{
			Alert.VerboseLog("Convert.StringToMaterial", "Unable to get Material from name: " + material_name + " - " + ex.getMessage());
			mat = Material.AIR;
		}
		return mat;
	}
	public static EntityType StringToEntityType(String entity_type)
	{
		EntityType et;
		
		if (entity_type == null)
		{
			entity_type = "<null>";
		}
		try 
		{
			et = EntityType.valueOf(entity_type.toUpperCase());
		}
		catch (Exception ex)
		{
			Alert.VerboseLog("Convert.StringToEntityType", "Unable to get Entity Type from name: " + entity_type + " - " + ex.getMessage());
			et =  null;
		}
		return et;
	}
	public static String LocationToReadableString(Location loc)
	{
		String ret = "";	
		ret = "X: " + Integer.toString(loc.getBlockX()) + " Y: " + Integer.toString(loc.getBlockY()) + " Z: " + Integer.toString(loc.getBlockZ());
		return ret;
	}
	public static Location LocationFromXYZ(String world, int x, int y, int z) {
		Location loc = null;
		
		Location loc1 = new Location(Bukkit.getWorld(world), x, y, z);
		if (loc1 != null)
		{
			loc = loc1;
		}
		
		return loc;
	}
	public static int IntegerFromString(String s)
	{
		int retVal = -1;
		try{
			retVal = Integer.parseInt(s);
		} 
		catch (NumberFormatException ex) {
			Alert.DebugLog("Convert", "IntegerFromString", "Unable to convert String to Integer - String: " + s);	
			retVal = -1;
		}
		return retVal;
	}
}
