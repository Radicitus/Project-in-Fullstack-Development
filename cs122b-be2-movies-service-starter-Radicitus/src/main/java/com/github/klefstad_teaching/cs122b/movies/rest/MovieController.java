package com.github.klefstad_teaching.cs122b.movies.rest;

import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.movies.model.data.Genre;
import com.github.klefstad_teaching.cs122b.movies.model.data.Movie;
import com.github.klefstad_teaching.cs122b.movies.model.data.Person;
import com.github.klefstad_teaching.cs122b.movies.model.request.MovieSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.model.request.PersonSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieByIDResponse;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.repo.MovieRepo;
import com.github.klefstad_teaching.cs122b.movies.util.Validate;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MovieController {
    private final MovieRepo repo;
    private final Validate validate;

    @Autowired
    public MovieController(MovieRepo repo, Validate validate) {
        this.repo = repo;
        this.validate = validate;
    }

    @GetMapping("/movie/search")
    public ResponseEntity<MovieSearchResponse> search(
            MovieSearchRequest request,
            @AuthenticationPrincipal SignedJWT user) throws ParseException {

        // Request validation
        validate.isValidOrderBy(request);
        validate.isValidDirection(request);
        validate.isValidLimit(request);
        validate.isValidOffset(request);

        // Get matching movies from db
        List<Movie> movieResults = repo.movieSearch(request, user);

        MovieSearchResponse msr = new MovieSearchResponse();
        if (movieResults.size() < 1) {
            msr.setMovies(null)
                    .setResult(MoviesResults.NO_MOVIES_FOUND_WITHIN_SEARCH);
        } else {
            msr.setMovies(movieResults)
                    .setResult(MoviesResults.MOVIES_FOUND_WITHIN_SEARCH);
        }

        return msr.toResponse();
    }

    @GetMapping("/movie/search/person/{personID}")
    public ResponseEntity<MovieSearchResponse> movieSearchByPersonID(
            @PathVariable Long personID,
            MovieSearchRequest request,
            @AuthenticationPrincipal SignedJWT user) throws ParseException {

        // Request validation
        validate.isValidOrderBy(request);
        validate.isValidDirection(request);
        validate.isValidLimit(request);
        validate.isValidOffset(request);

        //Get matching movies from db
        List<Movie> movieResults = repo.movieSearchByPersonID(personID, request, user);

        MovieSearchResponse msr = new MovieSearchResponse();
        if (movieResults.size() < 1) {
            msr.setMovies(null)
                    .setResult(MoviesResults.NO_MOVIES_WITH_PERSON_ID_FOUND);
        } else {
            msr.setMovies(movieResults)
                    .setResult(MoviesResults.MOVIES_WITH_PERSON_ID_FOUND);
        }

        return msr.toResponse();
    }

    @GetMapping("/movie/{movieID}")
    public ResponseEntity<MovieByIDResponse> movieByID(
            @PathVariable Long movieID,
            @AuthenticationPrincipal SignedJWT user) throws ParseException {

        MovieByIDResponse mbidr = new MovieByIDResponse();

        // DB
        repo.movieByID(movieID, user, mbidr);

        if (mbidr.getMovie() == null) {
            mbidr.setMovie(null).setGenres(null).setPersons(null)
                    .setResult(MoviesResults.NO_MOVIE_WITH_ID_FOUND);
        } else {
            mbidr.setResult(MoviesResults.MOVIE_WITH_ID_FOUND);
        }
        
        return mbidr.toResponse();
    }

}
