package com.github.cc007.headsplugin.api.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public interface HeadPlacer {
    /**
     * Place a head {@link ItemStack} at a certain {@link Location} in the world, with a certain rotation
     *
     * @param headItemStack the head itemstack to place in the world
     * @param location the location where to place the head
     * @param rotation the rotation of the head
     */
    void placeHead(@NonNull ItemStack headItemStack, @NonNull Location location, @NonNull BlockFace rotation);

    /**
     * Place a {@link Head} at a certain {@link Location} in the world, with a certain rotation
     *
     * @param head the head to place in the world
     * @param location the location where to place the head
     * @param rotation the rotation of the head
     */
    void placeHead(@NonNull Head head, @NonNull Location location, @NonNull BlockFace rotation);

    /**
     * Place a wall head {@link ItemStack} at a certain {@link Location} in the world, with a certain facing direction
     *
     * @param wallHeadItemStack the wall head itemstack to place in the world
     * @param location the location where to place the wall head
     * @param facing the facing direction of the wall head
     */
    void placeWallHead(@NonNull ItemStack wallHeadItemStack, @NonNull Location location, @NonNull BlockFace facing);

    /**
     * Place a {@link Head} at a certain {@link Location} in the world as wall head, with a certain facing direction
     *
     * @param head the head to place in the world
     * @param location the location where to place the wall head
     * @param facing the facing direction of the wall head
     */
    void placeWallHead(@NonNull Head head, @NonNull Location location, @NonNull BlockFace facing);
}
