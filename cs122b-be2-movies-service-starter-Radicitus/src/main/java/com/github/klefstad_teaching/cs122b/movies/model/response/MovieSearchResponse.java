package com.github.klefstad_teaching.cs122b.movies.model.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.movies.model.data.Genre;
import com.github.klefstad_teaching.cs122b.movies.model.data.Movie;
import com.github.klefstad_teaching.cs122b.movies.model.data.Person;

import java.util.List;

public class MovieSearchResponse extends ResponseModel<MovieSearchResponse> {
    private List<Movie> movies;
    private List<Genre> genres;
    private List<Person> persons;

    public List<Genre> getGenres() {
        return genres;
    }

    public MovieSearchResponse setGenres(List<Genre> genres) {
        this.genres = genres;
        return this;
    }

    public List<Person> getPersons() {
        return persons;
    }
    public MovieSearchResponse setPersons(List<Person> persons) {
        this.persons = persons;
        return this;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public MovieSearchResponse setMovies(List<Movie> movies) {
        this.movies = movies;
        return this;
    }

}
