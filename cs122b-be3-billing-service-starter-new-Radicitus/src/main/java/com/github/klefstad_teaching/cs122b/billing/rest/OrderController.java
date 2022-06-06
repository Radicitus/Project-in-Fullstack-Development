package com.github.klefstad_teaching.cs122b.billing.rest;

import com.github.klefstad_teaching.cs122b.billing.model.data.MovieItem;
import com.github.klefstad_teaching.cs122b.billing.model.data.Sale;
import com.github.klefstad_teaching.cs122b.billing.model.request.PaymentIntentRequest;
import com.github.klefstad_teaching.cs122b.billing.model.response.BasicResponse;
import com.github.klefstad_teaching.cs122b.billing.model.response.SaleListResponse;
import com.github.klefstad_teaching.cs122b.billing.model.response.TotalItemResponse;
import com.github.klefstad_teaching.cs122b.billing.repo.BillingRepo;
import com.github.klefstad_teaching.cs122b.billing.util.Validate;
import com.github.klefstad_teaching.cs122b.billing.model.response.StripeResponse;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.nimbusds.jwt.SignedJWT;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

@RestController
public class OrderController
{
    private final BillingRepo repo;
    private final Validate    validate;

    @Autowired
    public OrderController(BillingRepo repo,Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }

    @GetMapping("/order/payment")
    public ResponseEntity<StripeResponse> paymentIntent(@AuthenticationPrincipal SignedJWT user) throws ParseException, StripeException {

        // Validation
        validate.isCartEmpty(user);

        // Get cart items
        List<MovieItem> items = repo.retrieveUserCart(user);

        // Get total value
        BigDecimal total = BigDecimal.ZERO;
        StringBuilder descriptionBuilder = new StringBuilder();
        for (MovieItem item : items) {
            total = total.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            descriptionBuilder.append(item.getMovieTitle()).append(", ");
        }
        descriptionBuilder.setLength(descriptionBuilder.length() - 2);

        // Set up for PaymentIntent
        Long totalInCents = total.movePointRight(2).longValue();
        String description = descriptionBuilder.toString();

        // Create PaymentIntent params
        PaymentIntentCreateParams paymentIntentCreateParams =
                PaymentIntentCreateParams
                        .builder()
                        .setCurrency("USD")
                        .setDescription(description)
                        .setAmount(totalInCents)
                        .putMetadata("userId", user.getJWTClaimsSet().getClaim("id").toString())
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods
                                        .builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        // Create PaymentIntent
        PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentCreateParams);

        StripeResponse sr = new StripeResponse()
                .setPaymentIntentId(paymentIntent.getId())
                .setClientSecret(paymentIntent.getClientSecret())
                .setResult(BillingResults.ORDER_PAYMENT_INTENT_CREATED);

        return sr.toResponse();
    }

    @PostMapping("/order/complete")
    public ResponseEntity<BasicResponse> paymentComplete(
            @RequestBody PaymentIntentRequest request,
            @AuthenticationPrincipal SignedJWT user) throws StripeException, ParseException {

        // Validation
        validate.isPaymentSuccessful(request.getPaymentIntentId());
        validate.isPaymentForUser(request.getPaymentIntentId(), user);

        // Create sale records
        repo.createSaleRecord(request.getPaymentIntentId(), user);
        repo.createSaleRecordItem(user);

        // Clear user cart
        repo.deleteUserCart(user);

        BasicResponse br = new BasicResponse()
                .setResult(BillingResults.ORDER_COMPLETED);

        return br.toResponse();
    }

    @GetMapping("/order/list")
    public ResponseEntity<SaleListResponse> userSalesList(@AuthenticationPrincipal SignedJWT user) throws ParseException {

        // Get sales for user
        List<Sale> sales = repo.getLastFiveSaleRecordForUser(user);

        // Validate
        validate.isNonZeroSalesForUser(sales);

        SaleListResponse slr = new SaleListResponse()
                .setSales(sales)
                .setResult(BillingResults.ORDER_LIST_FOUND_SALES);

        return slr.toResponse();
    }

    @GetMapping("/order/detail/{saleId}")
    public ResponseEntity<TotalItemResponse> userSalesListDetailed(
            @PathVariable Long saleId,
            @AuthenticationPrincipal SignedJWT user) throws ParseException {

        // Get sale details
        List<MovieItem> sale = repo.getSaleItemsBySaleId(saleId, user);

        // Validate
        validate.isSaleFoundById(sale);

        // Process data
        BigDecimal total = BigDecimal.ZERO;
        for (MovieItem item : sale) {
            total = total.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        TotalItemResponse tir = new TotalItemResponse()
                .setItems(sale)
                .setTotal(total)
                .setResult(BillingResults.ORDER_DETAIL_FOUND);

        return tir.toResponse();
    }
}
