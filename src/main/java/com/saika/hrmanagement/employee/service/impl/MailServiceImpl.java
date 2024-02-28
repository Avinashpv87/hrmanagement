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
package com.saika.hrmanagement.employee.service.impl;


import com.saika.hrmanagement.common.entity.EmployeeTimesheet;
import com.saika.hrmanagement.common.model.ApplicationMailContract;
import com.saika.hrmanagement.common.util.MailContentBuilder;
import com.saika.hrmanagement.employee.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;


/**
 * @author Mani
 *
 */
@Service
public class MailServiceImpl implements MailService {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Autowired
    private JavaMailSender mailSender;

	@Autowired
	private MailContentBuilder mailContentBuilder;
	
	@Override
	public void sendCustomMail(final ApplicationMailContract appContract) {
		LOG.info("starts sendCustomMail ...");
		MimeMessage message = mailSender.createMimeMessage();
    	MimeMessageHelper helper = new MimeMessageHelper(message);
    	try {
			helper.setFrom(appContract.getFromEmail());
			helper.setTo(appContract.getSendTo());
			if(Objects.nonNull(appContract.getBccEmail())){
				helper.setBcc(appContract.getBccEmail().split(";"));
			}
			helper.setSubject(appContract.getSubject());
			String html = mailContentBuilder.buildEmailTemplate(appContract);
			helper.setText(html, true);
			//helper.addAttachment();
			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
			LOG.error("MessagingException sendCustomMail ... {} ", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception sendCustomMail ... {} ", e.getMessage());
		} 
    	
    	LOG.info("ends sendCustomMail ...");
	}

	@Override
	public void sendCustomMailWithAttachment(final ApplicationMailContract appContract, final EmployeeTimesheet employeeTimesheet) throws MessagingException {
		LOG.info("starts sendCustomMail ...");
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
				StandardCharsets.UTF_8.name());
		try {
			helper.setFrom(appContract.getFromEmail());
			helper.setTo(appContract.getSendTo());
			helper.setCc(appContract.getCcEmail());
			helper.setSubject(appContract.getSubject());
			String html = mailContentBuilder.buildEmailTemplate(appContract);
			helper.setText(html, true);

			if (!CollectionUtils.isEmpty(employeeTimesheet.getEmployeeTimesheetDocument())) {
				employeeTimesheet.getEmployeeTimesheetDocument().forEach( file -> {
					try {
						helper.addAttachment(file.getFileName(), new ByteArrayResource(file.getFileContent()));
					} catch (MessagingException e) {
						e.printStackTrace();
					}
				});
			}


			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
			LOG.error("MessagingException sendCustomMail ... {} ", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception sendCustomMail ... {} ", e.getMessage());
		}

		LOG.info("ends sendCustomMail ...");
	}

}
