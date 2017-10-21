/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.mail.javamail;

import javax.mail.internet.MimeMessage;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Roland Weisleder
 */
public class MimeMailMessageTests {

	@Test
	public void testSetMultipleReplyTo() throws Exception {
		MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();
		MimeMailMessage mimeMailMessage = new MimeMailMessage(mimeMessage);
		mimeMailMessage.setReplyTo(new String[]{"you@mail.org", "us@mail.org"});
		assertEquals("you@mail.org", mimeMessage.getReplyTo()[0].toString());
		assertEquals("us@mail.org", mimeMessage.getReplyTo()[1].toString());
	}
}
