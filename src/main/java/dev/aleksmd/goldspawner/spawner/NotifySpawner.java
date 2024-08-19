/*    */ package dev.aleksmd.goldspawner.spawner;
/*    */ 
/*    */ import dev.aleksmd.goldspawner.manager.ConfigManager;
/*    */ import dev.aleksmd.goldspawner.utils.HexUtils;
/*    */ import java.util.List;
/*    */ import java.util.Random;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.Location;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NotifySpawner
/*    */ {
/* 14 */   private static final Random RANDOM = new Random();
/*    */   
/*    */   public static void notifyAllPlayers(Location location, String playerName) {
/* 17 */     if (!ConfigManager.getConfig().getBoolean("notify.enable", false)) {
/*    */       return;
/*    */     }
/*    */     
/* 21 */     String coordinates = maskCoordinates(location);
/* 22 */     for (String line : ConfigManager.getConfig().getStringList("notify.messages")) {
/* 23 */       Bukkit.broadcastMessage(HexUtils.translate(line.replace("%coordinates%", coordinates).replace("%player%", playerName)));
/*    */     }
/*    */   }
/*    */   
/*    */   private static String maskCoordinates(Location location) {
/* 28 */     List<String> symbols = ConfigManager.getConfig().getStringList("notify.settings.symbols");
/* 29 */     boolean showX = ConfigManager.getConfig().getBoolean("notify.settings.coordinates.x", true);
/* 30 */     boolean showY = ConfigManager.getConfig().getBoolean("notify.settings.coordinates.y", true);
/* 31 */     boolean showZ = ConfigManager.getConfig().getBoolean("notify.settings.coordinates.z", true);
/*    */     
/* 33 */     StringBuilder maskedCoordinates = new StringBuilder();
/*    */     
/* 35 */     if (showX) {
/* 36 */       maskedCoordinates.append(maskNumber(location.getBlockX(), symbols)).append(" ");
/*    */     } else {
/* 38 */       maskedCoordinates.append("# ");
/*    */     } 
/*    */     
/* 41 */     if (showY) {
/* 42 */       maskedCoordinates.append(location.getBlockY()).append(" ");
/*    */     } else {
/* 44 */       maskedCoordinates.append("# ");
/*    */     } 
/*    */     
/* 47 */     if (showZ) {
/* 48 */       maskedCoordinates.append(maskNumber(location.getBlockZ(), symbols));
/*    */     } else {
/* 50 */       maskedCoordinates.append("#");
/*    */     } 
/*    */     
/* 53 */     return maskedCoordinates.toString();
/*    */   }
/*    */   
/*    */   private static String maskNumber(int number, List<String> symbols) {
/* 57 */     String numStr = String.valueOf(number);
/* 58 */     char[] chars = numStr.toCharArray();
/*    */     
/* 60 */     int digitsToMask = (int)Math.ceil(chars.length / 3.0D);
/* 61 */     int maskedCount = 0;
/*    */     
/* 63 */     while (maskedCount < digitsToMask) {
/* 64 */       int index = RANDOM.nextInt(chars.length);
/* 65 */       if (Character.isDigit(chars[index])) {
/* 66 */         chars[index] = ((String)symbols.get(RANDOM.nextInt(symbols.size()))).charAt(0);
/* 67 */         maskedCount++;
/*    */       } 
/*    */     } 
/*    */     
/* 71 */     return new String(chars);
/*    */   }
/*    */ }
