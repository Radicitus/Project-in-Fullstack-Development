package com.github.klefstad_teaching.cs122b.movies.model.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.movies.model.data.*;

import java.util.List;

public class MovieByIDResponse extends ResponseModel<MovieByIDResponse> {
    private MovieDetail movie;
    private List<Genre> genres;
    private List<SimplePerson> persons;

    public MovieDetail getMovie() {
        return movie;
    }

    public MovieByIDResponse setMovie(MovieDetail movie) {
        this.movie = movie;
        return this;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public MovieByIDResponse setGenres(List<Genre> genres) {
        this.genres = genres;
        return this;
    }

    public List<SimplePerson> getPersons() {
        return persons;
    }
    public MovieByIDResponse setPersons(List<SimplePerson> persons) {
        this.persons = persons;
        return this;
    }

}
