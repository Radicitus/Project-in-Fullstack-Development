package com.github.klefstad_teaching.cs122b.movies.rest;

import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.movies.model.data.Person;
import com.github.klefstad_teaching.cs122b.movies.model.request.PersonSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.model.response.PersonSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.repo.MovieRepo;
import com.github.klefstad_teaching.cs122b.movies.util.Validate;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@RestController
public class PersonController
{
    private final MovieRepo repo;
    private final Validate validate;

    @Autowired
    public PersonController(MovieRepo repo, Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }

    @GetMapping("/person/search")
    public ResponseEntity<PersonSearchResponse> personSearch(
            PersonSearchRequest request,
            @AuthenticationPrincipal SignedJWT user) throws ParseException {

        // Request validation
        validate.isValidOrderBy(request);
        validate.isValidDirection(request);
        validate.isValidLimit(request);
        validate.isValidOffset(request);

        // Get matching people from db
        List<Person> personResults = repo.personSearch(request, user);

        PersonSearchResponse psr = new PersonSearchResponse();
        if (personResults.size() < 1) {
            psr.setPersons(null)
                    .setResult(MoviesResults.NO_PERSONS_FOUND_WITHIN_SEARCH);
        } else {
            psr.setPersons(personResults)
                    .setResult(MoviesResults.PERSONS_FOUND_WITHIN_SEARCH);
        }

        return psr.toResponse();
    }

    @GetMapping("/person/{personID}")
    public ResponseEntity<PersonSearchResponse> personSearch(
            @PathVariable Long personID,
            @AuthenticationPrincipal SignedJWT user) throws ParseException {

        // Get matching people from db
        List<Person> personResults = repo.personByID(personID, user);

        PersonSearchResponse psr = new PersonSearchResponse();
        if (personResults.size() < 1) {
            psr.setPerson(null)
                    .setResult(MoviesResults.NO_PERSON_WITH_ID_FOUND);
        } else {
            psr.setPerson(personResults)
                    .setResult(MoviesResults.PERSON_WITH_ID_FOUND);
        }

        return psr.toResponse();
    }

}
