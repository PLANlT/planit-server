package com.planit.planit.auth;

import com.planit.planit.config.oauth.CustomOAuth2User;
import com.planit.planit.member.enums.SignType;

import java.util.Map;

public class FakeCustomOAuth2User extends CustomOAuth2User {

    public FakeCustomOAuth2User(Map<String, Object> attributes, SignType signType) {
        super(new FakeOAuth2User(attributes), signType);
    }
}
