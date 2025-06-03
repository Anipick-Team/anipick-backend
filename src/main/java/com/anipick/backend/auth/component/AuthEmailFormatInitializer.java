package com.anipick.backend.auth.component;

import com.anipick.backend.auth.domain.EmailDefaults;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthEmailFormatInitializer {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public MimeMessage initEmail(String mail, String number) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject(EmailDefaults.EMAIL_VERIFICATION_SUBJECT);

        StringBuilder body = new StringBuilder();
        body.append(EmailDefaults.EMAIL_VERIFICATION_NUMBER_MESSAGE);
        body.append(EmailDefaults.H1_NUMBER_FORMAT.formatted(number));
        body.append(EmailDefaults.LAST_MESSAGE);

        message.setText(body.toString(), "utf-8", "html");

        return message;
    }
}
