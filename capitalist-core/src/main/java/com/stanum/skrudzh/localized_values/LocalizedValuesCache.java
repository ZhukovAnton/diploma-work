package com.stanum.skrudzh.localized_values;

import com.google.common.base.Strings;
import com.stanum.skrudzh.jpa.model.system.LocalizedValue;
import com.stanum.skrudzh.jpa.repository.LocalizedValuesRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class LocalizedValuesCache {
    private static final String defaultLocaleString = "en";

    private final LocalizedValuesRepository repository;

    public static Map<String, String> cache = new HashMap<>();

    public String get(String key, String locale) {
        if(Strings.isNullOrEmpty(key)) {
            return null;
        }
        if(locale == null) {
            locale = defaultLocaleString;
        }
        String result = cache.get(key + "_" + locale);
        if(result == null) {
            log.warn("Can't find localized value for key {}, locale {}", key, locale);
            return fallBack(key);
        }
        return result;
    }

    public String get(String key) {
        return get(key, defaultLocaleString);
    }

    public String get(String key, Locale locale) {
        if(Strings.isNullOrEmpty(key)) {
            return null;
        }
        if(locale == null) {
            return get(key, defaultLocaleString);
        }
        String lang = locale.toLanguageTag();
        if(!"ru".equals(lang) && !"en".equals(lang)) {
            log.warn("Undefined locale {}", locale);
            lang = "en";
        }
        String result = cache.get(key + "_" + lang);
        if(result == null) {
            log.warn("Can't find localized value for key {}, locale {}", key, locale);
            return fallBack(key);
        }
        return result;
    }

    private String fallBack(String key) {
        return key.contains("description") ? null : key;
    }

    @PostConstruct
    public void init() {
        reloadCache();
    }

    public void reloadCache() {
        List<LocalizedValue> values = repository.findAll();
        cache = new HashMap<>();
        for(LocalizedValue v : values) {
            cache.put(v.getKey() + "_" + v.getLocale(), v.getValue());
        }
    }

}
