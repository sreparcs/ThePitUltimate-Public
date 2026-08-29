package cn.charlotte.pit.data.sub;

import cn.charlotte.pit.data.deserializer.TrashDeserializer;
import cn.charlotte.pit.data.serializer.TrashSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cn.charlotte.pit.util.inventory.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

@JsonSerialize(using = TrashSerializer.class)
@JsonDeserialize(using = TrashDeserializer.class)
public class PlayerTrash {

    public static final int INVENTORY_SIZE = 45;
    public static final int STORAGE_SIZE = 36;
    public static final int CLEAN_BUTTON_SLOT = 40;
    public static final String TITLE = "§8垃圾桶";

    private Inventory inventory;
    private String backup = "";
    private long lastCleanTime = 0L;

    public PlayerTrash() {
        this.inventory = Bukkit.createInventory(null, INVENTORY_SIZE, TITLE);
    }

    public static PlayerTrash deserialization(String content, String backup, long lastCleanTime) {
        PlayerTrash trash = new PlayerTrash();
        trash.applyContent(content);
        trash.backup = backup == null ? "" : backup;
        trash.lastCleanTime = lastCleanTime;
        return trash;
    }

    private void applyContent(String content) {
        ItemStack[] items = readItems(content);
        if (items == null) {
            return;
        }
        for (int i = 0; i < STORAGE_SIZE; i++) {
            this.inventory.setItem(i, i < items.length ? items[i] : null);
        }
    }

    private static ItemStack[] readItems(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        try {
            return InventoryUtil.stringToItems(content);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ItemStack[] getStorageContents() {
        ItemStack[] contents = new ItemStack[STORAGE_SIZE];
        for (int i = 0; i < STORAGE_SIZE; i++) {
            contents[i] = this.inventory.getItem(i);
        }
        return contents;
    }

    public void setStorageContents(ItemStack[] contents) {
        for (int i = 0; i < STORAGE_SIZE; i++) {
            this.inventory.setItem(i, contents == null || i >= contents.length ? null : contents[i]);
        }
    }

    public String serialize() {
        return InventoryUtil.itemsToString(getStorageContents());
    }

    public int getItemCount() {
        int count = 0;
        for (ItemStack item : getStorageContents()) {
            if (item != null && item.getTypeId() != 0) {
                count++;
            }
        }
        return count;
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public boolean hasBackup() {
        return this.backup != null && !this.backup.isEmpty();
    }

    public ItemStack[] consumeBackup() {
        if (!hasBackup()) {
            return null;
        }
        ItemStack[] items = readItems(this.backup);
        this.backup = "";
        return items;
    }

    public void snapshotAndClean(long cycleTime) {
        if (!isEmpty()) {
            this.backup = serialize();
        }
        setStorageContents(null);
        this.lastCleanTime = cycleTime;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public String getBackup() {
        return this.backup;
    }

    public void setBackup(String backup) {
        this.backup = backup == null ? "" : backup;
    }

    public long getLastCleanTime() {
        return this.lastCleanTime;
    }

    public void setLastCleanTime(long lastCleanTime) {
        this.lastCleanTime = lastCleanTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlayerTrash)) {
            return false;
        }
        PlayerTrash other = (PlayerTrash) o;
        return this.lastCleanTime == other.lastCleanTime
                && Objects.equals(this.backup, other.backup)
                && Objects.equals(this.inventory, other.inventory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.inventory, this.backup, this.lastCleanTime);
    }

    @Override
    public String toString() {
        return "PlayerTrash{items=" + getItemCount() + ", hasBackup=" + hasBackup() + "}";
    }
}
