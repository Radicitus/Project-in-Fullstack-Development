package com.github.klefstad_teaching.cs122b.movies.model.data;

public final class PersonOrderBy {

    private PersonOrderBy() {
        throw new AssertionError("No instances allowed.");
    }

    public static final String order_by_name_asc = " ORDER BY name ASC, p.id ASC";
    public static final String order_by_name_desc = " ORDER BY name DESC, p.id ASC";
    public static final String order_by_pop_asc = " ORDER BY popularity ASC, p.id ASC";
    public static final String order_by_pop_desc = " ORDER BY popularity DESC, p.id ASC";
    public static final String order_by_bday_asc = " ORDER BY birthday ASC, p.id ASC";
    public static final String order_by_bday_desc = " ORDER BY birthday DESC, p.id ASC";

}
