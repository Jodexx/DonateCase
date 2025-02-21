package com.jodexindustries.donatecase.api.event.player;

import com.jodexindustries.donatecase.api.event.DCEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@Accessors(fluent = true)
@Data
public class JoinEvent extends DCEvent {

    @NotNull private final DCPlayer player;
}
