package com.jodexindustries.donatecase.api.data.casedata.gui;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class PageData {
    private int currentPage;
    private int totalPages;
    private Map<Integer, Map<String, CaseGui.Item>> pages;

    public PageData() {
        this.pages = new HashMap<>();
        this.currentPage = 0;
        this.totalPages = 0;
    }
}