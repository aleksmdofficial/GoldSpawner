package dev.aleksmd.goldspawner.items;

import dev.aleksmd.goldspawner.Main;
import dev.aleksmd.goldspawner.manager.ConfigManager;
import dev.aleksmd.goldspawner.utils.HexUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс GoldenCompass отвечает за создание и управление новым компасом.
 */
public class GoldenCompass {

    private static NamespacedKey compassKey;
    
    private static NamespacedKey getCompassKey() {
        if (compassKey == null) {
            compassKey = new NamespacedKey(Main.getInstance(), "golden_compass");
        }
        return compassKey;
    }

    /**
     * Создает новый уникальный компас.
     *
     * @return объект ItemStack, представляющий компас.
     */
    public static ItemStack createCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();

        if (meta != null) {
            FileConfiguration itemsConfig = ConfigManager.getItemsConfig();
            
            // Получаем название из конфигурации
            String name = itemsConfig.getString("compass.name", "&6Золотой Компас");
            meta.setDisplayName(HexUtils.translate(name));
            
            // Получаем описание из конфигурации
            List<String> lore = itemsConfig.getStringList("compass.lore").stream()
                    .map(HexUtils::translate)
                    .collect(Collectors.toList());
            meta.setLore(lore);
            
            // Проверяем, нужно ли свечение
            boolean glow = itemsConfig.getBoolean("compass.glow", true);
            if (glow) {
                meta.addEnchant(Enchantment.LUCK, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            // Установка уникального идентификатора компаса
            meta.getPersistentDataContainer().set(getCompassKey(), PersistentDataType.STRING, "true");

            compass.setItemMeta(meta);
        }

        return compass;
    }

    /**
     * Проверяет, является ли данный предмет уникальным золотым компасом.
     *
     * @param item предмет для проверки.
     * @return true, если это уникальный золотой компас, иначе false.
     */
    public static boolean isGoldenCompass(ItemStack item) {
        if (item == null || item.getType() != Material.COMPASS) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            return meta.getPersistentDataContainer().has(getCompassKey(), PersistentDataType.STRING);
        }
        return false;
    }
}
