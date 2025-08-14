package com.jodexindustries.donatecase.spigot.api.platform;

import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.data.casedata.gui.CaseInventory;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitInventory implements CaseInventory, InventoryHolder {

    private final CaseGuiWrapper wrapper;
    private final Inventory inventory;

    public BukkitInventory(CaseGuiWrapper wrapper, int size, String title) {
        this.wrapper = wrapper;
        String safeTitle = title != null ? title : "";
        this.inventory = Bukkit.createInventory(this, size, safeTitle);
    }

    @Override
    public Inventory getHandle() {
        return inventory;
    }

    @Override
    public void setItem(int index, @Nullable Object item) {
        inventory.setItem(index, (ItemStack) item);
    }

    @Override
    public @NotNull CaseGuiWrapper getWrapper() {
        return wrapper;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
