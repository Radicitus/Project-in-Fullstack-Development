package com.github.klefstad_teaching.cs122b.idm.util;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.repo.IDMRepo;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.UserStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public final class Validate

{

    private final NamedParameterJdbcTemplate template;
    private final IDMRepo repo;

    public Validate(NamedParameterJdbcTemplate template, IDMRepo repo) {
        this.template = template;
        this.repo = repo;
    }

    public Boolean isValidEmailLength(String email) {
        return email.length() >= 6 && email.length() <= 32;
    }

    public Boolean isValidEmailFormat(String email) {
        Pattern p = Pattern.compile("[A-Za-z0-9]+@[A-Za-z0-9]+\\.[A-Za-z0-9]+");
        Matcher m = p.matcher(email);

        return m.find();
    }

    public Boolean isValidPasswordLength(char[] password) {
        return password.length >= 10 && password.length <= 20;
    }

    public Boolean isValidPasswordFormat(char[] password) {

        Pattern p = Pattern.compile("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])");
        Matcher m = p.matcher(Arrays.toString(password));

        return m.find();
    }

    public Boolean isRegisteredUser(String email) {
        List<User> users =
                this.template.query(
                        "SELECT * " +
                                "FROM idm.user " +
                                "WHERE email = :email;",

                        new MapSqlParameterSource()
                                .addValue("email", email, Types.VARCHAR),

                        (rs, rowNum) ->
                                new User()
                                        .setId(rs.getInt("id"))
                                        .setEmail(rs.getString("email"))
                                        .setUserStatus(UserStatus.fromId(rs.getInt("user_status_id")))
                                        .setSalt(rs.getString("salt"))
                                        .setHashedPassword("hashed_password")

                );

        return users.size() > 0;
    }

    public Boolean isUserLocked(String email) {
        List<User> user = this.template.query(
                "SELECT id, email, user_status_id, salt, hashed_password " +
                        "FROM idm.user " +
                        "WHERE email = :email;",

                new MapSqlParameterSource()
                        .addValue("email", email, Types.VARCHAR),

                (rs, rowNum) ->
                        new User()
                                .setId(rs.getInt("id"))
                                .setEmail(rs.getString("email"))
                                .setUserStatus(UserStatus.fromId(rs.getInt("user_status_id")))
                                .setSalt(rs.getString("salt"))
                                .setHashedPassword("hashed_password")

        );

        return user.get(0).getUserStatus() == UserStatus.LOCKED;

    }

    public Boolean isUserBanned(String email) {
        List<User> user = this.template.query(
                "SELECT id, email, user_status_id, salt, hashed_password " +
                        "FROM idm.user " +
                        "WHERE email = :email;",

                new MapSqlParameterSource()
                        .addValue("email", email, Types.VARCHAR),

                (rs, rowNum) ->
                        new User()
                                .setId(rs.getInt("id"))
                                .setEmail(rs.getString("email"))
                                .setUserStatus(UserStatus.fromId(rs.getInt("user_status_id")))
                                .setSalt(rs.getString("salt"))
                                .setHashedPassword("hashed_password")

        );

        return user.get(0).getUserStatus() == UserStatus.BANNED;
    }

    public void isRefreshTokenExpired(String refreshToken) {
        RefreshToken token = repo.getTokenFromDB(refreshToken).get(0);
        Instant time = Instant.now();
        if (token.getTokenStatus() == TokenStatus.EXPIRED ||
                Instant.now().isAfter(token.getExpireTime()) ||
                Instant.now().isAfter(token.getMaxLifeTime())) {
            repo.setRefreshTokenToExpired(refreshToken);
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_EXPIRED);
        }
    }

    public void isRefreshTokenRevoked(String refreshToken) {
        if (repo.getTokenFromDB(refreshToken).get(0).getTokenStatus() == TokenStatus.REVOKED) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_REVOKED);
        }
    }

    public void isRefreshTokenFound(String refreshToken) {
        if (repo.getTokenFromDB(refreshToken).size() < 1) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_NOT_FOUND);
        }
    }

    public void isRefreshTokenValidLength(String refreshToken) {
        if (refreshToken.length() != 36) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_HAS_INVALID_LENGTH);
        }
    }

    public void isRefreshTokenValidFormat(String refreshToken) {
        try {
            UUID refreshTokenUUID = UUID.fromString(String.valueOf(refreshToken));
        } catch (Exception e) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_HAS_INVALID_FORMAT);
        }
    }
}
