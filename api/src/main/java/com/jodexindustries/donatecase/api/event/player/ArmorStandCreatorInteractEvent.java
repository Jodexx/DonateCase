package com.jodexindustries.donatecase.api.event.player;

import com.jodexindustries.donatecase.api.armorstand.ArmorStandCreator;
import com.jodexindustries.donatecase.api.armorstand.EquipmentSlot;
import com.jodexindustries.donatecase.api.event.DCEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Accessors(fluent = true)
@Data
public class ArmorStandCreatorInteractEvent extends DCEvent {

    private final DCPlayer player;

    private final ArmorStandCreator armorStandCreator;

    private final EquipmentSlot hand;
}
