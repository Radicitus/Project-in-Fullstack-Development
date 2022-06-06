package com.github.klefstad_teaching.cs122b.idm.model.request;

import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;

public class RefreshRequest {
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }


}
