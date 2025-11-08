package com.jodexindustries.donatecase.api.data.casedata;

import java.util.List;

public interface MetaUpdater {

    void updateMeta(Object itemStack, String displayName, List<String> lore, int modelData,
                    boolean enchanted, Integer[] rgb);

}
