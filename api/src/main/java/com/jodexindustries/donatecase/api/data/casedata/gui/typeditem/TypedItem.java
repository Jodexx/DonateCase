package com.jodexindustries.donatecase.api.data.casedata.gui.typeditem;

import com.jodexindustries.donatecase.api.addon.Addon;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class for creating GUI typed item
 * @see TypedItemHandler
 * @see TypedItemClickHandler
 */
@Accessors(fluent = true)
@Getter
@Builder
public class TypedItem {

    @NotNull private final String id;
    @NotNull private final Addon addon;
    @Nullable private final String description;
    @Nullable private final TypedItemHandler handler;
    @Nullable private final TypedItemClickHandler click;

    private final boolean updateMeta;
    private final boolean loadOnCase;

}
