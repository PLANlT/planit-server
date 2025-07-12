    package com.planit.planit.auth.fixture;

    import com.planit.planit.auth.oauth.CustomOAuth2User;
    import com.planit.planit.member.enums.SignType;

    import java.util.Map;

    public class FakeCustomOAuth2User extends CustomOAuth2User {

        public FakeCustomOAuth2User(Map<String, Object> attributes, SignType signType) {
            super(new FakeOAuth2User(attributes), signType);
        }
    }
