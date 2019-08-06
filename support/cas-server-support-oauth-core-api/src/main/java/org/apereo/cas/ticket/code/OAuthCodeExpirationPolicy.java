package org.apereo.cas.ticket.code;

import org.apereo.cas.ticket.expiration.MultiTimeUseOrTimeoutExpirationPolicy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * This is {@link OAuthCodeExpirationPolicy}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OAuthCodeExpirationPolicy extends MultiTimeUseOrTimeoutExpirationPolicy {
    private static final long serialVersionUID = -8383186621682727360L;

    /**
     * Instantiates a new OAuth code expiration policy.
     *
     * @param numberOfUses             the number of uses
     * @param timeToKillInMilliSeconds the time to kill in milli seconds
     */
    public OAuthCodeExpirationPolicy(@JsonProperty("numberOfUses") final long numberOfUses,
                                     @JsonProperty("timeToLive") final long timeToKillInMilliSeconds) {
        super(numberOfUses, timeToKillInMilliSeconds);
    }
}
