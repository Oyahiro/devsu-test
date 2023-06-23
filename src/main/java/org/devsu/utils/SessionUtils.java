package org.devsu.utils;

import org.devsu.common.Constants;
import org.devsu.common.Exceptions;
import org.devsu.dto.PrimaryUser;
import org.devsu.entity.Client;
import org.devsu.enums.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SessionUtils {

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static PrimaryUser getPrimaryUser() {
        Authentication authentication = getAuthentication();
        return Objects.nonNull(authentication) && !authentication.getPrincipal().equals("anonymousUser")
                ? (PrimaryUser) authentication.getPrincipal() : null;
    }

    public static Map<String, Object> getSessionMap() {
        final PrimaryUser primaryUser = getPrimaryUser();
        return (Objects.nonNull(primaryUser))
                ? primaryUser.getProperties() : new HashMap<>();
    }

    public static <T> T getValueFromSession(String key) {
        Map<String, Object> sessionValues = getSessionMap();
        return (Objects.nonNull(sessionValues))
                ? (T) sessionValues.get(key) : null;
    }

    public static void verifyPermissions(Client client) {
        Client sessionUser = SessionUtils.getValueFromSession(Constants.CURRENT_USER);

        if (Objects.isNull(sessionUser) || Objects.isNull(sessionUser.getRoles())) {
            throw new Exceptions.UnauthorizedException("There is an error with the user stored in session");
        }

        if(sessionUser.getRoles().contains(Role.ROLE_ADMIN.toString())) {
            return;
        }

        if (!Objects.equals(client.getUsername(), sessionUser.getUsername())) {
            throw new Exceptions.UnauthorizedException("The current user does not have permissions to execute this action.");
        }
    }
}
