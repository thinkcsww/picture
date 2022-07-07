package com.applory.pictureserver.domain.oauth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyOAuth2Token {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private long expires_in;
    private String scope;
    private String jti;
}
