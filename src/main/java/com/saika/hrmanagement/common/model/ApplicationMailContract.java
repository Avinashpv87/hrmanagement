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
package com.saika.hrmanagement.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Mani
 *
 */

public class ApplicationMailContract extends GenericMailContract implements Serializable {

	private String firstName;
	
	private String lastName;
	
	private String fromEmail;

	private String ccEmail;

	private String bccEmail;

	private String subject;
	
	
	private String htmlTemplate;
	
	Map<String, Object> emailContent;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public String getCcEmail() {
		return ccEmail;
	}

	public void setCcEmail(String ccEmail) {
		this.ccEmail = ccEmail;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getHtmlTemplate() {
		return htmlTemplate;
	}

	public void setHtmlTemplate(String htmlTemplate) {
		this.htmlTemplate = htmlTemplate;
	}

	public Map<String, Object> getEmailContent() {
		return emailContent;
	}

	public void setEmailContent(Map<String, Object> emailContent) {
		this.emailContent = emailContent;
	}

	public String getBccEmail() {
		return bccEmail;
	}

	public void setBccEmail(String bccEmail) {
		this.bccEmail = bccEmail;
	}
}
