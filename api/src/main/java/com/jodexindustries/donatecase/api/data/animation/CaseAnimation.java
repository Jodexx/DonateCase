package com.jodexindustries.donatecase.api.data.animation;

import com.jodexindustries.donatecase.api.addon.Addon;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class for case animation storage
 */
@Builder
@Getter
public class CaseAnimation {

    @NotNull private final Addon addon;
    @NotNull private final String name;
    @NotNull private final Class<? extends Animation> animation;
    @Nullable private final String description;

    private final boolean requireBlock;
    private final boolean requireSettings;
    private final boolean removeKeyAtStart;

}
