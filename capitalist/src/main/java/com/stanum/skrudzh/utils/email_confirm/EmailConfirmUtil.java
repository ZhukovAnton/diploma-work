package com.stanum.skrudzh.utils.email_confirm;

import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.service.user.EmailService;
import com.stanum.skrudzh.utils.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Service
@RequiredArgsConstructor
public class EmailConfirmUtil {

    private static final String MESSAGE = "messages";

    private final TemplateEngine templateEngine;

    private final EmailService emailService;

    public ModelAndView createPageForExpiredLink(String filename, ModelAndView modelAndView) {
        Map<String, String> map = thymeleafUtilForExpiredLink(filename);
        map.forEach(modelAndView::addObject);
        modelAndView.setViewName(filename);
        return modelAndView;

    }

    public ModelAndView createPageForConfirmedUser(UserEntity user, String filename, ModelAndView modelAndView) {
        Map<String, String> map = thymeleafUtilForConfirmedUser(filename);
        map.putAll(thymeleafUtilForUserWithCode(user));
        map.forEach(modelAndView::addObject);
        modelAndView.setViewName(filename);
        return modelAndView;
    }

    public ModelAndView createPageForFailedConfirm(UserEntity user, String filename, ModelAndView modelAndView) {
        Map<String, String> map = thymeleafUtilForFailedConfirm(filename);
        map.putAll(thymeleafUtilForUserWithCode(user));
        map.forEach(modelAndView::addObject);
        modelAndView.setViewName(filename);
        return modelAndView;
    }

    private Map<String, String> thymeleafUtilForExpiredLink(String filename) {
        Map<String, String> thymeleafVars = new HashMap<>();
        Locale locale = RequestUtil.getLocale();

        thymeleafVars.put("link_expired", ResourceBundle.getBundle(MESSAGE, locale)
                .getString("user_mailer.registration_confirmation.link_expired"));
        emailService.addCommonInfo(thymeleafVars, locale);

        thymeleafUtil(thymeleafVars, filename);
        return thymeleafVars;
    }

    private Map<String, String> thymeleafUtilForConfirmedUser(String filename) {
        Map<String, String> thymeleafVars = new HashMap<>();
        Locale locale = RequestUtil.getLocale();

        thymeleafVars.put("successfully_confirmed", ResourceBundle.getBundle(MESSAGE, locale)
                .getString("user_mailer.registration_confirmation.successfully_confirmed"));
        emailService.addCommonInfo(thymeleafVars, locale);

        thymeleafUtil(thymeleafVars, filename);
        return thymeleafVars;
    }

    private Map<String, String> thymeleafUtilForFailedConfirm(String filename) {
        Map<String, String> thymeleafVars = new HashMap<>();
        Locale locale = RequestUtil.getLocale();

        thymeleafVars.put("failed_confirmation", ResourceBundle.getBundle(MESSAGE, locale)
                .getString("user_mailer.registration_confirmation.failed_confirmation"));
        emailService.addCommonInfo(thymeleafVars, locale);

        thymeleafUtil(thymeleafVars, filename);
        return thymeleafVars;
    }

    private Map<String, String> thymeleafUtilForUserWithCode(UserEntity user) {
        Map<String, String> thymeleafVars = new HashMap<>();
        Locale locale = RequestUtil.getLocale();

        thymeleafVars.put("greeting", String.format(ResourceBundle
                .getBundle(MESSAGE, locale)
                .getString("user_mailer.greeting"), getName(user.getFirstname())));
        return thymeleafVars;
    }

    private void thymeleafUtil(Map<String, String> thymeleafVars, String filename) {
        Context context = new Context();
        thymeleafVars.forEach(context::setVariable);
        templateEngine.process(filename, context);
    }

    private String getName(String name) {
        if(name == null || name.isEmpty()) {
            return "";
        } else {
            return ", " + name;
        }
    }

}
