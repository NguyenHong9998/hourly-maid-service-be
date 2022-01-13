package com.example.hourlymaids.util;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SendSmsUtils {
    @Value("${twilio.username}")
    private String twilioUsername;

    @Value("${twilio.password}")
    private String twilioPass;

    @Value("${twilio.phone}")
    private String twilioPhone;

    public void sendSMS(String toPhone, String message) {
        Twilio.init(
                twilioUsername,
                twilioPass);

        Message.creator(
                new PhoneNumber(toPhone),
                new PhoneNumber(twilioPhone),
                message)
                .create();
    }
}
