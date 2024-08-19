package dev.aleksmd.goldspawner.listener;

import dev.aleksmd.goldspawner.Main;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BukkitListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Проверка, что игрок разрушает спавнер
        if (event.getBlock().getType() == Material.SPAWNER && event.getBlock().hasMetadata("gold_spawner")) {
            ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
            String materialName = Main.getInstance().getConfig().getString("item.material");
            Material material = Material.valueOf(materialName);

            // Проверка, что игрок использует правильный предмет для разрушения спавнера
            if (itemStack.getType() == material && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta != null) {
                    PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                    if (container.has(new NamespacedKey(Main.getInstance(), "jake"), PersistentDataType.STRING)) {
                        String value = container.get(new NamespacedKey(Main.getInstance(), "jake"), PersistentDataType.STRING);
                        if (value != null && value.equals("1")) {

                            // Отменяем стандартное разрушение блока
                            event.setCancelled(true);

                            // Удаляем блок спавнера
                            event.getBlock().setType(Material.AIR);

                            // Спавним спавнер как предмет на земле
                            ItemStack spawnerItem = new ItemStack(Material.SPAWNER);
                            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), spawnerItem);

                            // Уменьшаем количество предметов в руке игрока
                            itemStack.setAmount(itemStack.getAmount() - 1);

                            // Отправляем сообщение игроку, если это настроено
                            boolean messages = Main.getInstance().getConfig().getBoolean("item.message-break");
                            if (messages) {
                                String message = Main.getInstance().getConfig().getString("messages.break-spawner");
                                if (message != null) {
                                    event.getPlayer().sendMessage(message);
                                }
                            }

                            // Удаление спавнера из памяти
                            Main.getInstance().getSpawnerLocations().remove(event.getBlock().getLocation());
                        }
                    }
                }
            }
        }
    }
}
