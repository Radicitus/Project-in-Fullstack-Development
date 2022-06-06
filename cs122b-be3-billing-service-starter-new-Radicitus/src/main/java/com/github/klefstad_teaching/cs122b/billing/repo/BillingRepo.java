package com.github.klefstad_teaching.cs122b.billing.repo;

import com.github.klefstad_teaching.cs122b.billing.model.data.MovieItem;
import com.github.klefstad_teaching.cs122b.billing.model.data.MovieInCart;
import com.github.klefstad_teaching.cs122b.billing.model.data.Sale;
import com.github.klefstad_teaching.cs122b.billing.model.request.MovieQuantityRequest;
import com.github.klefstad_teaching.cs122b.billing.util.Helper;
import com.nimbusds.jwt.SignedJWT;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.time.Instant;
import java.util.List;

@Component
public class BillingRepo
{
    private final NamedParameterJdbcTemplate template;
    private final Helper helper;

    @Autowired
    public BillingRepo(NamedParameterJdbcTemplate template, Helper helper) {
        this.template = template;
        this.helper = helper;
    }

    public List<MovieInCart> getCartItem(MovieQuantityRequest request, SignedJWT user) throws ParseException {
        List<MovieInCart> movieInCart = this.template.query(
                "SELECT * " +
                        "FROM billing.cart " +
                        "WHERE movie_id = :movieId AND user_id = :userId",

                new MapSqlParameterSource()
                        .addValue("movieId", request.getMovieId(), Types.BIGINT)
                        .addValue("userId", user.getJWTClaimsSet().getClaim("id")),

                (rs, rowNum) ->
                        new MovieInCart()
                                .setMovieId(rs.getLong("movie_id"))
                                .setUserId(rs.getInt("user_id"))
                                .setQuantity(rs.getInt("quantity"))

        );

        return movieInCart;
    }

    public void addToCart(MovieQuantityRequest request, SignedJWT user) throws ParseException {
        int rowsUpdated = this.template.update(
                "INSERT INTO billing.cart (user_id, movie_id, quantity) " +
                        "VALUES (:user_id, :movie_id, :quantity)",

                new MapSqlParameterSource()
                        .addValue("user_id", user.getJWTClaimsSet().getClaim("id"), Types.INTEGER)
                        .addValue("movie_id", request.getMovieId(), Types.BIGINT)
                        .addValue("quantity", request.getQuantity(), Types.INTEGER)
        );
    }

    public void updateCartItemQuantity(MovieQuantityRequest request, SignedJWT user) throws ParseException {
        int rowsUpdated = this.template.update(
                "UPDATE billing.cart " +
                        "SET quantity = :quantity " +
                        "WHERE movie_id = :movie_id AND user_id = :user_id",

                new MapSqlParameterSource()
                        .addValue("user_id", user.getJWTClaimsSet().getClaim("id"), Types.INTEGER)
                        .addValue("movie_id", request.getMovieId(), Types.BIGINT)
                        .addValue("quantity", request.getQuantity(), Types.INTEGER)
        );
    }

    public void deleteCartItem(MovieQuantityRequest request, SignedJWT user) throws ParseException {
        int rowsUpdated = this.template.update(
                "DELETE FROM billing.cart " +
                        "WHERE movie_id = :movie_id AND user_id = :user_id",

                new MapSqlParameterSource()
                        .addValue("user_id", user.getJWTClaimsSet().getClaim("id"), Types.INTEGER)
                        .addValue("movie_id", request.getMovieId(), Types.BIGINT)
        );
    }

    public List<MovieInCart> getUserCartItems(SignedJWT user) throws ParseException {
        List<MovieInCart> moviesInCart = this.template.query(
                "SELECT * " +
                        "FROM billing.cart " +
                        "WHERE user_id = :userId",

                new MapSqlParameterSource()
                        .addValue("userId", user.getJWTClaimsSet().getClaim("id")),

                (rs, rowNum) ->
                        new MovieInCart()
                                .setMovieId(rs.getLong("movie_id"))
                                .setUserId(rs.getInt("user_id"))
                                .setQuantity(rs.getInt("quantity"))

        );

        return moviesInCart;
    }

    public void deleteUserCart(SignedJWT user) throws ParseException {
        int rowsUpdated = this.template.update(
                "DELETE FROM billing.cart " +
                        "WHERE user_id = :user_id",

                new MapSqlParameterSource()
                        .addValue("user_id", user.getJWTClaimsSet().getClaim("id"), Types.INTEGER)
        );
    }

    public List<MovieItem> retrieveUserCart(SignedJWT user) throws ParseException {
        List<MovieItem> cart = this.template.query(
                "SELECT unit_price, quantity, id, title, backdrop_path, poster_path, premium_discount " +
                        "FROM billing.cart c " +
                        "JOIN billing.movie_price mp ON c.movie_id = mp.movie_id " +
                        "JOIN movies.movie m ON c.movie_id = m.id " +
                        "WHERE user_id = :userId",

                new MapSqlParameterSource()
                        .addValue("userId", user.getJWTClaimsSet().getClaim("id")),

                (rs, rowNum) ->
                {
                    try {
                        return new MovieItem()
                                .setUnitPrice(
                                        helper.applyDiscountIfPremium(rs.getBigDecimal("unit_price"),
                                                rs.getInt("premium_discount"),
                                                user))
                                .setQuantity(rs.getInt("quantity"))
                                .setMovieId(rs.getLong("id"))
                                .setMovieTitle(rs.getString("title"))
                                .setBackdropPath(rs.getString("backdrop_path"))
                                .setPosterPath(rs.getString("poster_path"));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        return cart;
    }

    public void createSaleRecord(String paymentIntentId, SignedJWT user) throws ParseException, StripeException {
        int rowsUpdated = this.template.update(
                "INSERT INTO billing.sale (user_id, total, order_date) " +
                        "VALUES (:user_id, :total, :order_date)",

                new MapSqlParameterSource()
                        .addValue("user_id", user.getJWTClaimsSet().getClaim("id"), Types.INTEGER)
                        .addValue("total", BigDecimal.valueOf(PaymentIntent.retrieve(paymentIntentId).getAmount())
                                .movePointLeft(2), Types.DECIMAL)
                        .addValue("order_date", Timestamp.from(Instant.now()), Types.TIMESTAMP)
        );
    }

    public List<Sale> getLastFiveSaleRecordForUser(SignedJWT user) throws ParseException {
        List<Sale> sales = this.template.query(
                "SELECT * " +
                        "FROM billing.sale " +
                        "WHERE user_id = :user_id " +
                        "ORDER BY order_date DESC " +
                        "LIMIT 5",

                new MapSqlParameterSource()
                        .addValue("user_id", user.getJWTClaimsSet().getClaim("id"), Types.INTEGER),

                (rs, rowNum) ->
                        new Sale()
                                .setSaleId(rs.getInt("id"))
                                .setTotal(rs.getBigDecimal("total"))
                                .setOrderDate(rs.getTimestamp("order_date"))
        );
        return sales;
    }

    public void createSaleRecordItem(SignedJWT user) throws ParseException {
        Integer saleId = getLastFiveSaleRecordForUser(user).get(0).getSaleId();
        List<MovieItem> userCart = retrieveUserCart(user);

        for (MovieItem item : userCart) {
            int rowsUpdated = this.template.update(
                    "INSERT INTO billing.sale_item (sale_id, movie_id, quantity) " +
                            "VALUES (:sale_id, :movie_id, :quantity)",

                    new MapSqlParameterSource()
                            .addValue("sale_id", saleId, Types.INTEGER)
                            .addValue("movie_id", item.getMovieId(), Types.BIGINT)
                            .addValue("quantity", item.getQuantity(), Types.INTEGER)
            );
        }
    }

    public List<MovieItem> getSaleItemsBySaleId(Long saleId, SignedJWT user) throws ParseException {
        List<MovieItem> sale = this.template.query(
                "SELECT unit_price, quantity, m.id, title, backdrop_path, poster_path, premium_discount " +
                        "FROM billing.sale s " +
                        "JOIN billing.sale_item si ON si.sale_id = s.id " +
                        "JOIN movies.movie m ON m.id = si.movie_id " +
                        "JOIN billing.movie_price mp ON mp.movie_id = si.movie_id " +
                        "WHERE s.id = :saleId AND s.user_id = :userId",

                new MapSqlParameterSource()
                        .addValue("saleId", saleId, Types.BIGINT)
                        .addValue("userId", user.getJWTClaimsSet().getClaim("id"), Types.INTEGER),

        (rs, rowNum) ->
                {
                    try {
                        return new MovieItem()
                                .setUnitPrice(
                                        helper.applyDiscountIfPremium(rs.getBigDecimal("unit_price"),
                                                rs.getInt("premium_discount"),
                                                user))
                                .setQuantity(rs.getInt("quantity"))
                                .setMovieId(rs.getLong("id"))
                                .setMovieTitle(rs.getString("title"))
                                .setBackdropPath(rs.getString("backdrop_path"))
                                .setPosterPath(rs.getString("poster_path"));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        return sale;
    }

}
