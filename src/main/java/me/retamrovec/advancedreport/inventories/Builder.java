package me.retamrovec.advancedreport.inventories;

import me.retamrovec.advancedreport.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Builder {

    private final Inventory inventory;
    public Builder(String title, int size, InventoryHolder holder) {
        this.inventory = Bukkit.createInventory(holder, size, Formatter.chatColors(title));
    }

    @Contract("_, _, _ -> new")
    public static @NotNull Builder getInstance(String title, int size, InventoryHolder holder) {
        return new Builder(title, size, holder);
    }

    public void addItem(Integer slot, ItemStack is) {
        if (slot == null) {
            inventory.addItem(is);
            return;
        }
        inventory.setItem(slot, is);
    }

    public Inventory getInventory() {
        return inventory;
    }
}
