package com.jodexindustries.donatecase.api.event.player;

import com.jodexindustries.donatecase.api.data.casedata.gui.CaseGuiWrapper;
import com.jodexindustries.donatecase.api.event.DCEvent;
import com.jodexindustries.donatecase.api.platform.DCPlayer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.kyori.event.Cancellable;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@Accessors(fluent = true)
@Data
public class GuiClickEvent extends DCEvent implements Cancellable {

    private final int slot;
    @NotNull private final DCPlayer player;
    @NotNull private final CaseGuiWrapper guiWrapper;
    @NotNull private final String itemType;

    private boolean cancelled;

    @Override
    public boolean cancelled() {
        return cancelled;
    }

    @Override
    public void cancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
