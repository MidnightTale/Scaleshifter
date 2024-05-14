package net.hynse.scaleshifter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GeneralCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("scaleguiplayer")) {
            if (!sender.hasPermission("scaleshifter.scaleguiplayer")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /scaleguiplayer <playername>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
            Scaleshifter.gui.openGUI(target.getPlayer());
            return true;
        } else if (command.getName().equalsIgnoreCase("scalegui")) {
            if (!sender.hasPermission("scaleshifter.scalegui")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                return true;
            }
            Scaleshifter.gui.openGUI((Player) sender);
        }
        return false;
    }
}
