package com.jodexindustries.donatecase.api.data.casedata.gui;

import com.jodexindustries.donatecase.api.data.casedata.CCloneable;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for handling item creating
 * @param <G> the type of {@link com.jodexindustries.donatecase.api.gui.CaseGui}
 * @param <M> the type of {@link com.jodexindustries.donatecase.api.data.casedata.CaseDataMaterial}
 */
public interface TypedItemHandler<M extends CCloneable, G> {

    /**
     * Called when tried to handle item in GUI <br/>
     * Actually you can manipulate all items in GUI
     *
     * @param caseGui Opened GUI
     * @param item    Current item
     * @return Completed GUI.Item
     */
    @NotNull
    GUI.@NotNull Item<M> handle(@NotNull G caseGui, @NotNull GUI.Item<M> item);
}
