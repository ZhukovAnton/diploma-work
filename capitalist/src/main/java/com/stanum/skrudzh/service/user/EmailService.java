package com.stanum.skrudzh.service.user;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.utils.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final TemplateEngine templateEngine;
    @Value("${threebaskets.mailgun.url}")
    private String url;
    @Value("${threebaskets.mailgun.apiKey}")
    private String apiKey;
    @Value("${threebaskets.mailgun.from}")
    private String from;
    @Value("${threebaskets.mailgun.domain}")
    private String domain;
    @Value("${threebaskets.mailgun.enabled}")
    private boolean mailgunEnabled;

    public void sendConfirmationEmail(UserEntity userEntity, String baseUrl) {
        log.info("Send confirmation mail for user id={}, baseUrl={}", userEntity.getId(), baseUrl);
        String confirmationUrl = baseUrl
                + "/"
                + userEntity.getEmailConfirmationCode()
                + "/confirm_email";
        Map<String, String> thymeleafVars = new HashMap<>();
        Locale locale = RequestUtil.getLocale();
        thymeleafVars.put("greeting", String.format(ResourceBundle
                .getBundle("messages", locale)
                .getString("user_mailer.greeting"), getName(userEntity.getFirstname())));
        thymeleafVars.put("email_used", String
                .format(ResourceBundle
                                .getBundle("messages", locale)
                                .getString("user_mailer.registration_confirmation.paragraphs.password_was_used"),
                        userEntity.getEmail()));
        thymeleafVars.put("confirm_if_you", ResourceBundle
                .getBundle("messages", locale)
                .getString("user_mailer.registration_confirmation.paragraphs.confirm_if_you"));
        thymeleafVars.put("confirm_message", ResourceBundle
                .getBundle("messages", locale)
                .getString("user_mailer.registration_confirmation.confirm_registration_link"));
        thymeleafVars.put("confirm_link", confirmationUrl);
        thymeleafVars.put("subject", ResourceBundle
                .getBundle("messages", locale)
                .getString("user_mailer.registration_confirmation.subject"));
        addCommonInfo(thymeleafVars, locale);

        sendEmail(thymeleafVars, userEntity.getEmail(), "registration_confirmation");
    }

    public void sendPasswordResetCodeEmail(UserEntity userEntity) {
        log.info("Send password reset code email for user id={}", userEntity.getId());
        Map<String, String> thymeleafVars = new HashMap<>();
        Locale locale = RequestUtil.getLocale();
        thymeleafVars.put("greeting", String.format(ResourceBundle
                .getBundle("messages", locale)
                .getString("user_mailer.greeting"), getName(userEntity.getFirstname())));
        thymeleafVars.put("pass_reset_code", ResourceBundle
                .getBundle("messages", locale)
                .getString("user_mailer.password_reset_code.paragraphs.your_reset_code"));
        thymeleafVars.put("code", userEntity.getPasswordResetCode());
        thymeleafVars.put("subject", ResourceBundle
                .getBundle("messages", locale)
                .getString("user_mailer.password_reset_code.subject"));
        addCommonInfo(thymeleafVars, locale);

        sendEmail(thymeleafVars, userEntity.getEmail(), "password_reset_code");
    }

    private String getName(String name) {
        if(name == null || name.isEmpty()) {
            return "";
        } else {
            return ", " + name;
        }
    }

    public void addCommonInfo(Map<String, String> thymeleafVars, Locale locale) {
        thymeleafVars.put("regards", ResourceBundle
                .getBundle("messages", locale)
                .getString("user_mailer.best_regards"));
        thymeleafVars.put("team", ResourceBundle
                .getBundle("messages", locale)
                .getString("user_mailer.team"));
        thymeleafVars.put("privacy_policy_link", ResourceBundle
                .getBundle("messages", locale)
                .getString("user_mailer.privacy_policy_link"));
        thymeleafVars.put("privacy_policy", ResourceBundle
                .getBundle("messages", locale)
                .getString("user_mailer.privacy_policy"));
        thymeleafVars.put("terms_of_use_link", ResourceBundle
                .getBundle("messages", locale)
                .getString("user_mailer.terms_of_use_link"));
        thymeleafVars.put("terms_of_use", ResourceBundle
                .getBundle("messages", locale)
                .getString("user_mailer.terms_of_use"));
        thymeleafVars.put("copyright", ResourceBundle
                .getBundle("messages", locale)
                .getString("user_mailer.copyright"));
    }

    void sendEmail(Map<String, String> variables, String email, String fileName) {
        Context emailContext = new Context();

        variables.entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals("subject"))
                .forEach(entry -> emailContext.setVariable(entry.getKey(), entry.getValue()));

        String message = templateEngine.process("mailers/" + fileName, emailContext);

        sendEmail(
                email,
                variables.get("subject"),
                message);
    }

    public void sendTextEmail(String stacktrace, List<String> recipients) {
        for(String mail : recipients) {
            log.info("Send text mail to {}", mail);
            sendEmail(mail, "Server error", "<pre>" + stacktrace + "</pre>");
        }
    }

    @Async
    void sendEmail(String email, String subject, String html) {
        if(!mailgunEnabled) {
            log.debug("MailGun disabled");
        }
        try {

            HttpResponse<String> request = Unirest.post(url + domain + "/messages")
                    .basicAuth("api", apiKey)
                    .field("from", from)
                    .field("to", email)
                    .field("subject", subject)
                    .field("html", html)
                    .asString();

            System.out.println(request.getBody());
        } catch (UnirestException e) {
            e.printStackTrace();
        }

    }

}
