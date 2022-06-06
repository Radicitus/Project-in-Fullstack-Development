package com.github.klefstad_teaching.cs122b.gateway.model.Response;

import com.github.klefstad_teaching.cs122b.gateway.model.Container.ResultClass;

public class AuthResponse {
    private ResultClass result;


    public ResultClass getResult() {
        return result;
    }

    public void setResult(ResultClass result) {
        this.result = result;
    }

}
