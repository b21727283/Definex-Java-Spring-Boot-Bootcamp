package com.mgumussoy.advancedtaskmanagement.enums;

public enum ProjectStatus {
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED");

    private final String projectStatus;

    private ProjectStatus(String status) {
        projectStatus = status;
    }

    public String getStatus() {
        return projectStatus;
    }
}
