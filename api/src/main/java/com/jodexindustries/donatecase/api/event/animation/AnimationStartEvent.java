package com.jodexindustries.donatecase.api.event.animation;

import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.event.DCEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AnimationStartEvent extends DCEvent {

    private final DCPlayer player;
    private final ActiveCase activeCase;
}
