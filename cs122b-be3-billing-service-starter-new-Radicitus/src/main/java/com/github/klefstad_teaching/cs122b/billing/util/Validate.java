package com.github.klefstad_teaching.cs122b.billing.util;

import com.github.klefstad_teaching.cs122b.billing.model.data.MovieItem;
import com.github.klefstad_teaching.cs122b.billing.model.data.Sale;
import com.github.klefstad_teaching.cs122b.billing.model.request.MovieQuantityRequest;
import com.github.klefstad_teaching.cs122b.billing.repo.BillingRepo;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.nimbusds.jwt.SignedJWT;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;

@Component
public final class Validate {

    private final BillingRepo repo;

    public Validate(BillingRepo repo) {
        this.repo = repo;
    }

    public void isValidQuantity(MovieQuantityRequest request) {
        if (request.getQuantity() < 1) {
            throw new ResultError(BillingResults.INVALID_QUANTITY);
        }

        if (request.getQuantity() > 10) {
            throw new ResultError(BillingResults.MAX_QUANTITY);
        }
    }

    public void isAlreadyInCart(MovieQuantityRequest request, SignedJWT user) throws ParseException {
        if (repo.getCartItem(request, user).size() > 0) {
            throw new ResultError(BillingResults.CART_ITEM_EXISTS);
        }
    }

    public void isNotAlreadyInCart(MovieQuantityRequest request, SignedJWT user) throws ParseException {
        if (repo.getCartItem(request, user).size() < 1) {
            throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);
        }
    }

    public void isCartEmpty(SignedJWT user) throws ParseException {
        if (repo.getUserCartItems(user).size() < 1) {
            throw new ResultError(BillingResults.CART_EMPTY);
        }
    }

    public void isPaymentSuccessful(String paymentIntentId) throws StripeException {
        if (!PaymentIntent.retrieve(paymentIntentId).getStatus().equals("succeeded")) {
            throw new ResultError(BillingResults.ORDER_CANNOT_COMPLETE_NOT_SUCCEEDED);
        }
    }

    public void isPaymentForUser(String paymentIntentId, SignedJWT user) throws ParseException, StripeException {
        String PaymentIntentUser = PaymentIntent.retrieve(paymentIntentId).getMetadata().get("userId");
        String thisUser = String.valueOf(user.getJWTClaimsSet().getClaim("id"));

        if (!PaymentIntentUser.equals(thisUser)) {
            throw new ResultError(BillingResults.ORDER_CANNOT_COMPLETE_WRONG_USER);
        }
    }

    public void isNonZeroSalesForUser(List<Sale> sales) {
        if (sales.size() < 1) {
            throw new ResultError(BillingResults.ORDER_LIST_NO_SALES_FOUND);
        }
    }

    public void isSaleFoundById(List<MovieItem> sale) {
        if (sale.size() < 1) {
            throw new ResultError(BillingResults.ORDER_DETAIL_NOT_FOUND);
        }
    }

}
