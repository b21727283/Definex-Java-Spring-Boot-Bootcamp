package com.mgumussoy.advancedtaskmanagement.enums;

public enum TaskState {
    BACKLOG("BACKLOG"),
    IN_ANALYSIS("IN_ANALYSIS"),
    IN_DEVELOPMENT("IN_DEVELOPMENT"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED"),
    BLOCKED("BLOCKED");

    private final String taskState;

    private TaskState(String state) {
        taskState = state;
    }

    public String getState() {
        return taskState;
    }
}


