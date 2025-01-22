package com.jodexindustries.donatecase.tools.updater;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VersionInfo {

    @SerializedName("version_number")
    private final String versionNumber;

    @SerializedName("date_published")
    private final String datePublished;

    private final String name;

    @SerializedName("version_type")
    private final String versionType;

    private final String status;

    private final int downloads;

}
