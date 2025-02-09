package com.jodexindustries.donatecase.common.tools.updater;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VersionInfo {

    @SerializedName("version_number")
    private String versionNumber;

    @SerializedName("date_published")
    private String datePublished;

    private String name;

    @SerializedName("version_type")
    private String versionType;

    private String status;

    private int downloads;

    private boolean isNew;

}
