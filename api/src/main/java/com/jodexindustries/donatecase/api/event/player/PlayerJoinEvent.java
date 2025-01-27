package com.jodexindustries.donatecase.api.event.player;

import com.jodexindustries.donatecase.api.event.DCEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerJoinEvent extends DCEvent {

    @NotNull private final DCPlayer player;
}
