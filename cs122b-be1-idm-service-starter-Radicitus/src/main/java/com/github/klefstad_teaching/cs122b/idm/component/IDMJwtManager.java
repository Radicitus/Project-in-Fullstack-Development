package com.github.klefstad_teaching.cs122b.idm.component;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.idm.config.IDMServiceConfig;
import com.github.klefstad_teaching.cs122b.idm.repo.IDMRepo;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class IDMJwtManager
{
    private final JWTManager jwtManager;
    private final IDMRepo repo;

    @Autowired
    public IDMJwtManager(IDMServiceConfig serviceConfig, IDMRepo repo)
    {
        this.jwtManager =
            new JWTManager.Builder()
                .keyFileName(serviceConfig.keyFileName())
                .accessTokenExpire(serviceConfig.accessTokenExpire())
                .maxRefreshTokenLifeTime(serviceConfig.maxRefreshTokenLifeTime())
                .refreshTokenExpire(serviceConfig.refreshTokenExpire())
                .build();

        this.repo = repo;
    }

    private SignedJWT buildAndSignJWT(JWTClaimsSet claimsSet)
        throws JOSEException
    {
        JWSHeader header =
                new JWSHeader.Builder(JWTManager.JWS_ALGORITHM)
                        .keyID(jwtManager.getEcKey().getKeyID())
                        .type(JWTManager.JWS_TYPE)
                        .build();

        SignedJWT signedJWT = new SignedJWT(header, claimsSet);

        signedJWT.sign(jwtManager.getSigner());

        return signedJWT;
    }

    private void verifyJWT(SignedJWT jwt)
            throws ParseException {

        try {

            jwt.verify(jwtManager.getVerifier());
            jwtManager.getJwtProcessor().process(jwt, null);

            if (Instant.now().isAfter(jwt.getJWTClaimsSet().getExpirationTime().toInstant())) {
                throw new ResultError(IDMResults.ACCESS_TOKEN_IS_EXPIRED);
            }

        } catch (IllegalStateException | JOSEException | BadJOSEException e) {
            throw new ResultError(IDMResults.ACCESS_TOKEN_IS_INVALID);
        }
    }

    public String buildAccessToken(User user) throws JOSEException {
        JWTClaimsSet claimsSet =
                new JWTClaimsSet.Builder()
                        .subject(user.getEmail())
                        .expirationTime(
                                Date.from(
                                        Instant.now().plus(jwtManager.getAccessTokenExpire())))
                        .issueTime(Date.from(Instant.now()))
                        .claim(JWTManager.CLAIM_ROLES, user.getRoles())
                        .claim(JWTManager.CLAIM_ID, user.getId())    // we set claims like values in a map
                        .build();

        return buildAndSignJWT(claimsSet).serialize();
    }

    public void verifyAccessToken(String jws) throws ParseException, BadJOSEException, JOSEException {
        SignedJWT jwt = SignedJWT.parse(jws);
        verifyJWT(jwt);
    }

    public RefreshToken buildRefreshToken(User user)
    {

        return new RefreshToken()
                .setToken(generateUUID())
                .setTokenStatus(TokenStatus.ACTIVE)
                .setExpireTime(Instant.now().plus(jwtManager.getRefreshTokenExpire()))
                .setUserId(user.getId())
                .setMaxLifeTime(Instant.now().plus(jwtManager.getMaxRefreshTokenLifeTime()));
    }

    public boolean hasExpired(RefreshToken refreshToken)
    {
        return Instant.now().isAfter(refreshToken.getMaxLifeTime());
    }

    public boolean needsRefresh(RefreshToken refreshToken)
    {
        return Instant.now().isAfter(refreshToken.getExpireTime());
    }

    public void updateRefreshTokenExpireTime(RefreshToken refreshToken)
    {
        refreshToken.setExpireTime(Instant.now().plus(jwtManager.getRefreshTokenExpire()));
    }

    private String generateUUID()
    {
        return UUID.randomUUID().toString();
    }

    public RefreshToken renewRefreshToken(RefreshToken refreshToken, User user) {

        updateRefreshTokenExpireTime(refreshToken);

        // Check if expireTime past token's maxLifeTime
        if (refreshToken.getExpireTime().isAfter(refreshToken.getMaxLifeTime())) {

            // Revoke token if past maxLifeTime and build new one
            repo.setRefreshTokenToRevoked(refreshToken.getToken());
            RefreshToken newRefreshToken = buildRefreshToken(user);
            // Upload new token to DB and return it
            repo.insertNewRefreshToken(newRefreshToken);
            return newRefreshToken;
        } else {
            repo.updateRefreshTokenExpireTime(refreshToken);
            return refreshToken;
        }
    }
}
