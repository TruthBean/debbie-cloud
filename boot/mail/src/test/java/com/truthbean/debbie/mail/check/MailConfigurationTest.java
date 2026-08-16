/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mail.check;

import com.truthbean.debbie.mail.MailConfiguration;
import com.truthbean.debbie.mail.SimpleMailMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class MailConfigurationTest {

    @Test
    public void configurationDefaults() {
        var config = new MailConfiguration();
        assertTrue(config.isEnable());
        assertEquals("localhost", config.getHost());
        assertEquals(25, config.getPort());
        assertEquals("smtp", config.getProtocol());
        assertEquals("UTF-8", config.getDefaultEncoding());
        assertFalse(config.isSsl());
        assertFalse(config.isStarttls());
        assertTrue(config.isAuth());
        assertEquals(5000, config.getConnectionTimeout());
        assertEquals(5000, config.getTimeout());
        assertEquals(5000, config.getWriteTimeout());
        assertNull(config.getUsername());
        assertNull(config.getPassword());
        assertNull(config.getDefaultFrom());
        assertNull(config.getDefaultFromPersonal());
    }

    @Test
    public void configurationHelpers() {
        var config = new MailConfiguration();
        assertFalse(config.hasCredentials());
        assertFalse(config.hasDefaultFrom());
        assertTrue(config.isSmtp());
        assertFalse(config.isSmtps());

        config.setUsername("user@example.com");
        config.setPassword("pass");
        assertTrue(config.hasCredentials());

        config.setDefaultFrom("from@example.com");
        assertTrue(config.hasDefaultFrom());

        config.setProtocol("smtps");
        assertFalse(config.isSmtp());
        assertTrue(config.isSmtps());
    }

    @Test
    public void configurationCopyShouldBeEqual() {
        var config = new MailConfiguration();
        config.setHost("smtp.example.com");
        config.setPort(587);
        config.setUsername("user@example.com");
        config.setPassword("pass");
        config.setProtocol("smtps");
        config.setDefaultEncoding("GBK");
        config.setSsl(true);
        config.setStarttls(true);
        config.setAuth(false);
        config.setConnectionTimeout(10000);
        config.setTimeout(15000);
        config.setWriteTimeout(20000);
        config.setDefaultFrom("from@example.com");
        config.setDefaultFromPersonal("Sender");

        var copy = config.<MailConfiguration>copy();
        assertEquals("smtp.example.com", copy.getHost());
        assertEquals(587, copy.getPort());
        assertEquals("user@example.com", copy.getUsername());
        assertEquals("pass", copy.getPassword());
        assertEquals("smtps", copy.getProtocol());
        assertEquals("GBK", copy.getDefaultEncoding());
        assertTrue(copy.isSsl());
        assertTrue(copy.isStarttls());
        assertFalse(copy.isAuth());
        assertEquals(10000, copy.getConnectionTimeout());
        assertEquals(15000, copy.getTimeout());
        assertEquals(20000, copy.getWriteTimeout());
        assertEquals("from@example.com", copy.getDefaultFrom());
        assertEquals("Sender", copy.getDefaultFromPersonal());
    }

    @Test
    public void configurationCopyShouldNotAffectOriginal() {
        var config = new MailConfiguration();
        config.setHost("original.com");
        var copy = config.<MailConfiguration>copy();
        copy.setHost("copy.com");
        assertEquals("original.com", config.getHost());
        assertEquals("copy.com", copy.getHost());
    }

    @Test
    public void configurationToStringShouldContainInfo() {
        var config = new MailConfiguration();
        config.setHost("smtp.example.com");
        config.setPort(587);
        var str = config.toString();
        assertTrue(str.contains("smtp.example.com"));
        assertTrue(str.contains("587"));
        assertTrue(str.startsWith("MailConfiguration{"));
    }

    @Test
    public void configurationProfileAndCategoryShouldBeDefault() {
        var config = new MailConfiguration();
        assertNotNull(config.getProfile());
        assertNotNull(config.getCategory());
    }

    @Test
    public void simpleMailMessageTest() {
        var msg = new SimpleMailMessage();
        msg.setFrom("from@example.com");
        msg.setTo("to1@example.com", "to2@example.com");
        msg.addCc("cc@example.com");
        msg.addBcc("bcc@example.com");
        msg.setSubject("Test Subject");
        msg.setText("Test Body");
        msg.setReplyTo("reply@example.com");

        assertEquals("from@example.com", msg.getFrom());
        assertEquals(2, msg.getTo().size());
        assertEquals("to1@example.com", msg.getTo().get(0));
        assertEquals("to2@example.com", msg.getTo().get(1));
        assertEquals(1, msg.getCc().size());
        assertEquals("cc@example.com", msg.getCc().get(0));
        assertEquals(1, msg.getBcc().size());
        assertEquals("Test Subject", msg.getSubject());
        assertEquals("Test Body", msg.getText());
        assertEquals("reply@example.com", msg.getReplyTo());

        var str = msg.toString();
        assertTrue(str.contains("from@example.com"));
        assertTrue(str.contains("Test Subject"));
    }
}