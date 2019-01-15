package work.torp.givespawner.helpers;

import org.bukkit.entity.Player;

public class Check {
	public static boolean hasPermission(Player player, String permission)
	{
		if (player.isOp() || player.hasPermission(permission))
		{
			return true;
		} else {
			return false;
		}
	}
}
