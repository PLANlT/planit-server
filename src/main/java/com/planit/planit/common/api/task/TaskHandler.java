package com.planit.planit.common.api.task;

import com.planit.planit.common.api.general.GeneralException;
import com.planit.planit.common.api.task.status.TaskErrorStatus;

public class TaskHandler extends GeneralException {
    public TaskHandler(TaskErrorStatus status) {
        super(status);
    }
}
