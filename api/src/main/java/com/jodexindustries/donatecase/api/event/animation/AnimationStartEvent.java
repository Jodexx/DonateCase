package com.jodexindustries.donatecase.api.event.animation;

import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.event.DCEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class AnimationStartEvent extends DCEvent {

    private final ActiveCase activeCase;
}
