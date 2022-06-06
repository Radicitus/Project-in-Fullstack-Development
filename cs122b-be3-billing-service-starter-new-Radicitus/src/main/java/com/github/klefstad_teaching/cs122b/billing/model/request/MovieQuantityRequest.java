package com.github.klefstad_teaching.cs122b.billing.model.request;

public class MovieQuantityRequest {
    private Long movieId;
    private Integer quantity;


    public Long getMovieId() {
        return movieId;
    }

    public MovieQuantityRequest setMovieId(Long movieId) {
        this.movieId = movieId;
        return null;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public MovieQuantityRequest setQuantity(Integer quantity) {
        this.quantity = quantity;
        return null;
    }
}
