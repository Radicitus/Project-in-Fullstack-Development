package com.github.klefstad_teaching.cs122b.movies.model.request;

import java.util.Objects;

public class MovieSearchRequest {

    private String title;
    private Integer year;
    private String director;
    private String genre;
    private Integer limit = 10;
    private Integer page = 1;
    private String orderBy = "title";
    private String direction = "asc";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (!title.equals("")) {
            this.title = title;
        }
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        if (!director.equals("")) {
            this.director = director;
        }
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        if (!genre.equals("")) {
            this.genre = genre;
        }
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

}
