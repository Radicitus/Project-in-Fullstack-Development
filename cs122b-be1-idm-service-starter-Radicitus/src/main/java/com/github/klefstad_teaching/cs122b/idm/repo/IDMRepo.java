package com.github.klefstad_teaching.cs122b.idm.repo;

import com.github.klefstad_teaching.cs122b.idm.component.IDMJwtManager;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Ref;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.List;

@Component
public class IDMRepo
{

    private final NamedParameterJdbcTemplate template;

    @Autowired
    public IDMRepo(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    public List<RefreshToken> getTokenFromDB(String refreshToken) {
        List<RefreshToken> tokens =
                this.template.query(
                        "SELECT * " +
                                "FROM idm.refresh_token " +
                                "WHERE token = :token;",

                        new MapSqlParameterSource()
                                .addValue("token", refreshToken, Types.CHAR),

                        (rs, rowNum) ->
                                new RefreshToken()
                                        .setId(rs.getInt("id"))
                                        .setToken(rs.getString("token"))
                                        .setUserId(rs.getInt("user_id"))
                                        .setTokenStatus(TokenStatus.fromId(rs.getInt("token_status_id")))
                                        .setExpireTime(rs.getTimestamp("expire_time").toInstant())
                                        .setMaxLifeTime(rs.getTimestamp("max_life_time").toInstant())

                );
        return tokens;
    }

    public void setRefreshTokenToExpired(String refreshToken) {
        String sql =
                "UPDATE idm.refresh_token " +
                "SET token_status_id = 2 " +
                "WHERE token = :token";

        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("token", refreshToken, Types.CHAR);

        int rowUpdated = this.template.update(sql, source);
    }

    public void setRefreshTokenToRevoked(String refreshToken) {
        String sql =
                "UPDATE idm.refresh_token " +
                        "SET token_status_id = 3 " +
                        "WHERE token = :token";

        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("token", refreshToken, Types.CHAR);

        int rowUpdated = this.template.update(sql, source);
    }

    public User getUserFromRefreshToken(RefreshToken refreshToken) {
        List<User> user = this.template.query(
                "SELECT id, email, user_status_id, salt, hashed_password " +
                        "FROM idm.user " +
                        "WHERE id = :id;",

                new MapSqlParameterSource()
                        .addValue("id", refreshToken.getUserId(), Types.INTEGER),

                (rs, rowNum) ->
                        new User()
                                .setId(rs.getInt("id"))
                                .setEmail(rs.getString("email"))
                                .setUserStatus(UserStatus.fromId(rs.getInt("user_status_id")))
                                .setSalt(rs.getString("salt"))
                                .setHashedPassword("hashed_password")

        );

        return user.get(0);
    }

    public void updateRefreshTokenExpireTime(RefreshToken refreshToken) {
        String sql =
                "UPDATE idm.refresh_token " +
                        "SET expire_time = :expire_time " +
                        "WHERE token = :token";

        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("token", refreshToken, Types.CHAR)
                .addValue("expire_time", Timestamp.from(refreshToken.getExpireTime()), Types.TIMESTAMP);

        int rowUpdated = this.template.update(sql, source);
    }

    public void insertNewRefreshToken(RefreshToken refreshToken) {
        int rowsUpdated = this.template.update(
                "INSERT INTO idm.refresh_token (token, user_id, token_status_id, expire_time, max_life_time) " +
                        "VALUES (:token, :user_id, :token_status_id, :expire_time, :max_life_time)",
                new MapSqlParameterSource()
                        .addValue("token", refreshToken.getToken(), Types.CHAR)
                        .addValue("user_id", refreshToken.getUserId(), Types.INTEGER)
                        .addValue("token_status_id", refreshToken.getTokenStatus().id(), Types.INTEGER)
                        .addValue("expire_time", Timestamp.from(refreshToken.getExpireTime()), Types.TIMESTAMP)
                        .addValue("max_life_time", Timestamp.from(refreshToken.getMaxLifeTime()), Types.TIMESTAMP)
        );

        if (rowsUpdated > 0) {
            System.out.println("Refresh token uploaded.");
        } else {
            System.out.println("Refresh token upload failed.");
        }
    }
}
