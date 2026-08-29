package cn.charlotte.pit.config;

import cn.charlotte.pit.ThePit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;

public class PreviewConfig {
    private static YamlConfiguration config;
    private static File configFile;

    private static void lazyInit() {
        if (config == null || configFile == null) {
            configFile = new File(ThePit.getInstance().getDataFolder(), "preview.yml");
            if (!configFile.exists()) {
                try {
                    configFile.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            config = YamlConfiguration.loadConfiguration(configFile);
        }
    }

    public static boolean saveOrOverwriteItem(int slot, ItemStack itemStack) {
        if (slot < 1 || slot > 21) {
            return false;
        }
        lazyInit();
        try {
            String base64String = itemStackToBase64(itemStack);
            config.set("preview.slots." + slot, base64String);
            config.save(configFile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ItemStack getItem(int slot) {
        if (slot < 1 || slot > 21) {
            return null;
        }
        lazyInit();
        String base64String = config.getString("preview.slots." + slot);
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }
        try {
            return base64ToItemStack(base64String);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String itemStackToBase64(ItemStack itemStack) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
        dataOutput.writeObject(itemStack);
        dataOutput.close();
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    public static ItemStack base64ToItemStack(String base64) throws Exception {
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
        ItemStack item = (ItemStack) dataInput.readObject();
        dataInput.close();
        return item;
    }
}