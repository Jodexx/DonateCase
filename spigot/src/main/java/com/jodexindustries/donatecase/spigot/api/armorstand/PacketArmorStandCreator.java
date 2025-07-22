package com.jodexindustries.donatecase.spigot.api.armorstand;

import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.data.storage.CaseLocation;
import com.jodexindustries.donatecase.common.entity.creator.APASC;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PacketArmorStandCreator extends APASC {

    public PacketArmorStandCreator(UUID animationId, CaseLocation location) {
        super(
                Bukkit.getOnlinePlayers(),
                animationId, SpigotReflectionUtil.generateEntityId(), location
        );
    }

    @Override
    public void setEquipment(EquipmentSlot equipmentSlot, Object item) {
        setEquipment0(equipmentSlot, SpigotReflectionUtil.decodeBukkitItemStack((ItemStack) item));
    }

}
