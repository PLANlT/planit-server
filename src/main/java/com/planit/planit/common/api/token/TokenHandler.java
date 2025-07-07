package com.planit.planit.common.api.token;

import com.planit.planit.common.api.general.GeneralException;
import com.planit.planit.common.api.token.status.TokenErrorStatus;

public class TokenHandler extends GeneralException {
    public TokenHandler(TokenErrorStatus status) {
        super(status);
    }
}
