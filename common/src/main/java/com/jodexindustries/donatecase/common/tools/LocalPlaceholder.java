package com.jodexindustries.donatecase.common.tools;

import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedata.CaseDataItem;
import com.jodexindustries.donatecase.api.tools.DCTools;
import com.jodexindustries.donatecase.api.tools.Placeholder;

import java.util.*;

public class LocalPlaceholder implements Placeholder {

    private final String name;
    private final String value;

    public LocalPlaceholder(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static LocalPlaceholder of(String name, Object value) {
        return new LocalPlaceholder(name, String.valueOf(value));
    }

    public static Set<LocalPlaceholder> of(CaseData caseData) {
        return new HashSet<>(Arrays.asList(
                of("%casetype%", caseData.caseType()),
                of("%casename%", caseData.caseType()), // outdated
                of("%casedisplayname%", caseData.caseDisplayName()),
                of("%casetitle%", caseData.caseGui().title()),
                of("%animation%", caseData.animation())
        ));
    }

    public static Set<LocalPlaceholder> of(CaseDataItem item) {
        return new HashSet<>(Arrays.asList(
                of("%group%", item.group()),
                of("%groupdisplayname%", item.material().displayName())
        ));
    }

    public static Set<LocalPlaceholder> of(CaseData.History data) {
        String time = DCTools.getDateFormat().format(new Date(data.time()));
        String group = data.group();
        String action = data.action() != null ? data.action() : group;

        return new HashSet<>(Arrays.asList(
                of("%group%", group),
                of("%action%", action),
                of("%player%", data.playerName()),
                of("%casetype%", data.caseType()),
                of("%casename%", data.caseType()), // outdated
                of("%time%", time),
                of("%id%", data.id())
        ));
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        LocalPlaceholder that = (LocalPlaceholder) object;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String value() {
        return value;
    }
}
