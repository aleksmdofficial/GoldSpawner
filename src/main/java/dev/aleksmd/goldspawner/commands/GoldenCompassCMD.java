package dev.aleksmd.goldspawner.commands;

import dev.aleksmd.goldspawner.items.GoldenCompass;
import dev.aleksmd.goldspawner.manager.ConfigManager;
import dev.aleksmd.goldspawner.utils.HexUtils;
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
            
            // Проверка прав
            if (!player.hasPermission("goldencompass.use")) {
                String noPermMsg = ConfigManager.getMessagesConfig().getString("messages.admin.no-permission", "&cНет прав");
                player.sendMessage(HexUtils.translate(noPermMsg));
                return true;
            }
            
            ItemStack compass = GoldenCompass.createCompass();

            player.getInventory().addItem(compass);
            String receivedMsg = ConfigManager.getMessagesConfig().getString("messages.compass.received", "&aВы получили Золотой Компас!");
            player.sendMessage(HexUtils.translate(receivedMsg));

            return true;
        }
        String onlyPlayerMsg = ConfigManager.getMessagesConfig().getString("messages.compass.only-player", "&cЭту команду может выполнить только игрок.");
        sender.sendMessage(HexUtils.translate(onlyPlayerMsg));
        return false;
    }
}
