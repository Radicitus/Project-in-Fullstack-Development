package com.github.klefstad_teaching.cs122b.movies.model.data;

import com.fasterxml.jackson.annotation.JsonInclude;

public class Person {

    private Integer ID;
    private String name;
    private String birthday;
    private String biography;
    private String birthplace;
    private Double popularity;
    private String profilePath;


    public Integer getID() {
        return ID;
    }

    public Person setID(Integer ID) {
        this.ID = ID;
        return this;
    }

    public String getName() {
        return name;
    }

    public Person setName(String name) {
        this.name = name;
        return this;
    }

    public String getBirthday() {
        return birthday;
    }

    public Person setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public String getBiography() {
        return biography;
    }

    public Person setBiography(String biography) {
        this.biography = biography;
        return this;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public Person setBirthplace(String birthplace) {
        this.birthplace = birthplace;
        return this;
    }

    public Double getPopularity() {
        return popularity;
    }

    public Person setPopularity(Double popularity) {
        this.popularity = popularity;
        return this;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public Person setProfilePath(String profilePath) {
        this.profilePath = profilePath;
        return this;
    }

}
