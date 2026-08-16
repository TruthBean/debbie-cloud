/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mail;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Mail sender. Handles SMTP session creation and email sending.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class MailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailSender.class);

    private final MailConfiguration configuration;
    private Session session;

    public MailSender(MailConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Initialize the Jakarta Mail Session.
     */
    public void init() {
        var props = new Properties();
        props.put("mail.transport.protocol", configuration.getProtocol());
        props.put("mail." + configuration.getProtocol() + ".host", configuration.getHost());
        props.put("mail." + configuration.getProtocol() + ".port", String.valueOf(configuration.getPort()));
        props.put("mail." + configuration.getProtocol() + ".connectiontimeout", String.valueOf(configuration.getConnectionTimeout()));
        props.put("mail." + configuration.getProtocol() + ".timeout", String.valueOf(configuration.getTimeout()));
        props.put("mail." + configuration.getProtocol() + ".writetimeout", String.valueOf(configuration.getWriteTimeout()));

        if (configuration.isAuth()) {
            props.put("mail." + configuration.getProtocol() + ".auth", "true");
        }

        if (configuration.isSsl()) {
            props.put("mail." + configuration.getProtocol() + ".ssl.enable", "true");
            props.put("mail." + configuration.getProtocol() + ".socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail." + configuration.getProtocol() + ".socketFactory.fallback", "false");
            props.put("mail." + configuration.getProtocol() + ".socketFactory.port", String.valueOf(configuration.getPort()));
        }

        if (configuration.isStarttls()) {
            props.put("mail." + configuration.getProtocol() + ".starttls.enable", "true");
        }

        if (configuration.hasCredentials()) {
            session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(configuration.getUsername(), configuration.getPassword());
                }
            });
        } else {
            session = Session.getInstance(props);
        }

        LOGGER.info("Mail sender initialized: " + configuration.getHost() + ":" + configuration.getPort()
                + ", protocol=" + configuration.getProtocol());
    }

    /**
     * Send a simple text email.
     */
    public void send(SimpleMailMessage message) throws MessagingException {
        var mimeMessage = createMimeMessage(message);
        Transport.send(mimeMessage);
        LOGGER.info("Mail sent to " + message.getTo() + ", subject=" + message.getSubject());
    }

    /**
     * Send a raw MimeMessage.
     */
    public void send(MimeMessage mimeMessage) throws MessagingException {
        Transport.send(mimeMessage);
        LOGGER.info("MimeMessage sent");
    }

    /**
     * Send multiple simple emails.
     */
    public void send(SimpleMailMessage... messages) throws MessagingException {
        for (var message : messages) {
            send(message);
        }
    }

    /**
     * Create a MimeMessage from a SimpleMailMessage.
     */
    public MimeMessage createMimeMessage(SimpleMailMessage simpleMessage) throws MessagingException {
        var mimeMessage = new MimeMessage(session);

        var from = simpleMessage.getFrom();
        if (from == null || from.isBlank()) {
            from = configuration.getDefaultFrom();
        }
        if (from != null && !from.isBlank()) {
            var personal = simpleMessage.getFromPersonal();
            if (personal == null || personal.isBlank()) {
                personal = configuration.getDefaultFromPersonal();
            }
            if (personal != null && !personal.isBlank()) {
                try {
                    mimeMessage.setFrom(new InternetAddress(from, personal, configuration.getDefaultEncoding()));
                } catch (java.io.UnsupportedEncodingException e) {
                    mimeMessage.setFrom(new InternetAddress(from));
                }
            } else {
                mimeMessage.setFrom(new InternetAddress(from));
            }
        }

        if (!simpleMessage.getTo().isEmpty()) {
            mimeMessage.setRecipients(Message.RecipientType.TO, toAddresses(simpleMessage.getTo()));
        }
        if (!simpleMessage.getCc().isEmpty()) {
            mimeMessage.setRecipients(Message.RecipientType.CC, toAddresses(simpleMessage.getCc()));
        }
        if (!simpleMessage.getBcc().isEmpty()) {
            mimeMessage.setRecipients(Message.RecipientType.BCC, toAddresses(simpleMessage.getBcc()));
        }

        if (simpleMessage.getReplyTo() != null && !simpleMessage.getReplyTo().isBlank()) {
            mimeMessage.setReplyTo(new InternetAddress[]{new InternetAddress(simpleMessage.getReplyTo())});
        }

        if (simpleMessage.getSubject() != null) {
            mimeMessage.setSubject(simpleMessage.getSubject(), configuration.getDefaultEncoding());
        }

        if (simpleMessage.getText() != null) {
            mimeMessage.setText(simpleMessage.getText(), configuration.getDefaultEncoding());
        }

        mimeMessage.saveChanges();
        return mimeMessage;
    }

    /**
     * Create an empty MimeMessage.
     */
    public MimeMessage createMimeMessage() {
        return new MimeMessage(session);
    }

    private InternetAddress[] toAddresses(List<String> addresses) throws MessagingException {
        var list = new ArrayList<InternetAddress>();
        for (var addr : addresses) {
            list.add(new InternetAddress(addr));
        }
        return list.toArray(new InternetAddress[0]);
    }

    public MailConfiguration getConfiguration() {
        return configuration;
    }

    public Session getSession() {
        return session;
    }

    public void close() {
        LOGGER.info("Mail sender closed");
    }
}