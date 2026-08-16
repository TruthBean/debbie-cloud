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

import java.util.ArrayList;
import java.util.List;

/**
 * Simple mail message (plain text, no attachments).
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class SimpleMailMessage {

    private String from;
    private String fromPersonal;
    private List<String> to = new ArrayList<>();
    private List<String> cc = new ArrayList<>();
    private List<String> bcc = new ArrayList<>();
    private String subject;
    private String text;
    private String replyTo;

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getFromPersonal() { return fromPersonal; }
    public void setFromPersonal(String fromPersonal) { this.fromPersonal = fromPersonal; }

    public List<String> getTo() { return to; }
    public void setTo(List<String> to) { this.to = to; }
    public void setTo(String... to) {
        this.to = new ArrayList<>();
        for (var t : to) this.to.add(t);
    }
    public void addTo(String to) { this.to.add(to); }

    public List<String> getCc() { return cc; }
    public void setCc(List<String> cc) { this.cc = cc; }
    public void setCc(String... cc) {
        this.cc = new ArrayList<>();
        for (var c : cc) this.cc.add(c);
    }
    public void addCc(String cc) { this.cc.add(cc); }

    public List<String> getBcc() { return bcc; }
    public void setBcc(List<String> bcc) { this.bcc = bcc; }
    public void setBcc(String... bcc) {
        this.bcc = new ArrayList<>();
        for (var b : bcc) this.bcc.add(b);
    }
    public void addBcc(String bcc) { this.bcc.add(bcc); }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getReplyTo() { return replyTo; }
    public void setReplyTo(String replyTo) { this.replyTo = replyTo; }

    @Override
    public String toString() {
        return "SimpleMailMessage{from=" + from
                + ", to=" + to
                + ", subject=" + subject + "}";
    }
}