package dev.aleksmd.goldspawner.commands;

import dev.aleksmd.goldspawner.Main;
import dev.aleksmd.goldspawner.manager.ConfigManager;
import dev.aleksmd.goldspawner.utils.HexUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GoldSpawnerCMD implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            for (String line : ConfigManager.getMessagesConfig().getStringList("messages.command.usage")) {
                sender.sendMessage(HexUtils.translate(line));
            }
            return false;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("holygoldspawner.give")) {
                for (String line : ConfigManager.getMessagesConfig().getStringList("messages.admin.no-permission")) {
                    sender.sendMessage(HexUtils.translate(line));
                }
                return true;
            }

            if (args.length < 4) {
                for (String line : ConfigManager.getMessagesConfig().getStringList("messages.command.usage")) {
                    sender.sendMessage(HexUtils.translate(line));
                }
                return false;
            }

            String itemType = args[1].toUpperCase();
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                for (String line : ConfigManager.getMessagesConfig().getStringList("messages.admin.not-found")) {
                    sender.sendMessage(HexUtils.translate(line.replace("%player%", args[2])));
                }
                return false;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                for (String line : ConfigManager.getMessagesConfig().getStringList("messages.admin.invalid-amount")) {
                    sender.sendMessage(HexUtils.translate(line));
                }
                return false;
            }

            ItemStack item;
            String itemName;

            if (itemType.equals("SPAWNER")) {
                item = new ItemStack(Material.SPAWNER, amount);
                ItemMeta meta = item.getItemMeta();
                itemName = ConfigManager.getItemsConfig().getString("spawner.name");
                meta.setDisplayName(HexUtils.translate(itemName));
                meta.setLore(ConfigManager.getItemsConfig().getStringList("spawner.lore").stream()
                        .map(HexUtils::translate)
                        .collect(Collectors.toList()));
                item.setItemMeta(meta);
            } else {
                for (String line : ConfigManager.getMessagesConfig().getStringList("messages.command.usage")) {
                    sender.sendMessage(HexUtils.translate(line));
                }
                return false;
            }

            target.getInventory().addItem(item);

            for (String line : ConfigManager.getMessagesConfig().getStringList("messages.admin.give-items")) {
                sender.sendMessage(HexUtils.translate(line.replace("%items%", itemName).replace("%player%", target.getName())));
            }

            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("holygoldspawner.reload")) {
                for (String line : ConfigManager.getMessagesConfig().getStringList("messages.admin.no-permission")) {
                    sender.sendMessage(HexUtils.translate(line));
                }
                return true;
            }

            ConfigManager.reloadConfigs(Main.getInstance());
            for (String line : ConfigManager.getMessagesConfig().getStringList("messages.admin.reload")) {
                sender.sendMessage(HexUtils.translate(line));
            }
            return true;
        }

        for (String line : ConfigManager.getMessagesConfig().getStringList("messages.command.usage")) {
            sender.sendMessage(HexUtils.translate(line));
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("give", "reload").stream()
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .filter(cmd -> (cmd.equals("give") && sender.hasPermission("holygoldspawner.give")) ||
                            (cmd.equals("reload") && sender.hasPermission("holygoldspawner.reload")))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("give") && sender.hasPermission("holygoldspawner.give")) {
            return Arrays.asList("SPAWNER").stream()
                    .filter(type -> type.startsWith(args[1].toUpperCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("give") && sender.hasPermission("holygoldspawner.give")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("give") && sender.hasPermission("holygoldspawner.give")) {
            return Arrays.asList("1", "8", "16", "32", "64").stream()
                    .filter(amount -> amount.startsWith(args[3]))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
