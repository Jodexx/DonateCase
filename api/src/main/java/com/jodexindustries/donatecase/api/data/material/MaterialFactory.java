package com.jodexindustries.donatecase.api.data.material;

import com.jodexindustries.donatecase.api.addon.Addon;
import org.jetbrains.annotations.Nullable;

public interface MaterialFactory {

    @Nullable CaseMaterial create(Addon addon);
}
