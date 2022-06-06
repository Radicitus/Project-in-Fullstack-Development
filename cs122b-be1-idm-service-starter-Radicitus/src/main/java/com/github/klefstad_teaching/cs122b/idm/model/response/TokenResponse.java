package com.github.klefstad_teaching.cs122b.idm.model.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;

public class TokenResponse extends ResponseModel<TokenResponse> {

    private String accessToken;
    private RefreshToken refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public TokenResponse setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getRefreshToken() {
        return refreshToken.getToken();
    }

    public TokenResponse setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
}
