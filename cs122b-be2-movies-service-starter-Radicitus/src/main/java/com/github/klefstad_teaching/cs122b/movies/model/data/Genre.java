package com.github.klefstad_teaching.cs122b.movies.model.data;

public class Genre {

    private Integer ID;
    private String name;


    public Integer getID() {
        return ID;
    }

    public Genre setID(Integer ID) {
        this.ID = ID;
        return this;
    }

    public String getName() {
        return name;
    }

    public Genre setName(String name) {
        this.name = name;
        return this;
    }

}
