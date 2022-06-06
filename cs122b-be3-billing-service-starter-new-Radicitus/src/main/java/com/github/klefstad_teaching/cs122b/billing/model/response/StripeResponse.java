package com.github.klefstad_teaching.cs122b.billing.model.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;

public class StripeResponse extends ResponseModel<StripeResponse> {
    private String paymentIntentId;
    private String clientSecret;


    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public StripeResponse setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public StripeResponse setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }
}
