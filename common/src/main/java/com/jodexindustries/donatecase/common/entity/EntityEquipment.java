package com.jodexindustries.donatecase.common.entity;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityEquipment {

    private final int entityId;
    private static final EquipmentSlot[] EQUIPMENT_SLOTS = EquipmentSlot.values();
    private final ItemStack[] equipment = new ItemStack[6];

    public EntityEquipment(int entityId) {
        this.entityId = entityId;
        Arrays.fill(this.equipment, ItemStack.EMPTY);
    }

    public void clearSlot(@NotNull EquipmentSlot slot) {
        this.equipment[slot.ordinal()] = ItemStack.EMPTY;
    }

    public void clearAll() {
        Arrays.fill(this.equipment, ItemStack.EMPTY);
    }

    public void setHelmet(@Nullable ItemStack itemStack) {
        this.equipment[5] = itemStack == null ? ItemStack.EMPTY : itemStack;
    }

    public void setChestplate(@Nullable ItemStack itemStack) {
        this.equipment[4] = itemStack == null ? ItemStack.EMPTY : itemStack;
    }

    public void setLeggings(@Nullable ItemStack itemStack) {
        this.equipment[3] = itemStack == null ? ItemStack.EMPTY : itemStack;
    }

    public void setBoots(@Nullable ItemStack itemStack) {
        this.equipment[2] = itemStack == null ? ItemStack.EMPTY : itemStack;
    }

    public void setMainHand(@Nullable ItemStack itemStack) {
        this.equipment[0] = itemStack == null ? ItemStack.EMPTY : itemStack;
    }

    public void setOffhand(@Nullable ItemStack itemStack) {
        this.equipment[1] = itemStack == null ? ItemStack.EMPTY : itemStack;
    }

    public void setItem(@NotNull EquipmentSlot slot, @Nullable ItemStack itemStack) {
        this.equipment[slot.ordinal()] = itemStack == null ? ItemStack.EMPTY : itemStack;
    }

    public @NotNull ItemStack getItem(@NotNull EquipmentSlot slot) {
        ItemStack itemStack = this.equipment[slot.ordinal()];
        return itemStack == null ? ItemStack.EMPTY : itemStack;
    }

    public @NotNull ItemStack getHelmet() {
        return this.getItem(EquipmentSlot.HELMET);
    }

    public @NotNull ItemStack getChestplate() {
        return this.getItem(EquipmentSlot.CHEST_PLATE);
    }

    public @NotNull ItemStack getLeggings() {
        return this.getItem(EquipmentSlot.LEGGINGS);
    }

    public @NotNull ItemStack getBoots() {
        return this.getItem(EquipmentSlot.BOOTS);
    }

    public @NotNull ItemStack getMainHand() {
        return this.getItem(EquipmentSlot.MAIN_HAND);
    }

    public @NotNull ItemStack getOffhand() {
        return this.getItem(EquipmentSlot.OFF_HAND);
    }

    public WrapperPlayServerEntityEquipment createPacket() {
        List<Equipment> equipment = new ArrayList<>();

        for (int i = 0; i < this.equipment.length; ++i) {
            ItemStack itemStack = this.equipment[i];
            equipment.add(new Equipment(EQUIPMENT_SLOTS[i], itemStack));
        }

        return new WrapperPlayServerEntityEquipment(this.entityId, equipment);
    }

}
