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
package com.saika.hrmanagement.common.util;

import com.saika.hrmanagement.common.exception.CustomApplicationException;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Mani
 *
 */
@Component
public class JwtTokenUtil implements Serializable {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Value("${app.jwt.secret}")
	private String secret;

	@Value("${app.jwt.tokenValidityInMs}")
	private int tokenValidityInMs;
	@Value("${app.jwt.emailExpiryInMs}")
	private int jwtEmailExpInMs;
	
	@Value("${app.jwt.userActiveExpiryInMs}")
	private String userActiveExpiryInMs;
	

	// retrieve username from jwt token
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	// retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	// for retrieveing any information from token we will need the secret key
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	// check if the token has expired
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	// generate token for user
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, userDetails.getUsername());
	}

	// while creating the token -
	// 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
	// 2. Sign the JWT using the HS512 algorithm and secret key.
	// 3. According to JWS Compact
	// Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	// compaction of the JWT to a URL-safe string
	private String doGenerateToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + tokenValidityInMs * 1000L))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	// validate token
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
	public String generateEmailJwtToken() {
		log.info(" Generate JWT Token for Email Starts...");
		Date now = new Date();
		Date currentDate = new Date(now.getTime());
		Date expiryDate = new Date(now.getTime() + jwtEmailExpInMs);
		String jwt = Jwts.builder()
				.setIssuer("application")
				.setSubject("reset")
			    .setIssuedAt(currentDate)
			    .setExpiration(expiryDate)
			    .signWith(SignatureAlgorithm.HS256, secret)
			    .compact();
		log.info(" Generate JWT Token for Email ends ... {} ",jwt);
		return jwt;
	}
	
	public String generateActiveUserToken() {
		log.info(" Generate JWT Token for Active User Starts...");
		Date now = new Date();
		Date currentDate = new Date(now.getTime());
		Long timeInMs = Long.parseLong(userActiveExpiryInMs);
		Date expiryDate = new Date(now.getTime() + timeInMs);
		String jwt = Jwts.builder()
				.setIssuer("application")
				.setSubject("active")
			    .setIssuedAt(currentDate)
			    .setExpiration(expiryDate)
			    .signWith(SignatureAlgorithm.HS256, secret)
			    .compact();
		log.info(" Generate JWT Token for Active user ends ... {} ",jwt);
		return jwt;
	}
	
	public boolean validateToken(String authToken)  {
		try {
			Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
			return true;
		} catch (ArrayIndexOutOfBoundsException ex) {
			log.error("ArrayIndexOutOfBoundsException");
			throw new CustomApplicationException(HttpStatus.NOT_ACCEPTABLE, "Invalid JWT - ArrayIndexOutOfBoundsException!");
		} catch (SignatureException ex) {
			log.error("SignatureException");
			throw new CustomApplicationException(HttpStatus.NOT_ACCEPTABLE, "SignatureException, Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			log.error("Invalid JWT token");
			throw new CustomApplicationException(HttpStatus.NOT_ACCEPTABLE, "MalformedJwtException, Invalid JWT token");
		}  catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token");
			throw new CustomApplicationException(HttpStatus.NOT_ACCEPTABLE, "Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty.");
			throw new CustomApplicationException(HttpStatus.NOT_ACCEPTABLE, "IllegalArgumentException, JWT claims string is empty.");
		}
	}
}
