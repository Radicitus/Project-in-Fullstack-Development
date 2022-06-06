package com.github.klefstad_teaching.cs122b.movies.model.data;

public final class MovieOrderBy {

    private MovieOrderBy() {
        throw new AssertionError("No instances allowed.");
    }

    public static final String order_by_title_asc = " ORDER BY title ASC, m.id ASC";
    public static final String order_by_title_desc = " ORDER BY title DESC, m.id ASC";
    public static final String order_by_year_asc = " ORDER BY year ASC, m.id ASC";
    public static final String order_by_year_desc = " ORDER BY year DESC, m.id ASC";
    public static final String order_by_rating_asc = " ORDER BY rating ASC, m.id ASC";
    public static final String order_by_rating_desc = " ORDER BY rating DESC, m.id ASC";

}
