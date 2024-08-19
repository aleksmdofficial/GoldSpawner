/*    */ package dev.aleksmd.goldspawner.spawner;
/*    */
/*    */ import dev.aleksmd.goldspawner.Main;
/*    */ import dev.aleksmd.goldspawner.manager.ConfigManager;
/*    */ import dev.aleksmd.goldspawner.utils.HexUtils;
/*    */ import dev.aleksmd.goldspawner.utils.LogUtils;
/*    */ import java.util.HashSet;
/*    */ import java.util.List;
/*    */ import java.util.Set;
/*    */ import java.util.stream.Collectors;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.Material;
/*    */ import org.bukkit.NamespacedKey;
/*    */ import org.bukkit.configuration.ConfigurationSection;
/*    */ import org.bukkit.inventory.ItemStack;
/*    */ import org.bukkit.inventory.Recipe;
/*    */ import org.bukkit.inventory.ShapedRecipe;
/*    */ import org.bukkit.inventory.meta.ItemMeta;
/*    */ import org.bukkit.plugin.Plugin;
/*    */
/*    */ public class Crafting
/*    */ {
/*    */   public static void registerRecipes(Main plugin) {
/* 24 */     ItemStack spawner = new ItemStack(Material.SPAWNER);
/* 25 */     ItemMeta meta = spawner.getItemMeta();
/* 26 */     meta.setDisplayName(HexUtils.translate(ConfigManager.getItemsConfig().getString("spawner.name")));
/* 27 */     meta.setLore((List)ConfigManager.getItemsConfig().getStringList("spawner.lore").stream().map(HexUtils::translate).collect(Collectors.toList()));
/* 28 */     spawner.setItemMeta(meta);
/*    */
/* 30 */     NamespacedKey key = new NamespacedKey((Plugin)plugin, "gold_spawner");
/* 31 */     ShapedRecipe recipe = new ShapedRecipe(key, spawner);
/*    */
/* 33 */     List<String> schem = ConfigManager.getCraftingConfig().getStringList("crafting.schem");
/* 34 */     if (schem.isEmpty()) {
/* 35 */       LogUtils.warning(HexUtils.translate("&fСхема крафта пуста, проверьте конфигурацию &ecraft.yml"));
/*    */
/*    */       return;
/*    */     }
/* 39 */     for (String row : schem) {
/* 40 */       if (row.length() != 3) {
/* 41 */         LogUtils.warning(HexUtils.translate("&fНеверная длина строки в схеме крафта: &e" + row));
/*    */
/*    */         return;
/*    */       }
/*    */     }
/* 46 */     recipe.shape(schem.<String>toArray(new String[0]));
/*    */
/* 48 */     Set<Character> shapeSymbols = new HashSet<>();
/* 49 */     for (String row : schem) {
/* 50 */       for (char c : row.toCharArray()) {
/* 51 */         shapeSymbols.add(Character.valueOf(c));
/*    */       }
/*    */     }
/*    */
/* 55 */     ConfigurationSection ingredientsSection = ConfigManager.getCraftingConfig().getConfigurationSection("crafting");
/* 56 */     if (ingredientsSection != null) {
/* 57 */       Set<String> keys = ingredientsSection.getKeys(false);
/* 58 */       for (String keyChar : keys) {
/* 59 */         if (keyChar.equals("schem"))
/* 60 */           continue;  String materialName = ingredientsSection.getString(keyChar + ".material");
/* 61 */         if (materialName != null) {
/* 62 */           Material material = Material.getMaterial(materialName.toUpperCase());
/* 63 */           if (material != null) {
/* 64 */             char symbol = keyChar.charAt(0);
/* 65 */             if (shapeSymbols.contains(Character.valueOf(symbol)))
/* 66 */               recipe.setIngredient(symbol, material);
/*    */             continue;
/*    */           }
/* 69 */           LogUtils.warning(HexUtils.translate("&fНеизвестный материал: &e" + materialName));
/*    */         }
/*    */       }
/*    */     }
/*    */
/*    */
/* 75 */     Bukkit.addRecipe((Recipe)recipe);
/*    */   }
/*    */ }