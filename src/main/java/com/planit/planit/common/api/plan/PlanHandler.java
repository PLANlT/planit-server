package com.planit.planit.common.api.plan;

import com.planit.planit.common.api.general.GeneralException;
import com.planit.planit.common.api.plan.status.PlanErrorStatus;

public class PlanHandler extends GeneralException {
    public PlanHandler(PlanErrorStatus status) {
        super(status);
    }
}
