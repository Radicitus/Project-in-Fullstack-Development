package com.github.klefstad_teaching.cs122b.movies.model.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.movies.model.data.Person;

import java.util.List;

public class PersonSearchResponse extends ResponseModel<PersonSearchResponse> {
    private List<Person> person;
    private List<Person> persons;


    public List<Person> getPersons() {
        return persons;
    }

    public PersonSearchResponse setPersons(List<Person> persons) {
        this.persons = persons;
        return this;
    }

    public List<Person> getPerson() {
        return person;
    }

    public PersonSearchResponse setPerson(List<Person> person) {
        this.person = person;
        return this;
    }

}
