package net.hynse.scaleshifter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ScaleCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("scaleplayer")) {
            if (!sender.hasPermission("scaleshifter.scaleplayer")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                return true;
            }

            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /scaleplayer <playername> <value>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }

            double scale;
            try {
                scale = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid value! Please enter a number.");
                return true;
            }

            Scaleshifter.scaleUtil.setPlayerScale(target, scale); // Apply transition when changing scale through command
            sender.sendMessage(ChatColor.GREEN + "Player scale set successfully!");

            return true;
        } else if (command.getName().equalsIgnoreCase("scale")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                return true;
            }

            if (!sender.hasPermission("scaleshifter.scale")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /scale <value>");
                return true;
            }

            Player player = (Player) sender;

            double scale;
            try {
                scale = Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid value! Please enter a number.");
                return true;
            }

            Scaleshifter.scaleUtil.setPlayerScale(player, scale); // Apply transition when changing own scale through command
            sender.sendMessage(ChatColor.GREEN + "Your scale set successfully!");

            return true;
        }
        return false;
    }
}
