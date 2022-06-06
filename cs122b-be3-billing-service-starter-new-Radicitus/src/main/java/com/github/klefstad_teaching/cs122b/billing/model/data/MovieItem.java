package com.github.klefstad_teaching.cs122b.billing.model.data;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MovieItem {

    private BigDecimal unitPrice;
    private Integer quantity;
    private Long movieId;
    private String movieTitle;
    private String backdropPath;
    private String posterPath;


    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public MovieItem setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice.setScale(2, RoundingMode.DOWN);
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public MovieItem setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public Long getMovieId() {
        return movieId;
    }

    public MovieItem setMovieId(Long movieId) {
        this.movieId = movieId;
        return this;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public MovieItem setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
        return this;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public MovieItem setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
        return this;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public MovieItem setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        return this;
    }

}
