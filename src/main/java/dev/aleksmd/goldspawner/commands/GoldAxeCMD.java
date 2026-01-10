package dev.aleksmd.goldspawner.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GoldAxeCMD implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;

    public GoldAxeCMD(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("goldaxepick") && args.length >= 1 && sender.hasPermission("goldaxepick.admin")) {
            if (args.length >= 3 && args[0].equalsIgnoreCase("give")) {
                String playerName = args[1];
                String itemType = args[2];
                Player player = plugin.getServer().getPlayer(playerName); // Use the plugin instance to access getServer()
                if (player != null) {
                    ItemStack itemStack = null;
                    if (itemType.equalsIgnoreCase("pickaxe")) {
                        String materialName = plugin.getConfig().getString("item.material");  // Use the plugin instance to access getConfig()
                        Material material = Material.valueOf(materialName);
                        itemStack = new ItemStack(material);
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setDisplayName(plugin.getConfig().getString("item.name")); // Use the plugin instance to access getConfig()
                        boolean shouldGlow = plugin.getConfig().getBoolean("item.glow"); // Use the plugin instance to access getConfig()
                        if (shouldGlow) {
                            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            itemMeta.addEnchant(org.bukkit.enchantments.Enchantment.OXYGEN, 1, true);
                        }
                        NamespacedKey key = new NamespacedKey(plugin, "goldaxe");  // NamespacedKey requires the plugin instance
                        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "1");
                        itemMeta.setLore(plugin.getConfig().getStringList("item.lore")); // Use the plugin instance to access getConfig()
                        itemStack.setItemMeta(itemMeta);
                    }
                    if (itemStack != null) {
                        player.getInventory().addItem(itemStack);
                        return true;
                    }
                    sender.sendMessage("Предмет не найден.");
                } else {
                    sender.sendMessage("Игрок оффлайн!");
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("goldaxepick.admin")) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            return Arrays.asList("give").stream()
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return Arrays.asList("pickaxe").stream()
                    .filter(type -> type.startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
