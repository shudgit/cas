package org.apereo.cas.oidc.token;

import org.apereo.cas.oidc.AbstractOidcTests;
import org.apereo.cas.services.DefaultRegisteredServiceProperty;
import org.apereo.cas.services.RegisteredServiceProperty.RegisteredServiceProperties;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.util.EncodingUtils;

import com.nimbusds.jwt.SignedJWT;
import lombok.val;
import org.jose4j.jwk.JsonWebKey;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link OidcRegisteredServiceJwtAccessTokenCipherExecutorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@Tag("OIDC")
public class OidcRegisteredServiceJwtAccessTokenCipherExecutorTests extends AbstractOidcTests {

    @Test
    public void verifyOperationGlobally() throws Exception {
        val service = getOidcRegisteredService("whatever");
        service.setJwks(null);
        service.setClientId(UUID.randomUUID().toString());
        
        assertTrue(oidcRegisteredServiceJwtAccessTokenCipherExecutor.supports(service));
        val at = getAccessToken(service.getClientId());
        val encoded = oidcRegisteredServiceJwtAccessTokenCipherExecutor.encode(at.getId(), Optional.of(service));
        assertNotNull(encoded);
        val header = SignedJWT.parse(encoded).getHeader();
        assertNotNull(header.getAlgorithm());
        val decoded = oidcRegisteredServiceJwtAccessTokenCipherExecutor.decode(encoded, Optional.of(service));
        assertNotNull(decoded);
        assertEquals(at.getId(), decoded);
    }


    @Test
    public void verifyOperationByService() throws Exception {
        val service = getOidcRegisteredService("whatever");
        assertTrue(oidcRegisteredServiceJwtAccessTokenCipherExecutor.supports(service));
        val at = getAccessToken(service.getClientId());
        val encoded = oidcRegisteredServiceJwtAccessTokenCipherExecutor.encode(at.getId(), Optional.of(service));
        assertNotNull(encoded);
        val header = SignedJWT.parse(encoded).getHeader();
        assertNotNull(header.getAlgorithm());
        val decoded = oidcRegisteredServiceJwtAccessTokenCipherExecutor.decode(encoded, Optional.of(service));
        assertNotNull(decoded);
        assertEquals(at.getId(), decoded);
    }

    @Test
    public void verifyNoSigningKey() throws Exception {
        val service = getOidcRegisteredService("whatever");
        service.getProperties().put(RegisteredServiceProperties.ACCESS_TOKEN_AS_JWT_SIGNING_ENABLED.getPropertyName(),
            new DefaultRegisteredServiceProperty(RegisteredServiceProperties.ACCESS_TOKEN_AS_JWT_SIGNING_ENABLED.getDefaultValue()));

        val key = EncodingUtils.generateJsonWebKey(512);
        service.getProperties().put(RegisteredServiceProperties.ACCESS_TOKEN_AS_JWT_SIGNING_KEY.getPropertyName(),
            new DefaultRegisteredServiceProperty(key));
        val at = getAccessToken(service.getClientId());
        val encoded = oidcRegisteredServiceJwtAccessTokenCipherExecutor.encode(at.getId(), Optional.of(service));
        assertNotNull(encoded);
    }

    @Test
    public void verifyEncKey() throws Exception {
        val service = getOidcRegisteredService("whatever");
        service.getProperties().put(RegisteredServiceProperties.ACCESS_TOKEN_AS_JWT_ENCRYPTION_ENABLED.getPropertyName(),
            new DefaultRegisteredServiceProperty("true"));

        val key = EncodingUtils.newJsonWebKey(2048);
        service.getProperties().put(RegisteredServiceProperties.ACCESS_TOKEN_AS_JWT_ENCRYPTION_KEY.getPropertyName(),
            new DefaultRegisteredServiceProperty(key.toJson(JsonWebKey.OutputControlLevel.INCLUDE_PRIVATE)));
        val at = getAccessToken(service.getClientId());
        val encoded = oidcRegisteredServiceJwtAccessTokenCipherExecutor.encode(at.getId(), Optional.of(service));
        assertNotNull(encoded);
    }

    @Test
    public void verifyNoEncKey() throws Exception {
        val service = getOidcRegisteredService("whatever");
        service.getProperties().put(RegisteredServiceProperties.ACCESS_TOKEN_AS_JWT_ENCRYPTION_ENABLED.getPropertyName(),
            new DefaultRegisteredServiceProperty("true"));
        val at = getAccessToken(service.getClientId());
        val encoded = oidcRegisteredServiceJwtAccessTokenCipherExecutor.encode(at.getId(), Optional.of(service));
        assertNotNull(encoded);
    }

    @Test
    public void verifyOAuthService() throws Exception {
        val service = getOAuthRegisteredService(UUID.randomUUID().toString(), RegisteredServiceTestUtils.CONST_TEST_URL);
        service.getProperties().put(RegisteredServiceProperties.ACCESS_TOKEN_AS_JWT_ENCRYPTION_ENABLED.getPropertyName(),
            new DefaultRegisteredServiceProperty("true"));
        val at = getAccessToken(service.getClientId());
        val encoded = oidcRegisteredServiceJwtAccessTokenCipherExecutor.encode(at.getId(), Optional.of(service));
        assertNotNull(encoded);
    }
}
