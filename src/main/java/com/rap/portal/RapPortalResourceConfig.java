package com.rap.portal;

import org.glassfish.jersey.server.ResourceConfig;

import com.rap.filter.AuthorizationRequestFilter;

public class RapPortalResourceConfig extends ResourceConfig{
	
	public RapPortalResourceConfig() {

        packages("com.rap.portal", "com.rap.filter");
        register(AuthorizationRequestFilter.class);
        System.out.println("º”‘ÿrap-portal");
    }
}
