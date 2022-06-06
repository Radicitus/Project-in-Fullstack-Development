package com.github.klefstad_teaching.cs122b.movies.util;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.movies.model.request.MovieSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.model.request.PersonSearchRequest;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class Validate
{

    public void isValidOrderBy(MovieSearchRequest request) {
        if (!request.getOrderBy().matches("(?i)title|rating|year")) {
            throw new ResultError(MoviesResults.INVALID_ORDER_BY);
        }
    }

    public void isValidOrderBy(PersonSearchRequest request) {
        if (!request.getOrderBy().matches("(?i)name|popularity|birthday")) {
            throw new ResultError(MoviesResults.INVALID_ORDER_BY);
        }
    }

    public void isValidDirection(MovieSearchRequest request) {
        if (!request.getDirection().matches("(?i)asc|desc")) {
            throw new ResultError(MoviesResults.INVALID_DIRECTION);
        }
    }

    public void isValidDirection(PersonSearchRequest request) {
        if (!request.getDirection().matches("(?i)asc|desc")) {
            throw new ResultError(MoviesResults.INVALID_DIRECTION);
        }
    }

    public void isValidLimit(MovieSearchRequest request) {

        Set<Integer> limits = new HashSet<>();
        limits.add(10);
        limits.add(25);
        limits.add(50);
        limits.add(100);

        if (!limits.contains(request.getLimit())) {
            throw new ResultError(MoviesResults.INVALID_LIMIT);
        }
    }

    public void isValidLimit(PersonSearchRequest request) {

        Set<Integer> limits = new HashSet<>();
        limits.add(10);
        limits.add(25);
        limits.add(50);
        limits.add(100);

        if (!limits.contains(request.getLimit())) {
            throw new ResultError(MoviesResults.INVALID_LIMIT);
        }
    }

    public void isValidOffset(MovieSearchRequest request) {
        if (request.getPage() < 1) {
            throw new ResultError(MoviesResults.INVALID_PAGE);
        }
    }

    public void isValidOffset(PersonSearchRequest request) {
        if (request.getPage() < 1) {
            throw new ResultError(MoviesResults.INVALID_PAGE);
        }
    }

}
