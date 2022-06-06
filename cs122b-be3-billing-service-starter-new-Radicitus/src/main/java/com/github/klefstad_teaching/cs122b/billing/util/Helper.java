package com.github.klefstad_teaching.cs122b.billing.util;

import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;

@Component
public class Helper {

    public BigDecimal applyDiscountIfPremium(BigDecimal unitPrice, Integer discount, SignedJWT user) throws ParseException {
        if (user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES).contains("PREMIUM")) {
            return unitPrice.multiply(BigDecimal.valueOf(1 - (discount / 100.0))).setScale(2, RoundingMode.DOWN);
        } else {
            return unitPrice;
        }
    }

}
