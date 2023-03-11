package com.github.cc007.headsplugin.api.business.domain;

import lombok.Builder;
import lombok.Data;
import lombok.val;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class Head {
    private String name;
    private String value;
    private UUID headOwner;
    private String headDatabase;

    public Map<String, String> getAsMap() {
        val map = new HashMap<String, String>();
        map.put("name", name);
        map.put("value", value);
        map.put("headOwner", headOwner.toString());
        map.put("headDatabase", headDatabase);
        return map;
    }
}
