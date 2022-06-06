package com.github.klefstad_teaching.cs122b.movies.model.data;

public class SimplePerson {

    private Integer ID;
    private String name;


    public Integer getID() {
        return ID;
    }

    public SimplePerson setID(Integer ID) {
        this.ID = ID;
        return this;
    }

    public String getName() {
        return name;
    }

    public SimplePerson setName(String name) {
        this.name = name;
        return this;
    }

}
