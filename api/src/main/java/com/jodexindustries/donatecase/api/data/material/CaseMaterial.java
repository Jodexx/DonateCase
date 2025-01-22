package com.jodexindustries.donatecase.api.data.material;

import com.jodexindustries.donatecase.api.addon.Addon;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class for custom material storage
 */
@Getter
@Builder
public class CaseMaterial implements MaterialHandler {
    @NotNull private final MaterialHandler handler;
    @NotNull private final Addon addon;
    @NotNull private final String id;
    @Nullable private final String description;

    @Override
    public @NotNull Object handle(@NotNull String context) throws CaseMaterialException {
        return handler.handle(context);
    }
}
