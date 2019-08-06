package org.apereo.cas.authorization;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.LdapEntry;
import org.ldaptive.SearchExecutor;
import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.profile.UserProfile;

import java.util.Optional;

/**
 * Provides a simple {@link AuthorizationGenerator} implementation that obtains user roles from an LDAP search.
 * Searches are performed by this component for every user details lookup:
 * <ol>
 * <li>Search for an entry to resolve the username. In most cases the search should return exactly one result,
 * but the {@link #isAllowMultipleResults()} property may be toggled to change that behavior.</li>
 * </ol>
 *
 * @author Jerome Leleu
 * @author Marvin S. Addison
 * @author Misagh Moayyed
 * @since 4.0.0
 */
@Slf4j
public class LdapUserAttributesToRolesAuthorizationGenerator extends BaseUseAttributesAuthorizationGenerator {

    private final String roleAttribute;
    private final String rolePrefix;

    /**
     * Creates a new instance with the given required parameters.
     *
     * @param factory              Source of LDAP connections for searches.
     * @param userSearchExecutor   Executes the LDAP search for user data.
     * @param allowMultipleResults allow multiple search results in which case the first result
     *                             returned is used to construct user details, or false to indicate that
     *                             a runtime exception should be raised on multiple search results for user details.
     * @param roleAttribute        the role attribute
     * @param rolePrefix           the role prefix
     */
    public LdapUserAttributesToRolesAuthorizationGenerator(final ConnectionFactory factory,
                                                           final SearchExecutor userSearchExecutor,
                                                           final boolean allowMultipleResults,
                                                           final String roleAttribute,
                                                           final String rolePrefix) {
        super(factory, userSearchExecutor, allowMultipleResults);
        this.roleAttribute = roleAttribute;
        this.rolePrefix = rolePrefix;
    }

    @Override
    protected Optional<UserProfile> generateAuthorizationForLdapEntry(final UserProfile profile, final LdapEntry userEntry) {
        if (!userEntry.getAttributes().isEmpty()) {
            val attribute = userEntry.getAttribute(this.roleAttribute);
            if (attribute != null) {
                addProfileRoles(userEntry, profile, attribute, this.rolePrefix);
            } else {
                LOGGER.warn("Configured role attribute cannot be found for this user");
            }
        } else {
            LOGGER.warn("No attributes are retrieved for this user.");
        }
        return Optional.ofNullable(profile);
    }
}
