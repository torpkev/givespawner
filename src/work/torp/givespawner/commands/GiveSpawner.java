package work.torp.givespawner.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import work.torp.givespawner.Main;
import work.torp.givespawner.alerts.Alert;
import work.torp.givespawner.helpers.Check;

public class GiveSpawner implements CommandExecutor {
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Alert.DebugLog("GiveSpawner", "onCommand", "/givespawner command run");
		if (sender instanceof Player) {
			Alert.DebugLog("GiveSpawner", "onCommand", "Player sent");
		} else {
			// Console sent
			Alert.DebugLog("GiveSpawner", "onCommand", "Unable to run command from Console");
			return true;
		}

		StringBuilder fullargs = new StringBuilder();
		int iargs = 0;
		if (args.length > 0) {
			for (Object o : args)
			{
				iargs++;
				fullargs.append(o.toString());
				fullargs.append(" ");
			}
			Alert.DebugLog("GiveSpawner", "onCommand", "/givespawner command run by " + sender.getName() + " with arguments: " + fullargs);
		}
		if (sender instanceof Player)
		{
			
			Player senderPlayer = (Player) sender;
			Player player = (Player) sender;
			if (Check.hasPermission(player, "givespawner.give"))
			{
				if (iargs >= 1)
				{
					if (args[0] != null)
					{
						player = Bukkit.getPlayer(args[0].toString());
						if (player == null)
						{
							Alert.Sender("Unable to run /givespawner <player>, " + args[0].toString() + " is not a valid player", sender, true);
							return true;
						}
					}
				}
				Main.CommandUUID.put(senderPlayer.getUniqueId(), player.getUniqueId());
				Main.getInstance().egglist.open((Player) sender);
			} else {
				Alert.Player("You do not have permission to use /givespawner", player, true);
			}
		} else 
		{
			Alert.Sender("Unable to run /givespawner from Console", sender, true);
			return true;
		}
		return true;
	}

}
