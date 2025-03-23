package com.mgumussoy.advancedtaskmanagement.listeners;

import com.mgumussoy.advancedtaskmanagement.entities.TaskEntity;
import com.mgumussoy.advancedtaskmanagement.enums.TaskState;
import com.mgumussoy.advancedtaskmanagement.exceptions.TaskStateCanNotBeChanged;
import jakarta.persistence.PreUpdate;

public class PreventTaskStateChangeListener {

    @PreUpdate
    public void preUpdate(TaskEntity taskEntity) {
        if (taskEntity.getState() == TaskState.COMPLETED) {
            throw new TaskStateCanNotBeChanged();
        }
    }
}
