package com.github.klefstad_teaching.cs122b.idm.component;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.idm.repo.IDMRepo;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.UserStatus;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class IDMAuthenticationManager
{
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String       HASH_FUNCTION = "PBKDF2WithHmacSHA512";

    private static final int ITERATIONS     = 10000;
    private static final int KEY_BIT_LENGTH = 512;

    private static final int SALT_BYTE_LENGTH = 4;

    public final IDMRepo repo;

    private final NamedParameterJdbcTemplate template;


    @Autowired
    public IDMAuthenticationManager(IDMRepo repo, NamedParameterJdbcTemplate template)
    {
        this.repo = repo;
        this.template = template;
    }

    private static byte[] hashPassword(final char[] password, String salt)
    {
        return hashPassword(password, Base64.getDecoder().decode(salt));
    }

    private static byte[] hashPassword(final char[] password, final byte[] salt)
    {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(HASH_FUNCTION);

            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_BIT_LENGTH);

            SecretKey key = skf.generateSecret(spec);

            return key.getEncoded();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] genSalt()
    {
        byte[] salt = new byte[SALT_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }

    public User selectAndAuthenticateUser(String email, char[] password) {

        // Get stored hashed_password and salt
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
                                .setHashedPassword(rs.getString("hashed_password"))

        );

        // Get base64 encoded hashed password to check
        byte[] encodedToCheck = hashPassword(password, user.get(0).getSalt());
        String base64EncodedToCheck = Base64.getEncoder().encodeToString(encodedToCheck);

        // Check if stored hash password and password to check are the same
        if (base64EncodedToCheck.equals(user.get(0).getHashedPassword())) {
            return user.get(0);
        } else {
            throw new ResultError(IDMResults.INVALID_CREDENTIALS);
        }

    }

    public void createAndInsertUser(String email, char[] password)
    {

        // Generate salt and create hashed password
        byte[] salt = genSalt();
        String base64EncodedSalt = Base64.getEncoder().encodeToString(salt);
        byte[] encoded = hashPassword(password, salt);
        String base64EncodedPass = Base64.getEncoder().encodeToString(encoded);

        // Store user information in database
        int rowsUpdated = this.template.update(
                "INSERT INTO idm.user (email, user_status_id, salt, hashed_password)" +
                        "VALUES (:email, :user_status_id, :salt, :hashed_password)",
                new MapSqlParameterSource()
                        .addValue("email", email, Types.VARCHAR)
                        .addValue("user_status_id", 1, Types.INTEGER)
                        .addValue("salt", base64EncodedSalt, Types.CHAR)
                        .addValue("hashed_password", base64EncodedPass, Types.CHAR)
        );

        if (rowsUpdated > 0) {
            System.out.println("User upload successful.");
        } else {
            System.out.println("User upload failed.");
        }

    }

    public void insertRefreshToken(RefreshToken refreshToken)
    {
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

    public RefreshToken verifyRefreshToken(String token)
    {
        return null;
    }

    public void updateRefreshTokenExpireTime(RefreshToken token)
    {
    }

    public void expireRefreshToken(RefreshToken token)
    {
    }

    public void revokeRefreshToken(RefreshToken token)
    {
    }

    public User getUserFromRefreshToken(RefreshToken refreshToken)
    {
        return null;
    }
}
