package com.mgumussoy.advancedtaskmanagement.enums;

public enum TaskPriority {
    HIGHEST("HIGHEST"),
    HIGH("HIGH"),
    MEDIUM("MEDIUM"),
    LOW("LOW"),
    LOWEST("LOWEST");

    private final String taskPriority;

    private TaskPriority(String priority) {
        taskPriority = priority;
    }

    public String getPriority() {
        return taskPriority;
    }
}
