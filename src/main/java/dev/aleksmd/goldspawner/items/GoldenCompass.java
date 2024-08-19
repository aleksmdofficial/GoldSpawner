package dev.aleksmd.goldspawner.items;

import dev.aleksmd.goldspawner.Main;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import java.util.List;

/**
 * Класс GoldenCompass отвечает за создание и управление новым компасом.
 */
public class GoldenCompass {

    private static final String COMPASS_NAME = "Золотой Компас";

    /**
     * Создает новый уникальный компас.
     *
     * @return объект ItemStack, представляющий компас.
     */
    public static ItemStack createCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(COMPASS_NAME);
            meta.addEnchant(Enchantment.LUCK, 1, true);  // Пример: зачарование на удачу
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setLore(List.of("Этот компас укажет путь к", "золотому спавнеру."));

            // Установка уникального идентификатора компаса
            NamespacedKey key = new NamespacedKey(Main.getInstance(), "golden_compass");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "true");

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
            NamespacedKey key = new NamespacedKey(Main.getInstance(), "golden_compass");
            return meta.getPersistentDataContainer().has(key, PersistentDataType.STRING);
        }
        return false;
    }
}
