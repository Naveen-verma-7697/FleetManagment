/*package com.fleman.security;

public class AuthenticatedUser {
	 public static Long getCurrentUserId() {

	        Object principal =
	                org.springframework.security.core.context.SecurityContextHolder
	                        .getContext()
	                        .getAuthentication()
	                        .getPrincipal();

	        return (Long) principal;
	    } 
}*/

package com.fleman.security;

import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticatedUser {

    public static UserPrincipal getCurrentUser() {

    	 if (SecurityContextHolder.getContext().getAuthentication() == null) {
    	        throw new RuntimeException("No authenticated user found.");
    	    }

    	    return (UserPrincipal) SecurityContextHolder
    	            .getContext()
    	            .getAuthentication()
    	            .getPrincipal();
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    public static String getCurrentRole() {
        return getCurrentUser().getRole();
    }

    public static String getCurrentEmail() {
        return getCurrentUser().getEmail();
    }
    
    
    public static boolean isAuthenticated() {

        var authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserPrincipal;
    }
}
