package com.example.hourlymaids.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sun.istack.NotNull;

import javax.validation.constraints.Email;
import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SendMailDomain {
    @NotNull
    @Email
    private List<String> toEmail;
    @NotNull
    private String subject;
    @NotNull
    private String messageContent;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String bcc;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String cc;

    /**
     * Gets to email.
     *
     * @return the toEmail
     */
    public List<String> getToEmail() {
        return toEmail;
    }

    /**
     * Sets to email.
     *
     * @param toEmail the toEmail to set
     */
    public void setToEmail(List<String> toEmail) {
        this.toEmail = toEmail;
    }

    /**
     * Gets subject.
     *
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets subject.
     *
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Gets message content.
     *
     * @return the messageContent
     */
    public String getMessageContent() {
        return messageContent;
    }

    /**
     * Sets message content.
     *
     * @param messageContent the messageContent to set
     */
    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    /**
     * Gets bcc.
     *
     * @return the bcc
     */
    public String getBcc() {
        return bcc;
    }

    /**
     * Sets bcc.
     *
     * @param bcc the bcc to set
     */
    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    /**
     * Gets cc.
     *
     * @return the cc
     */
    public String getCc() {
        return cc;
    }

    /**
     * Sets cc.
     *
     * @param cc the cc to set
     */
    public void setCc(String cc) {
        this.cc = cc;
    }
}
