package com.jodexindustries.donatecase.api.data.casedata;

import java.util.List;

public interface MetaUpdatable {

    void updateMeta();

    void updateMeta(String displayName, List<String> lore, int modelData,
                    boolean enchanted, String[] rgb);

}
