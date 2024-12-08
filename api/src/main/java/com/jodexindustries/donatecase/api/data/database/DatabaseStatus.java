package com.jodexindustries.donatecase.api.data.database;

/**
 * Enum representing the possible statuses of a database operation.
 */
public enum DatabaseStatus {
    /**
     * Indicates that the database operation was completed successfully.
     */
    COMPLETE,

    /**
     * Indicates that the database operation was cancelled.
     */
    CANCELLED,

    /**
     * Indicates that the database operation failed.
     */
    FAIL
}
