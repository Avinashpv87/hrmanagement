/*
 * Copyright 2022 the original author or authors.
 * Licensed under the Saika Technologies Inc License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.saika.com/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.saika.hrmanagement.common.filters;

import com.saika.hrmanagement.common.util.JwtTokenUtil;
import com.saika.hrmanagement.employee.service.impl.JwtUserDetailsServiceImpl;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Mani
 *
 */
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUserDetailsServiceImpl userDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String requestTokenHeader = request.getHeader("Authorization");
		String username = null;
		String jwtToken = null;
		// JWT Token is in the form "Bearer token". Remove Bearer word and get
		// only the Token
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			} catch (ArrayIndexOutOfBoundsException ex) {
				logger.error("ArrayIndexOutOfBoundsException");
				//throw new CustomApplicationException(HttpStatus.NOT_ACCEPTABLE, "Invalid JWT ArrayIndexOutOfBoundsException!");
			} catch (SignatureException e) {
				logger.error("Invalid JWT signature: {}");
				//throw new CustomApplicationException(HttpStatus.NOT_ACCEPTABLE, "Invalid JWT  - SignatureException!");
			} catch (MalformedJwtException e) {
				logger.error("Invalid JWT token: {}");
				//throw new CustomApplicationException(HttpStatus.NOT_ACCEPTABLE, "Invalid JWT  - MalformedJwtException!");
			} catch (IllegalArgumentException e) {
				logger.error("Unable to get JWT Token");
				//throw new CustomApplicationException(HttpStatus.NOT_ACCEPTABLE, "Invalid JWT  - IllegalArgumentException!");
			} catch (UnsupportedJwtException e) {
				logger.error("JWT token is unsupported: {}");
				//throw new CustomApplicationException(HttpStatus.NOT_ACCEPTABLE, "Invalid JWT  - UnsupportedJwtException!");
			}
		} else {
			logger.warn("JWT Token does not begin with Bearer String");
			//throw new CustomApplicationException(HttpStatus.UNAUTHORIZED, "Token Missing or JWT Token does not begin with Bearer String!");
		}
		// Once we get the token validate it.
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
			// if token is valid configure Spring Security to manually set
			// authentication
			if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				// After setting the Authentication in the context, we specify
				// that the current user is authenticated. So it passes the
				// Spring Security Configurations successfully.
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		filterChain.doFilter(request, response);
	}

}
