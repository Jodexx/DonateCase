package com.jodexindustries.donatecase.spigot.tools;

import com.jodexindustries.donatecase.api.data.casedata.gui.CaseInventory;
import com.jodexindustries.donatecase.spigot.BukkitBackend;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.spigot.api.armorstand.EntityArmorStandCreator;
import com.jodexindustries.donatecase.spigot.api.armorstand.PacketArmorStandCreator;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.spigot.api.platform.BukkitInventory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ToolsImpl extends DCToolsBukkit {

    private final BukkitBackend backend;

    public ToolsImpl(BukkitBackend backend) {
        this.backend = backend;
    }

    @Override
    public CaseInventory createInventory(int size, String title) {
        return new BukkitInventory(size, title);
    }

    @Override
    public ArmorStandCreator createArmorStand(CaseLocation location) {
        if (backend.getPacketEventsSupport() != null && backend.getPacketEventsSupport().isUsePackets()) {
            return new PacketArmorStandCreator(location);
        } else {
            return new EntityArmorStandCreator(BukkitUtils.toBukkit(location));
        }
    }

    @Override
    public Object loadCaseItem(String id) {
        if(id == null) return new ItemStack(Material.AIR);

        Material material = Material.getMaterial(id);

        if (material == null) {
            Object item = DCTools.getItemFromManager(id);
            return item == null ? new ItemStack(Material.AIR) : item;
        } else {
            return new ItemStack(material);
        }
    }

}
