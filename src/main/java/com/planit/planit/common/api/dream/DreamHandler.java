package com.planit.planit.common.api.dream;

import com.planit.planit.common.api.dream.status.DreamErrorStatus;
import com.planit.planit.common.api.general.GeneralException;

public class DreamHandler extends GeneralException {
    public DreamHandler(DreamErrorStatus status) {
        super(status);
    }
}
