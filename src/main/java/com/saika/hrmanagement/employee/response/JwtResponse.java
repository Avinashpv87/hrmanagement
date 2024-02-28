/**
 * 
 */
package com.saika.hrmanagement.employee.response;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * @author Mani
 *
 */
@Data
public class JwtResponse implements Serializable {


	private String firstName;

	private String lastName;

	private String phoneNumber;

	private String accessToken;

	private String tokenType;

	private Collection<? extends GrantedAuthority> roles;

	private LocalDateTime createdOn;

	private LocalDateTime LastModified;

	private LocalDateTime lastLoginDate;

}
