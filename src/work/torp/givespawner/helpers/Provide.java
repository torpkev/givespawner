package work.torp.givespawner.helpers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import work.torp.givespawner.alerts.Alert;

public class Provide {
	public static ItemStack getSpawner(Player player, EntityType entitytype, int spawnercount, boolean displayOwner, boolean displayDate)
	{
		ItemStack istack = new ItemStack(Material.SPAWNER, spawnercount);
		if (istack != null)
		{
			ItemMeta imeta = istack.getItemMeta();
			if (imeta != null)
			{
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd"); // Create a DateTimeFormatter
				LocalDate localDate = LocalDate.now(); // Get the current date
				List<String> lore = new ArrayList<String>(); // Create a new list for our Lore (this will tag the items as donation items)
				if (entitytype != null) // If our entitytype is not null, then this is a mob spawner
				{
					String loreString = "Type: " + entitytype.name().replace("_", " ").substring(0, 1).toUpperCase() + entitytype.name().replace("_", " ").substring(1).toLowerCase(); // Create a new string to hold our EntityType name
					lore.add(loreString); // Add our string to the Lore list
				}
				if (displayOwner)
				{
					lore.add("Owner: " + player.getDisplayName()); // Add the owner of the spawner to the Lore list
				}
				if (displayDate)
				{
					lore.add("Given: " + dtf.format(localDate)); // Add the current date (formatted yyyy/MM/dd) as the date given to the Lore list
				}
				lore.add(ChatColor.BLACK + "ID: " + Integer.toString((player.getUniqueId().toString() + "-" + entitytype.name()).hashCode()));
				imeta.setLore(lore);
				imeta.setDisplayName(entitytype.name().replace("_", " ").substring(0, 1).toUpperCase() + entitytype.name().replace("_", " ").substring(1).toLowerCase() + " Spawner");
				istack.setItemMeta(imeta);	
				Alert.DebugLog("Provide", "getSpawner", "Created " + entitytype.name() + " spawner + ItemStack for " + player.getDisplayName());
			}
		}
		return istack;
	}
	public static EntityType getEntityTypeFromSpawner(Player player, ItemStack istack)
	{
		EntityType entitytype = EntityType.PIG;
		
		List<String> lore = new ArrayList<String>();
		if (istack.getType() == Material.SPAWNER)
		{
			Alert.DebugLog("Provide", "getEntityTypeFromSpawner", "Itemstack is a spawner");
			if(istack.getItemMeta().getLore() != null) {
				Alert.DebugLog("Provide", "getEntityTypeFromSpawner", "Lore is not null");
				lore = istack.getItemMeta().getLore();
				for (String s : lore)
				{
					s = ChatColor.stripColor(s);
					if (s.length() > 1)
					{
						if (s.substring(0,  4).equals("ID: "))
						{
							String hash = s.replace("ID: ",  "");
							for (EntityType et : EntityType.values())
							{
								String playerhash = Integer.toString((player.getUniqueId().toString() + "-" + et.name()).hashCode());
								if (playerhash.equals(hash))
								{
									entitytype = et;
								}
							}
						} else {
							Alert.DebugLog("Provide", "getEntityTypeFromSpawner", "ID not found");
						}
					}
				}
			} else {
				Alert.DebugLog("Provide", "getEntityTypeFromSpawner", "Lore is null");
			}
		} else {
			Alert.VerboseLog("getEntityTypeFromSpawner", "Itemstack is not a spawner");
			return null;
		}

		return entitytype;
	}
	public static Material getEggFromEntityType(EntityType entitytype)
	{
		Material m = Material.AIR;
		switch (entitytype)
		{
			case BAT : m = Material.BAT_SPAWN_EGG; break;
			case BLAZE : m = Material.BLAZE_SPAWN_EGG; break;
			case CAVE_SPIDER: m = Material.CAVE_SPIDER_SPAWN_EGG; break;
			case CHICKEN: m = Material.CHICKEN_SPAWN_EGG; break;
			case COD: m = Material.COD_SPAWN_EGG; break;
			case COW: m = Material.COW_SPAWN_EGG; break;
			case CREEPER: m = Material.CREEPER_SPAWN_EGG; break;
			case DOLPHIN: m = Material.DOLPHIN_SPAWN_EGG; break;
			case DONKEY: m = Material.DONKEY_SPAWN_EGG; break;
			case DROWNED: m = Material.DROWNED_SPAWN_EGG; break;
			case ELDER_GUARDIAN: m = Material.ELDER_GUARDIAN_SPAWN_EGG; break;
			case ENDERMAN: m = Material.ENDERMAN_SPAWN_EGG; break;
			case ENDERMITE: m = Material.ENDERMITE_SPAWN_EGG; break;
			case EVOKER: m = Material.EVOKER_SPAWN_EGG; break;
			case GUARDIAN: m = Material.GUARDIAN_SPAWN_EGG; break;
			case HORSE: m = Material.HORSE_SPAWN_EGG; break;
			case HUSK: m = Material.HUSK_SPAWN_EGG; break;
			case IRON_GOLEM: m = Material.IRON_INGOT; break;
			case LLAMA: m = Material.LLAMA_SPAWN_EGG; break;
			case MAGMA_CUBE: m = Material.MAGMA_CUBE_SPAWN_EGG; break;
			case MULE: m = Material.MULE_SPAWN_EGG; break;
			case MUSHROOM_COW: m = Material.MOOSHROOM_SPAWN_EGG; break;
			case OCELOT: m = Material.OCELOT_SPAWN_EGG; break;
			case PARROT: m = Material.PARROT_SPAWN_EGG; break;
			case PHANTOM: m = Material.PHANTOM_SPAWN_EGG; break;
			case PIG: m = Material.PIG_SPAWN_EGG; break;
			case PIG_ZOMBIE: m = Material.ZOMBIE_PIGMAN_SPAWN_EGG; break;
			case POLAR_BEAR: m = Material.POLAR_BEAR_SPAWN_EGG; break;
			case PUFFERFISH: m = Material.PUFFERFISH_SPAWN_EGG; break;
			case RABBIT: m = Material.RABBIT_SPAWN_EGG; break;
			case SALMON: m = Material.SALMON_SPAWN_EGG; break;
			case SHEEP: m = Material.SHEEP_SPAWN_EGG; break;
			case SILVERFISH: m = Material.SILVERFISH_SPAWN_EGG; break;
			case SKELETON: m = Material.SKELETON_SPAWN_EGG; break;
			case SLIME: m = Material.SLIME_SPAWN_EGG; break;
			case SNOWMAN: m = Material.SNOWBALL; break;
			case SPIDER: m = Material.SPIDER_SPAWN_EGG; break;
			case SQUID: m = Material.SQUID_SPAWN_EGG; break;
			case STRAY: m = Material.STRAY_SPAWN_EGG; break;
			case TROPICAL_FISH: m = Material.TROPICAL_FISH_SPAWN_EGG; break;
			case TURTLE: m = Material.TURTLE_SPAWN_EGG; break;
			case VEX: m = Material.VEX_SPAWN_EGG; break;
			case VILLAGER: m = Material.VILLAGER_SPAWN_EGG; break;
			case VINDICATOR: m = Material.VINDICATOR_SPAWN_EGG; break;
			case WITCH: m = Material.WITCH_SPAWN_EGG; break;
			case WITHER_SKELETON: m = Material.WITHER_SKELETON_SPAWN_EGG; break;
			case WOLF: m = Material.WOLF_SPAWN_EGG; break;
			case ZOMBIE: m = Material.ZOMBIE_SPAWN_EGG; break;
			default: 
				break;
		}
		
		return m;
	}
	public static List<EntityType> getEntityTypes()
	{
    	List<EntityType> lstEntity = new ArrayList<EntityType>();
    	lstEntity.add(EntityType.BAT);
    	lstEntity.add(EntityType.BLAZE);
    	lstEntity.add(EntityType.CAVE_SPIDER);
    	lstEntity.add(EntityType.CHICKEN);
    	lstEntity.add(EntityType.COD);
    	lstEntity.add(EntityType.COW);
    	lstEntity.add(EntityType.CREEPER);
    	lstEntity.add(EntityType.DOLPHIN);
    	lstEntity.add(EntityType.DONKEY);
    	lstEntity.add(EntityType.DROWNED);
    	lstEntity.add(EntityType.ELDER_GUARDIAN);
    	lstEntity.add(EntityType.ENDERMAN);
    	lstEntity.add(EntityType.ENDERMITE);
    	lstEntity.add(EntityType.EVOKER);
    	lstEntity.add(EntityType.GHAST);
    	lstEntity.add(EntityType.GUARDIAN);
    	lstEntity.add(EntityType.HORSE);
    	lstEntity.add(EntityType.HUSK);
    	lstEntity.add(EntityType.IRON_GOLEM);
    	lstEntity.add(EntityType.LLAMA);
    	lstEntity.add(EntityType.MAGMA_CUBE);
    	lstEntity.add(EntityType.MULE);
    	lstEntity.add(EntityType.MUSHROOM_COW);
    	lstEntity.add(EntityType.OCELOT);
    	lstEntity.add(EntityType.PARROT);
    	lstEntity.add(EntityType.PHANTOM);
    	lstEntity.add(EntityType.PIG);
    	lstEntity.add(EntityType.PIG_ZOMBIE);
    	lstEntity.add(EntityType.POLAR_BEAR);
    	lstEntity.add(EntityType.PUFFERFISH);
    	lstEntity.add(EntityType.RABBIT);
    	lstEntity.add(EntityType.SALMON);
    	lstEntity.add(EntityType.SHEEP);
    	lstEntity.add(EntityType.SILVERFISH);
    	lstEntity.add(EntityType.SKELETON);
    	lstEntity.add(EntityType.SLIME);
    	lstEntity.add(EntityType.SNOWMAN);
    	lstEntity.add(EntityType.SPIDER);
    	lstEntity.add(EntityType.SQUID);
    	lstEntity.add(EntityType.STRAY);
    	lstEntity.add(EntityType.TROPICAL_FISH);
    	lstEntity.add(EntityType.TURTLE);
    	lstEntity.add(EntityType.VEX);
    	lstEntity.add(EntityType.VILLAGER);
    	lstEntity.add(EntityType.VINDICATOR);
    	lstEntity.add(EntityType.WITCH);
    	lstEntity.add(EntityType.WITHER_SKELETON);
    	lstEntity.add(EntityType.WOLF);
    	lstEntity.add(EntityType.ZOMBIE);
    	
    	return lstEntity;
	}
}
