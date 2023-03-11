package com.github.cc007.headsplugin.api.business.domain;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public class Category {
    private String name;
    private List<String> sources;
}
