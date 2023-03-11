package com.github.cc007.headsplugin.api;

import com.github.cc007.headsplugin.api.business.services.heads.CategorySearcher;
import com.github.cc007.headsplugin.api.business.services.heads.CategoryUpdater;
import com.github.cc007.headsplugin.api.business.services.heads.HeadCreator;
import com.github.cc007.headsplugin.api.business.services.heads.HeadPlacer;
import com.github.cc007.headsplugin.api.business.services.heads.HeadSearcher;
import com.github.cc007.headsplugin.api.business.services.heads.HeadToItemstackMapper;

public interface HeadsPluginServices {
    CategorySearcher categorySearcher();

    CategoryUpdater categoryUpdater();

    HeadCreator headCreator();

    HeadPlacer headPlacer();

    HeadSearcher headSearcher();

    HeadToItemstackMapper headToItemstackMapper();
}
