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

import com.saika.hrmanagement.common.model.ApplicationMailContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

/**
 * @author Mani
 *
 */
@Service
public class MailContentBuilder {
	
	@Autowired
	private SpringTemplateEngine templateEngine;
	
	public String buildEmailTemplate(final ApplicationMailContract appContract) {
		
		Context context = new Context();
		context.setVariables(appContract.getEmailContent());
		
		return templateEngine.process(appContract.getHtmlTemplate(), context);
	}

}
