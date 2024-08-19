package dev.aleksmd.goldspawner.commands;

import dev.aleksmd.goldspawner.items.GoldenCompass;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GoldenCompassCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack compass = GoldenCompass.createCompass();

            player.getInventory().addItem(compass);
            player.sendMessage("Вы получили Золотой Компас!");

            return true;
        }
        sender.sendMessage("Эту команду может выполнить только игрок.");
        return false;
    }
}
