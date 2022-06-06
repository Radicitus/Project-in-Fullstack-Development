package com.github.klefstad_teaching.cs122b.billing.rest;

import com.github.klefstad_teaching.cs122b.billing.model.data.MovieItem;
import com.github.klefstad_teaching.cs122b.billing.model.request.MovieQuantityRequest;
import com.github.klefstad_teaching.cs122b.billing.model.response.BasicResponse;
import com.github.klefstad_teaching.cs122b.billing.model.response.TotalItemResponse;
import com.github.klefstad_teaching.cs122b.billing.repo.BillingRepo;
import com.github.klefstad_teaching.cs122b.billing.util.Validate;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

@RestController
public class CartController {
    private final BillingRepo repo;
    private final Validate validate;

    @Autowired
    public CartController(BillingRepo repo, Validate validate) {
        this.repo = repo;
        this.validate = validate;
    }

    @PostMapping("/cart/insert")
    public ResponseEntity<BasicResponse> cartInsert(
            @RequestBody MovieQuantityRequest request,
            @AuthenticationPrincipal SignedJWT user) throws ParseException {

        // Validation
        validate.isValidQuantity(request);
        validate.isAlreadyInCart(request, user);

        // Insert into cart
        repo.addToCart(request, user);

        BasicResponse br = new BasicResponse()
                .setResult(BillingResults.CART_ITEM_INSERTED);
        return br.toResponse();
    }

    @PostMapping("/cart/update")
    public ResponseEntity<BasicResponse> cartUpdate(
            @RequestBody MovieQuantityRequest request,
            @AuthenticationPrincipal SignedJWT user) throws ParseException {

        // Validation
        validate.isValidQuantity(request);
        validate.isNotAlreadyInCart(request, user);

        // Update item in cart
        repo.updateCartItemQuantity(request, user);

        BasicResponse br = new BasicResponse()
                .setResult(BillingResults.CART_ITEM_UPDATED);
        return br.toResponse();
    }

    @DeleteMapping("/cart/delete/{movieId}")
    public ResponseEntity<BasicResponse> cartDelete(
            @PathVariable Long movieId,
            @AuthenticationPrincipal SignedJWT user) throws ParseException {

        //TODO: Add better implementation
        MovieQuantityRequest request = new MovieQuantityRequest();
        request.setMovieId(movieId);

        // Validation
        validate.isNotAlreadyInCart(request, user);

        // Delete item from cart
        repo.deleteCartItem(request, user);

        BasicResponse br = new BasicResponse()
                .setResult(BillingResults.CART_ITEM_DELETED);
        return br.toResponse();
    }

    @GetMapping("/cart/retrieve")
    public ResponseEntity<TotalItemResponse> cartRetrieve(@AuthenticationPrincipal SignedJWT user) throws ParseException {

        // Validation
        validate.isCartEmpty(user);

        // Get cart items
        List<MovieItem> items = repo.retrieveUserCart(user);

        // Get total value
        BigDecimal total = BigDecimal.ZERO;
        for (MovieItem item : items) {
            total = total.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        TotalItemResponse cr = new TotalItemResponse()
                .setTotal(total)
                .setItems(items)
                .setResult(BillingResults.CART_RETRIEVED);

        return cr.toResponse();
    }

    @PostMapping("/cart/clear")
    public ResponseEntity<BasicResponse> cartClear(@AuthenticationPrincipal SignedJWT user) throws ParseException {

        // Validation
        validate.isCartEmpty(user);

        // Clear user cart
        repo.deleteUserCart(user);

        BasicResponse br = new BasicResponse()
                .setResult(BillingResults.CART_CLEARED);
        return br.toResponse();
    }
}
