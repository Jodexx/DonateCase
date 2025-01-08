package com.jodexindustries.donatecase.tools.updater;

import com.google.gson.annotations.SerializedName;

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

    public VersionInfo(String versionNumber, String datePublished,
                       String name, String versionType, String status,
                       int downloads) {
        this.versionNumber = versionNumber;
        this.datePublished = datePublished;
        this.name = name;
        this.versionType = versionType;
        this.status = status;
        this.downloads = downloads;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public String getName() {
        return name;
    }

    public String getVersionType() {
        return versionType;
    }

    public String getStatus() {
        return status;
    }

    public int getDownloads() {
        return downloads;
    }

}
