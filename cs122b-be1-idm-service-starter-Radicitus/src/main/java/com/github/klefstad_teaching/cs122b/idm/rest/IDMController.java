package com.github.klefstad_teaching.cs122b.idm.rest;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.component.IDMAuthenticationManager;
import com.github.klefstad_teaching.cs122b.idm.component.IDMJwtManager;
import com.github.klefstad_teaching.cs122b.idm.model.request.AuthRequest;
import com.github.klefstad_teaching.cs122b.idm.model.request.BasicRequest;
import com.github.klefstad_teaching.cs122b.idm.model.request.RefreshRequest;
import com.github.klefstad_teaching.cs122b.idm.model.response.TokenResponse;
import com.github.klefstad_teaching.cs122b.idm.model.response.BasicResponse;
import com.github.klefstad_teaching.cs122b.idm.repo.IDMRepo;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.util.Validate;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
public class IDMController
{
    private final IDMAuthenticationManager authManager;
    private final IDMJwtManager            jwtManager;
    private final Validate                 validate;
    private final IDMRepo repo;

    @Autowired
    public IDMController(IDMAuthenticationManager authManager,
                         IDMJwtManager jwtManager,
                         Validate validate,
                         NamedParameterJdbcTemplate template,
                         IDMRepo repo)
    {
        this.authManager = authManager;
        this.jwtManager = jwtManager;
        this.validate = validate;
        this.repo = repo;
    }

    @PostMapping("/register")
    public ResponseEntity<BasicResponse> register(
            @RequestBody BasicRequest request
    ) {

        // Email/Pass/User Validation
        if (!validate.isValidEmailLength(request.getEmail())) {
            throw new ResultError(IDMResults.EMAIL_ADDRESS_HAS_INVALID_LENGTH);
        }

        if (!validate.isValidEmailFormat(request.getEmail())) {
            throw new ResultError(IDMResults.EMAIL_ADDRESS_HAS_INVALID_FORMAT);
        }

        if (!validate.isValidPasswordLength(request.getPassword())) {
            throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_LENGTH_REQUIREMENTS);
        }

        if (!validate.isValidPasswordFormat(request.getPassword())) {
            throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_CHARACTER_REQUIREMENT);
        }

        if (validate.isRegisteredUser(request.getEmail())) {
            throw new ResultError(IDMResults.USER_ALREADY_EXISTS);
        }

        // Add user to DB
        authManager.createAndInsertUser(request.getEmail(), request.getPassword());

        BasicResponse br = new BasicResponse()
                .setResult(IDMResults.USER_REGISTERED_SUCCESSFULLY);

        return br.toResponse();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> logjn(
            @RequestBody BasicRequest request
    ) throws JOSEException {

        // Email/Pass/User Validation
        if (!validate.isValidEmailLength(request.getEmail())) {
            throw new ResultError(IDMResults.EMAIL_ADDRESS_HAS_INVALID_LENGTH);
        }

        if (!validate.isValidEmailFormat(request.getEmail())) {
            throw new ResultError(IDMResults.EMAIL_ADDRESS_HAS_INVALID_FORMAT);
        }

        if (!validate.isValidPasswordLength(request.getPassword())) {
            throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_LENGTH_REQUIREMENTS);
        }

        if (!validate.isValidPasswordFormat(request.getPassword())) {
            throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_CHARACTER_REQUIREMENT);
        }

        if (!validate.isRegisteredUser(request.getEmail())) {
            throw new ResultError(IDMResults.USER_NOT_FOUND);
        }

        if (validate.isUserLocked(request.getEmail())) {
            throw new ResultError(IDMResults.USER_IS_LOCKED);
        }

        if (validate.isUserBanned(request.getEmail())) {
            throw new ResultError(IDMResults.USER_IS_BANNED);
        }

        // Authenticate user
        User user = authManager.selectAndAuthenticateUser(request.getEmail(), request.getPassword());

        String accessToken = jwtManager.buildAccessToken(user);
        RefreshToken refreshToken = jwtManager.buildRefreshToken(user);

        // Save refreshToken in db
        authManager.insertRefreshToken(refreshToken);

        TokenResponse tr = new TokenResponse()
                .setResult(IDMResults.USER_LOGGED_IN_SUCCESSFULLY)
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken);

        return tr.toResponse();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<BasicResponse> auth(
            @RequestBody AuthRequest request
    ) throws BadJOSEException, ParseException, JOSEException {
        jwtManager.verifyAccessToken(request.getAccessToken());

        BasicResponse br = new BasicResponse()
                .setResult(IDMResults.ACCESS_TOKEN_IS_VALID);

        return br.toResponse();
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @RequestBody RefreshRequest request
    ) throws JOSEException {

        // Refresh token validation
        validate.isRefreshTokenValidLength(request.getRefreshToken());
        validate.isRefreshTokenValidFormat(request.getRefreshToken());
        validate.isRefreshTokenFound(request.getRefreshToken());
        validate.isRefreshTokenRevoked(request.getRefreshToken());
        validate.isRefreshTokenExpired(request.getRefreshToken());

        // Get refreshToken
        RefreshToken token = repo.getTokenFromDB(request.getRefreshToken()).get(0);
        // Get user from refreshToken
        User user = repo.getUserFromRefreshToken(token);
        // Build new accessToken
        String accessToken = jwtManager.buildAccessToken(user);
        // Get refreshToken (new if cannot be renewed, old if renewed)
        RefreshToken refreshToken = jwtManager.renewRefreshToken(token, user);

        TokenResponse tr = new TokenResponse()
                .setResult(IDMResults.RENEWED_FROM_REFRESH_TOKEN)
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken);

        return tr.toResponse();
    }
}
