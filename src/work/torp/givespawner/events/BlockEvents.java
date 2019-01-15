package work.torp.givespawner.events;

import java.sql.Timestamp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import work.torp.givespawner.Main;
import work.torp.givespawner.alerts.Alert;
import work.torp.givespawner.classes.PlacedSpawner;
import work.torp.givespawner.helpers.Check;
import work.torp.givespawner.helpers.Convert;
import work.torp.givespawner.helpers.Provide;

public class BlockEvents implements Listener {
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent evt) {
		if (evt.getItemInHand().getType().equals(Material.SPAWNER)) {
			EntityType etype = Provide.getEntityTypeFromSpawner(evt.getPlayer(), evt.getItemInHand());
			if (etype != null)
			{
				Block setBlock = evt.getBlock();
				CreatureSpawner s = (CreatureSpawner)setBlock.getState();
				s.setSpawnedType(etype);
				s.update();
				int i = Main.getInstance().getDatabase().savePlacedSpawner(etype, evt.getPlayer().getUniqueId().toString(), evt.getBlock().getLocation());
				if (i > 0)
				{
					PlacedSpawner ps = new PlacedSpawner();
					ps.setPlacedSpawnerID(i);
					ps.setEntityType(etype);
					ps.setPlacedByUUID(evt.getPlayer().getUniqueId().toString());
					ps.setPlacedDateTime(new Timestamp(System.currentTimeMillis()));
					ps.setPlacedLocation(evt.getBlock().getLocation());
					Main.getInstance().AddPlacedSpawner(ps);
					Alert.Player("You have placed your " + ps.getEntityType().name() + " Spawner", evt.getPlayer(), true);
				}
			} else {
				evt.setCancelled(true);
				Alert.Player("Invalid Spawner", evt.getPlayer(), true);
			}
		}
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent evt) {
		if (evt.getBlock().getType().equals(Material.SPAWNER))
		{
			if (Check.hasPermission(evt.getPlayer(), "givespawner.break"))
			{
				if (evt.getPlayer().getInventory().firstEmpty() >= 0)
    			{
					if (Main.getInstance().GetPlacedSpawner() != null)
					{
						EntityType etype = EntityType.PIG;
						Location blockloc = evt.getBlock().getLocation();
						
						for (PlacedSpawner ps : Main.getInstance().GetPlacedSpawner())
						{
							// Create a new location from the base values of the ps location to remove pitch/yaw etc.
							Location psloc = new Location(ps.getPlacedLocation().getWorld(), ps.getPlacedLocation().getBlockX(), ps.getPlacedLocation().getBlockY(), ps.getPlacedLocation().getBlockZ());
							if (Convert.LocationToReadableString(blockloc).equals(Convert.LocationToReadableString(psloc))) {
								etype = ps.getEntityType();
								
								BlockState bs = evt.getBlock().getState();
								bs.setType(Material.AIR);
								bs.update(true);
								
								ItemStack istack = Provide.getSpawner(evt.getPlayer(), etype, 1, true, true);
								evt.getPlayer().getInventory().addItem(istack);   
								evt.getPlayer().updateInventory();
								Main.getInstance().getDatabase().saveBrokenSpawner(etype, evt.getPlayer().getUniqueId().toString(), evt.getBlock().getLocation());
								Alert.Player("Your " + etype.name().replace("_", " ").substring(0, 1).toUpperCase() + etype.name().replace("_", " ").substring(1).toLowerCase() + " spawner is in your inventory", evt.getPlayer(), true);
								Main.getInstance().RemovePlacedSpawner(ps);
								break;
							}
						}

					} else {
						Alert.Player("This spawner hasn't been placed with GiveSpawner - Unable to break. Please contact a staff member for assistance.", evt.getPlayer(), true);
						evt.setCancelled(true);
					}
    			} else {
    				Alert.Player("You must have a free inventory slot", evt.getPlayer(), true);
    				evt.setCancelled(true);
    				Alert.Player("You cannot break this spawner", evt.getPlayer(), true);
    			}
			} else {
				evt.setCancelled(true);
				Alert.Player("You cannot break this spawner", evt.getPlayer(), true);
			}
		}
	}
	@EventHandler
	public void onBlockDamage(BlockDamageEvent evt) {
		if (evt.getBlock().getType().equals(Material.SPAWNER))
		{
			if (Check.hasPermission(evt.getPlayer(), "givespawner.break"))
			{
				if (evt.getPlayer().getInventory().firstEmpty() >= 0)
    			{
					if (Main.getInstance().GetPlacedSpawner() != null)
					{
						EntityType etype = EntityType.PIG;
						Location blockloc = evt.getBlock().getLocation();
						
						for (PlacedSpawner ps : Main.getInstance().GetPlacedSpawner())
						{
							// Create a new location from the base values of the ps location to remove pitch/yaw etc.
							Location psloc = new Location(ps.getPlacedLocation().getWorld(), ps.getPlacedLocation().getBlockX(), ps.getPlacedLocation().getBlockY(), ps.getPlacedLocation().getBlockZ());
							if (Convert.LocationToReadableString(blockloc).equals(Convert.LocationToReadableString(psloc))) {
								etype = ps.getEntityType();
								
								BlockState bs = evt.getBlock().getState();
								bs.setType(Material.AIR);
								bs.update(true);
								
								ItemStack istack = Provide.getSpawner(evt.getPlayer(), etype, 1, true, true);
								evt.getPlayer().getInventory().addItem(istack);   
								evt.getPlayer().updateInventory();
								Main.getInstance().getDatabase().saveBrokenSpawner(etype, evt.getPlayer().getUniqueId().toString(), evt.getBlock().getLocation());
								Alert.Player("Your " + etype.name().replace("_", " ").substring(0, 1).toUpperCase() + etype.name().replace("_", " ").substring(1).toLowerCase() + " spawner is in your inventory", evt.getPlayer(), true);
								Main.getInstance().RemovePlacedSpawner(ps);
								break;
							}
						}
					} else {
						Alert.Player("This spawner hasn't been placed with GiveSpawner - Unable to break. Please contact a staff member for assistance.", evt.getPlayer(), true);
					}
    			} else {
    				Alert.Player("You must have a free inventory slot", evt.getPlayer(), true);
    				evt.setCancelled(true);
    				Alert.Player("You cannot break this spawner", evt.getPlayer(), true);
    			}
			} else {
				evt.setCancelled(true);
				Alert.Player("You cannot break this spawner", evt.getPlayer(), true);
			}
		}
	}
}
