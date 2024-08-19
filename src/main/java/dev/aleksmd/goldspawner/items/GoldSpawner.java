package dev.aleksmd.goldspawner.items;

import dev.aleksmd.goldspawner.Main;
import dev.aleksmd.goldspawner.manager.ConfigManager;
import dev.aleksmd.goldspawner.spawner.NotifySpawner;
import dev.aleksmd.goldspawner.utils.HexUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class GoldSpawner implements Listener {

    private static final Map<Location, Boolean> spawnerLocations = new HashMap<>();

    public static Map<Location, Boolean> getSpawnerLocations() {
        return spawnerLocations;
    }

    public void saveSpawnersToFile() {
        File file = new File(Main.getInstance().getDataFolder(), "spawners.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        List<Map<String, Object>> spawners = spawnerLocations.keySet().stream().map(loc -> {
            Map<String, Object> locMap = new HashMap<>();
            locMap.put("world", loc.getWorld().getName());
            locMap.put("x", loc.getX());
            locMap.put("y", loc.getY());
            locMap.put("z", loc.getZ());
            return locMap;
        }).collect(Collectors.toList());

        config.set("spawners", spawners);
        try {
            config.save(file);
            Bukkit.getLogger().info("Spawner locations saved to spawners.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSpawnersFromFile() {
        File file = new File(Main.getInstance().getDataFolder(), "spawners.yml");
        if (!file.exists()) {
            return;  // Если файла нет, возвращаемся
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<Map<String, Object>> spawners = (List<Map<String, Object>>) config.get("spawners");

        if (spawners != null) {
            for (Map<String, Object> locMap : spawners) {
                World world = Bukkit.getWorld((String) locMap.get("world"));
                if (world != null) {
                    double x = (double) locMap.get("x");
                    double y = (double) locMap.get("y");
                    double z = (double) locMap.get("z");
                    Location loc = new Location(world, x, y, z);

                    Block block = world.getBlockAt(loc);

                    if (block.getType() == Material.SPAWNER) {
                        // Установка метаданных для загруженного спавнера
                        block.setMetadata("gold_spawner", new FixedMetadataValue(Main.getInstance(), true));
                        spawnerLocations.put(loc, true);
                    }
                }
            }
            Bukkit.getLogger().info("Spawner locations loaded from spawners.yml");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item == null || item.getType() != Material.SPAWNER)
            return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.getDisplayName().equals(HexUtils.translate(ConfigManager.getItemsConfig().getString("spawner.name")))) {
            return;
        }

        Block block = event.getBlockPlaced();
        int minHeight = ConfigManager.getItemsConfig().getInt("spawner.settings.limiter.min");
        int maxHeight = ConfigManager.getItemsConfig().getInt("spawner.settings.limiter.max");

        if (block.getY() < minHeight) {
            for (String line : ConfigManager.getMessagesConfig().getStringList("messages.spawner.limit-min")) {
                event.getPlayer().sendMessage(HexUtils.translate(line.replace("%min%", String.valueOf(minHeight))));
            }
            event.setCancelled(true);
            return;
        }
        if (block.getY() > maxHeight) {
            for (String line : ConfigManager.getMessagesConfig().getStringList("messages.spawner.limit-max")) {
                event.getPlayer().sendMessage(HexUtils.translate(line.replace("%max%", String.valueOf(maxHeight))));
            }
            event.setCancelled(true);
            return;
        }

        block.setMetadata("gold_spawner", new FixedMetadataValue(Main.getInstance(), true));

        CreatureSpawner spawner = (CreatureSpawner)block.getState();
        spawner.setSpawnedType(EntityType.AREA_EFFECT_CLOUD);
        spawner.update();

        spawnerLocations.put(block.getLocation(), true);
        Bukkit.getLogger().info("Spawner added at: " + block.getLocation().toString());

        saveSpawnersToFile(); // Сохранение спавнеров после установки

        if (ConfigManager.getConfig().getBoolean("notify.enable")) {
            NotifySpawner.notifyAllPlayers(block.getLocation(), event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.SPAWNER || !block.hasMetadata("gold_spawner"))
            return;

        // Создание ItemStack для спавнера с метаданными из конфигурации
        ItemStack spawnerItem = new ItemStack(Material.SPAWNER, 1);
        ItemMeta meta = spawnerItem.getItemMeta();
        if (meta != null) {
            // Получаем имя спавнера из конфигурации
            String itemName = ConfigManager.getItemsConfig().getString("spawner.name");
            meta.setDisplayName(HexUtils.translate(itemName)); // Устанавливаем имя спавнера

            // Устанавливаем лор (описание) из конфигурации
            List<String> lore = ConfigManager.getItemsConfig().getStringList("spawner.lore").stream()
                    .map(HexUtils::translate)
                    .collect(Collectors.toList());
            meta.setLore(lore);

            // Добавление метаданных к предмету
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(new NamespacedKey(Main.getInstance(), "gold_spawner"), PersistentDataType.STRING, "true");

            spawnerItem.setItemMeta(meta);
        }

        // Выбрасывание спавнера на место разрушенного блока
        block.getWorld().dropItemNaturally(block.getLocation(), spawnerItem);

        // Удаление метаданных и спавнера из карты
        block.removeMetadata("gold_spawner", Main.getInstance());
        spawnerLocations.remove(block.getLocation());
        Bukkit.getLogger().info("Spawner removed at: " + block.getLocation().toString());

        saveSpawnersToFile(); // Сохранение спавнеров после удаления

        // Устанавливаем блок в воздух, чтобы его не было
        block.setType(Material.AIR);

        // Отмена стандартного события разрушения блока
        event.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER &&
                event.getEntity().getLocation().getBlock().hasMetadata("gold_spawner")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block != null && block.getType() == Material.SPAWNER && block.hasMetadata("gold_spawner")) {
                ItemStack item = event.getItem();
                if (item != null && item.getType().name().endsWith("_SPAWN_EGG")) {
                    event.setCancelled(true);
                    Player player = event.getPlayer();
                    for (String line : ConfigManager.getMessagesConfig().getStringList("messages.spawner.mob-spawner-add"))
                        player.sendMessage(HexUtils.translate(line));
                }
            }
        }
    }
}
