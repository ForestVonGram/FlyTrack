package com.flytrack.service.impl;

import com.flytrack.service.MailService;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {
    private final boolean enableEmail = true;
    @Value("${mail.smtp.host}")
    private String host;

    @Value("${mail.smtp.port}")
    private int port;

    @Value("${mail.smtp.username}")
    private String username;

    @Value("${mail.smtp.password}")
    private String password;

    Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    private Mailer getMailer() {
        return MailerBuilder
                .withSMTPServer(host, port, username, password)
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withSessionTimeout(10 * 1000)
                .buildMailer();
    }

    @Override
    // Email simple
    public void sendSimpleEmail(String to, String subject, String text) {
        String htmlContent = """
                <html>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; margin: 0;">
                    <div style="max-width: 600px; margin: auto; background: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                        <div style="background-color: #1A237E; padding: 20px; text-align: center;">
                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; letter-spacing: 1px;">FlyTrack</h1>
                        </div>
                        <div style="padding: 30px; text-align: left;">
                            <h2 style="color: #0D1642; margin-top: 0; font-size: 20px; border-bottom: 2px solid #3949AB; padding-bottom: 10px;">%s</h2>
                            <p style="color: #333333; font-size: 16px; line-height: 1.6; white-space: pre-wrap;">%s</p>
                        </div>
                        <div style="background-color: #3949AB; padding: 15px; text-align: center;">
                            <p style="color: #e0e0e0; margin: 0; font-size: 12px;">&copy; 2026 FlyTrack. Todos los derechos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(subject, text);

        if(!enableEmail) {
            log.info("[EMAIL DISABLED] To: {} | Subject: {} | Body: {}", to, subject, text);
            return;
        }
        try{
            log.info("Enviando email a: {} con asunto: {}", to, subject);
            var email = EmailBuilder.startingBlank()
                    .from("FlyTrack", username)
                    .to(to)
                    .withSubject(subject)
                    .withHTMLText(htmlContent)
                    .buildEmail();

            getMailer().sendMail(email);
        }catch (Exception e){
            log.error("Error al registrar el envío de email: {}", e.getMessage());
        }

    }

    @Override
    // Email HTML personalizado
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        if(!enableEmail) {
            log.info("[EMAIL DISABLED] To: {} | Subject: {} | HTML Body: {}", to, subject, htmlContent);
            return;
        }

        var email = EmailBuilder.startingBlank()
                .from("FlyTrack", username)
                .to(to)
                .withSubject(subject)
                .withHTMLText(htmlContent)
                .buildEmail();

        getMailer().sendMail(email);
    }


    @Override
    // Email con nombre personalizado del destinatario
    public void sendPersonalizedEmail(String toName, String toEmail, String subject, String htmlContent) {
        if(!enableEmail) {
            log.info("[EMAIL DISABLED] To: {} <{}> | Subject: {} | HTML Body: {}", toName, toEmail, subject, htmlContent);
            return;
        }
        var email = EmailBuilder.startingBlank()
                .from("FlyTrack", username)
                .to(toName, toEmail)
                .withSubject(subject)
                .withHTMLText(htmlContent)
                .buildEmail();

        getMailer().sendMail(email);
    }

    @Override
    public void sendVerificationEmail(String toEmail, String verificationLink) {
        String subject = "Verify your email - FlyTrack";
        String htmlContent = """
                <html>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; margin: 0;">
                    <div style="max-width: 600px; margin: auto; background: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                        <div style="background-color: #1A237E; padding: 20px; text-align: center;">
                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; letter-spacing: 1px;">FlyTrack</h1>
                        </div>
                        <div style="padding: 30px; text-align: center;">
                            <h2 style="color: #0D1642; margin-top: 0; border-bottom: 2px solid #3949AB; padding-bottom: 10px; display: inline-block;">Welcome to FlyTrack!</h2>
                            <p style="color: #333333; font-size: 16px; margin-top: 20px;">Please verify your email address by clicking the button below:</p>
                            <div style="margin: 30px 0;">
                                <a href="%s" style="background-color: #3949AB; color: white; padding: 14px 28px; text-decoration: none; border-radius: 5px; font-size: 16px; font-weight: bold; display: inline-block;">
                                    Verify Email
                                </a>
                            </div>
                            <p style="color: #666666; font-size: 13px;">If you did not create an account, please ignore this email.</p>
                            <p style="color: #666666; font-size: 13px;">This link will expire in 15 minutes.</p>
                        </div>
                        <div style="background-color: #3949AB; padding: 15px; text-align: center;">
                            <p style="color: #e0e0e0; margin: 0; font-size: 12px;">&copy; 2026 FlyTrack. Todos los derechos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(verificationLink);

        if (!enableEmail) {
            log.info("[EMAIL DISABLED] To: {} | Subject: {} | Verification Link: {}", toEmail, subject, verificationLink);
            return;
        }

        try {
            var email = EmailBuilder.startingBlank()
                    .from("FlyTrack", username)
                    .to(toEmail)
                    .withSubject(subject)
                    .withHTMLText(htmlContent)
                    .buildEmail();

            getMailer().sendMail(email);
            log.info("Verification email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Error sending verification email to {}: {}", toEmail, e.getMessage());
        }
    }

}