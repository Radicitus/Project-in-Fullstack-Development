package com.github.klefstad_teaching.cs122b.billing.model.data;

public class MovieInCart {
    private Integer userId;
    private Long movieId;
    private Integer quantity;


    public Integer getUserId() {
        return userId;
    }

    public MovieInCart setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public Long getMovieId() {
        return movieId;
    }

    public MovieInCart setMovieId(Long movieId) {
        this.movieId = movieId;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public MovieInCart setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }
}
