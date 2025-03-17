package com.jodexindustries.donatecase.spigot.hook.packetevents;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.event.player.ArmorStandCreatorInteractEvent;
import com.jodexindustries.donatecase.spigot.tools.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PacketEventsPacketListener implements PacketListener {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        User user = event.getUser();

        if (event.getPacketType() != PacketType.Play.Client.INTERACT_ENTITY) return;

        WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);

        int entityId = packet.getEntityId();

        ArmorStandCreator creator = ArmorStandCreator.armorStands.get(entityId);
        if (creator == null) return;

        Player player = Bukkit.getPlayer(user.getUUID());
        if (player == null) return;

        EquipmentSlot hand = packet.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;

        DCAPI.getInstance().getEventBus().post(
                new ArmorStandCreatorInteractEvent(BukkitUtils.fromBukkit(player), creator, hand)
        );

    }

}
