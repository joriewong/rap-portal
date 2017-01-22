package com.rap.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

public class AuthorizationRequestFilter implements ContainerRequestFilter {
	
	@Context   
    private HttpServletRequest request;  
	
	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		// TODO Auto-generated method stub
		if (request.getRequestURI().toString().endsWith("/login")) {
			return;
		}
		if (request.getSession() == null) {
			requestContext.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("Please login!!!")
                    .build());
		}
	}

}
