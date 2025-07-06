package com.planit.planit.common.api.member;

import com.planit.planit.common.api.general.GeneralException;
import com.planit.planit.common.api.member.status.MemberErrorStatus;

public class MemberHandler extends GeneralException {
    public MemberHandler(MemberErrorStatus status) {
        super(status);
    }
}
