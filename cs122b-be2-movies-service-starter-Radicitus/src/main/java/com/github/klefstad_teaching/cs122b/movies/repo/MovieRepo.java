package com.github.klefstad_teaching.cs122b.movies.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.movies.model.data.*;
import com.github.klefstad_teaching.cs122b.movies.model.request.MovieSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.model.request.PersonSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieByIDResponse;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieSearchResponse;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.text.ParseException;
import java.util.List;

@Component
public class MovieRepo {

    private final ObjectMapper objectMapper;
    private final NamedParameterJdbcTemplate template;

    private final String movie_search_base =
            "SELECT m.id, m.title, m.year, m.director_id, m.rating, " +
                "m.num_votes, m.backdrop_path, m.poster_path, m.hidden, dir.name " +
            "FROM movies.movie m " +
            "JOIN movies.person dir ON dir.id = m.director_id ";

    private final String movie_search_genre =
            movie_search_base +
            "JOIN movies.movie_genre mg ON mg.movie_id = m.id " +
            "JOIN movies.genre g ON mg.genre_id = g.id ";

    private final String movie_search_personid =
            movie_search_base +
            "JOIN movies.movie_person mp ON m.id = mp.movie_id " +
            "JOIN movies.person p ON mp.person_id = p.id ";

    private final String movie_base =
            "SELECT m.id, m.title, m.year, m.director_id, m.rating, m.num_votes, m.budget, " +
                    "m.revenue, m.overview, m.backdrop_path, m.poster_path, m.hidden, dir.name " +
                    "FROM movies.movie m " +
                    "JOIN movies.person dir ON dir.id = m.director_id ";

    private final String movie_genre =
            "SELECT g.id, g.name " +
            "FROM movies.movie m " +
            "JOIN movies.movie_genre mg ON mg.movie_id = m.id " +
            "JOIN movies.genre g ON mg.genre_id = g.id ";

    private final String person_search_base =
            "SELECT DISTINCT p.id, p.name, p.birthday, p.biography, p.birthplace, " +
                    "p.popularity, p.profile_path " +
            "FROM movies.person p ";

    private final String person_search_movie_title =
            "SELECT DISTINCT p.id, p.name, p.birthday, p.biography, p.birthplace, " +
                    "p.popularity, p.profile_path, m.title " +
            "FROM movies.person p " +
            "JOIN movies.movie_person mp ON p.id = mp.person_id " +
            "JOIN movies.movie m ON m.id = mp.movie_id ";

    private final String person_search_json =
            "SELECT DISTINCT p.id, p.name, p.popularity " +
            "FROM movies.person p " +
            "JOIN movies.movie_person mp ON p.id = mp.person_id " +
            "JOIN movies.movie m ON m.id = mp.movie_id ";

    @Autowired
    public MovieRepo(ObjectMapper objectMapper, NamedParameterJdbcTemplate template) {
        this.objectMapper = objectMapper;
        this.template = template;
    }

    public List<Movie> movieSearch(MovieSearchRequest request, SignedJWT user) throws ParseException {

        StringBuilder sql;
        MapSqlParameterSource source = new MapSqlParameterSource();
        boolean whereAdded = false;
        List<String> userRoles = user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);

        // Genre
        if (request.getGenre() != null) {
            sql = new StringBuilder(movie_search_genre);

            sql.append(" WHERE g.name LIKE :genre ");
            String wildcardSearch = "%" + request.getGenre() + "%";
            source.addValue("genre", wildcardSearch, Types.VARCHAR);
            whereAdded = true;
        } else {
            sql = new StringBuilder(movie_search_base);
        }

        // Director
        if (request.getDirector() != null) {
            if (whereAdded) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                whereAdded = true;
            }

            sql.append(" dir.name LIKE :director ");
            String wildcardSearch = "%" + request.getDirector() + "%";
            source.addValue("director", wildcardSearch, Types.VARCHAR);
        }

        // Title
        if (request.getTitle() != null) {
            if (whereAdded) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                whereAdded = true;
            }

            sql.append(" title LIKE :title ");
            String wildcardSearch = "%" + request.getTitle() + "%";
            source.addValue("title", wildcardSearch, Types.VARCHAR);
        }

        // Year
        if (request.getYear() != null) {
            if (whereAdded) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                whereAdded = true;
            }

            sql.append(" year = :year ");
            source.addValue("year", request.getYear(), Types.INTEGER);
        }

        // Hidden
        if (!(userRoles.contains("ADMIN") || userRoles.contains("EMPLOYEE"))) {
            if (whereAdded) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                whereAdded = true;
            }

            sql.append(" m.hidden != TRUE");
        }

        // OrderBy and Direction
        if (request.getOrderBy().equals("title")) {
            if (request.getDirection().equals("asc")) {
                sql.append(MovieOrderBy.order_by_title_asc);
            } else {
                sql.append(MovieOrderBy.order_by_title_desc);
            }
        }

        if (request.getOrderBy().equals("year")) {
            if (request.getDirection().equals("asc")) {
                sql.append(MovieOrderBy.order_by_year_asc);
            } else {
                sql.append(MovieOrderBy.order_by_year_desc);
            }
        }

        if (request.getOrderBy().equals("rating")) {
            if (request.getDirection().equals("asc")) {
                sql.append(MovieOrderBy.order_by_rating_asc);
            } else {
                sql.append(MovieOrderBy.order_by_rating_desc);
            }
        }

        // Limit
        sql.append(" LIMIT :limit ");
        source.addValue("limit", request.getLimit(), Types.INTEGER);

        // Page
        sql.append(" OFFSET :offset ");
        Integer offset = request.getLimit() * (request.getPage() - 1);
        source.addValue("offset", offset, Types.INTEGER);

        List<Movie> movies = this.template.query(
                sql.toString(),
                source,
                (rs, rowNum) ->
                        new Movie()
                                .setId(rs.getInt("id"))
                                .setTitle(rs.getString("title"))
                                .setYear(rs.getInt("year"))
                                .setDirector(rs.getString("name"))
                                .setRating(rs.getDouble("rating"))
                                .setBackdropPath(rs.getString("backdrop_path"))
                                .setPosterPath(rs.getString("poster_path"))
                                .setHidden(rs.getBoolean("hidden"))
        );

        return movies;
    }

    public List<Movie> movieSearchByPersonID(Long personID, MovieSearchRequest request, SignedJWT user) throws ParseException {

        StringBuilder sql = new StringBuilder(movie_search_personid);
        MapSqlParameterSource source = new MapSqlParameterSource();
        List<String> userRoles = user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);

        // Person ID
        sql.append(" WHERE p.id = :personID ");
        source.addValue("personID", personID, Types.BIGINT);

        // Hidden
        if (!(userRoles.contains("ADMIN") || userRoles.contains("EMPLOYEE"))) {
            sql.append(" AND ");
            sql.append(" m.hidden != TRUE");
        }

        // OrderBy and Direction
        if (request.getOrderBy().equals("title")) {
            if (request.getDirection().equals("asc")) {
                sql.append(MovieOrderBy.order_by_title_asc);
            } else {
                sql.append(MovieOrderBy.order_by_title_desc);
            }
        }

        if (request.getOrderBy().equals("year")) {
            if (request.getDirection().equals("asc")) {
                sql.append(MovieOrderBy.order_by_year_asc);
            } else {
                sql.append(MovieOrderBy.order_by_year_desc);
            }
        }

        if (request.getOrderBy().equals("rating")) {
            if (request.getDirection().equals("asc")) {
                sql.append(MovieOrderBy.order_by_rating_asc);
            } else {
                sql.append(MovieOrderBy.order_by_rating_desc);
            }
        }

        // Limit
        sql.append(" LIMIT :limit ");
        source.addValue("limit", request.getLimit(), Types.INTEGER);

        // Page
        sql.append(" OFFSET :offset ");
        Integer offset = request.getLimit() * (request.getPage() - 1);
        source.addValue("offset", offset, Types.INTEGER);

        List<Movie> movies = this.template.query(
                sql.toString(),
                source,
                (rs, rowNum) ->
                        new Movie()
                                .setId(rs.getInt("id"))
                                .setTitle(rs.getString("title"))
                                .setYear(rs.getInt("year"))
                                .setDirector(rs.getString("name"))
                                .setRating(rs.getDouble("rating"))
                                .setBackdropPath(rs.getString("backdrop_path"))
                                .setPosterPath(rs.getString("poster_path"))
                                .setHidden(rs.getBoolean("hidden"))
        );

        return movies;
    }

    public void movieByID(Long movieID, SignedJWT user, MovieByIDResponse mbidr) throws ParseException {

        List<String> userRoles = user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);

        // Movie
        StringBuilder sql_movie = new StringBuilder(movie_base);
        MapSqlParameterSource source_movie = new MapSqlParameterSource();

        sql_movie.append(" WHERE m.id = :id ");
        source_movie.addValue("id", movieID, Types.BIGINT);

        // Hidden
        if (!(userRoles.contains("ADMIN") || userRoles.contains("EMPLOYEE"))) {
            sql_movie.append("AND m.hidden != TRUE");
        }

        List<MovieDetail> movies = this.template.query(
                sql_movie.toString(),
                source_movie,
                (rs, rowNum) ->
                        new MovieDetail()
                                .setId(rs.getLong("id"))
                                .setTitle(rs.getString("title"))
                                .setYear(rs.getInt("year"))
                                .setDirector(rs.getString("name"))
                                .setRating(rs.getDouble("rating"))
                                .setNumVotes(rs.getLong("num_votes"))
                                .setBudget(rs.getLong("budget"))
                                .setRevenue(rs.getLong("revenue"))
                                .setOverview(rs.getString("overview"))
                                .setBackdropPath(rs.getString("backdrop_path"))
                                .setPosterPath(rs.getString("poster_path"))
                                .setHidden(rs.getBoolean("hidden"))
        );
        if (movies.size() < 1) {
            mbidr.setMovie(null);
        } else {
            mbidr.setMovie(movies.get(0));
        }

        // Genre
        StringBuilder sql_genre = new StringBuilder(movie_genre);
        MapSqlParameterSource source_genre = new MapSqlParameterSource();

        sql_genre.append(" WHERE m.id = :id ");
        source_genre.addValue("id", movieID, Types.BIGINT);
        sql_genre.append(" ORDER BY name ");

        List<Genre> genres = this.template.query(
                sql_genre.toString(),
                source_genre,
                (rs, rowNum) ->
                        new Genre()
                                .setID(rs.getInt("id"))
                                .setName(rs.getString("name"))
        );

        mbidr.setGenres(genres);

        // Persons
        StringBuilder sql_persons = new StringBuilder(person_search_json);
        MapSqlParameterSource source_persons = new MapSqlParameterSource();

        sql_persons.append(" WHERE m.id = :id ");
        source_persons.addValue("id", movieID, Types.BIGINT);
        sql_persons.append(" ORDER BY p.popularity DESC, p.id ASC ");

        List<SimplePerson> persons = this.template.query(
                sql_persons.toString(),
                source_persons,
                (rs, rowNum) ->
                        new SimplePerson()
                                .setID(rs.getInt("id"))
                                .setName(rs.getString("name"))
        );

        mbidr.setPersons(persons);
    }

    public List<Person> personSearch(PersonSearchRequest request, SignedJWT user) throws ParseException {
        StringBuilder sql;
        MapSqlParameterSource source = new MapSqlParameterSource();
        boolean whereAdded = false;
        List<String> userRoles = user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);

        // Movie Title
        if (request.getMovieTitle() != null) {
            sql = new StringBuilder(person_search_movie_title);

            sql.append(" WHERE m.title LIKE :title ");
            String wildcardSearch = "%" + request.getMovieTitle() + "%";
            source.addValue("title", wildcardSearch, Types.VARCHAR);
            whereAdded = true;
        } else {
            sql = new StringBuilder(person_search_base);
        }

        // Name
        if (request.getName() != null) {
            if (whereAdded) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                whereAdded = true;
            }

            sql.append(" p.name LIKE :name ");
            String wildcardSearch = "%" + request.getName() + "%";
            source.addValue("name", wildcardSearch, Types.VARCHAR);
        }

        // Birthday
        if (request.getBirthday() != null) {
            if (whereAdded) {
                sql.append(" AND ");
            } else {
                sql.append(" WHERE ");
                whereAdded = true;
            }

            sql.append(" p.birthday LIKE :birthday ");
            source.addValue("birthday", request.getBirthday(), Types.VARCHAR);
        }

        // OrderBy and Direction
        if (request.getOrderBy().equals("name")) {
            if (request.getDirection().equals("asc")) {
                sql.append(PersonOrderBy.order_by_name_asc);
            } else {
                sql.append(PersonOrderBy.order_by_name_desc);
            }
        }

        if (request.getOrderBy().equals("popularity")) {
            if (request.getDirection().equals("asc")) {
                sql.append(PersonOrderBy.order_by_pop_asc);
            } else {
                sql.append(PersonOrderBy.order_by_pop_desc);
            }
        }

        if (request.getOrderBy().equals("birthday")) {
            if (request.getDirection().equals("asc")) {
                sql.append(PersonOrderBy.order_by_bday_asc);
            } else {
                sql.append(PersonOrderBy.order_by_bday_desc);
            }
        }

        // Limit
        sql.append(" LIMIT :limit ");
        source.addValue("limit", request.getLimit(), Types.INTEGER);

        // Page
        sql.append(" OFFSET :offset ");
        Integer offset = request.getLimit() * (request.getPage() - 1);
        source.addValue("offset", offset, Types.INTEGER);

        List<Person> persons = this.template.query(
                sql.toString(),
                source,
                (rs, rowNum) ->
                        new Person()
                                .setID(rs.getInt("id"))
                                .setName(rs.getString("name"))
                                .setBirthday(rs.getString("birthday"))
                                .setBiography(rs.getString("biography"))
                                .setBirthplace(rs.getString("birthplace"))
                                .setPopularity(rs.getDouble("popularity"))
                                .setProfilePath(rs.getString("profile_path"))
        );

        return persons;
    }

    public List<Person> personByID(Long personID, SignedJWT user) throws ParseException {
        StringBuilder sql = new StringBuilder(person_search_base);
        MapSqlParameterSource source = new MapSqlParameterSource();
        List<String> userRoles = user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);

        // Person
        sql.append(" WHERE p.id = :personID ");
        source.addValue("personID", personID, Types.BIGINT);

        List<Person> persons = this.template.query(
                sql.toString(),
                source,
                (rs, rowNum) ->
                        new Person()
                                .setID(rs.getInt("id"))
                                .setName(rs.getString("name"))
                                .setBirthday(rs.getString("birthday"))
                                .setBiography(rs.getString("biography"))
                                .setBirthplace(rs.getString("birthplace"))
                                .setPopularity(rs.getDouble("popularity"))
                                .setProfilePath(rs.getString("profile_path"))
        );

        return persons;
    }
}
