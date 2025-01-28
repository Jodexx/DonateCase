package com.jodexindustries.donatecase.api.event.addon;

import com.jodexindustries.donatecase.api.addon.PowerReason;
import com.jodexindustries.donatecase.api.addon.InternalAddon;
import com.jodexindustries.donatecase.api.event.DCEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddonDisableEvent extends DCEvent {

    private final InternalAddon addon;
    private final PowerReason reason;

}
