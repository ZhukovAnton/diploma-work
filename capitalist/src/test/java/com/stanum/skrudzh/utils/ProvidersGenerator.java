package com.stanum.skrudzh.utils;

import com.stanum.skrudzh.saltage.SaltedgeAPI;
import com.stanum.skrudzh.utils.provider.Provider;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
@Disabled
public class ProvidersGenerator {

    private List<String> countries = Arrays.asList(
            "JP",
            "GB",
            "NZ",
            "IS",
            "AE",
            "AU",
            "QA",
            "SE",
            "CH",
            "KW",
            "SA",
            "IE",
            "RU",
            "CA",
            "SG",
            "US",
            "NO"
    );

    private String trExampleInsert = "INSERT INTO public.transactionable_examples (name, icon_url, transactionable_type, basket_type, created_at, " +
            "updated_at, localized_key, create_by_default, country, description_localized_key, prototype_key) " +
            "VALUES ('%s', '%s', " +
            "'ExpenseSource', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, " +
            "'%s'," +
            " false, '%s', null, '%s');\n";

    private String providerMetaInsert = "INSERT INTO providers_meta(prototype_key, provider_codes, disabled) VALUES('%s', '%s', false);\n";

    private String localizedValuesInsert = "INSERT INTO localized_values(type, key, value, locale) VALUES('transactionable_example'," +
            "'%s'," +
            "'%s'," +
            "'en');\n";

    private String propName = "activerecord.defaults.models.transactionable_example.expense_source.attributes.name.";

    @Autowired
    private SaltedgeAPI saltedgeAPI;

    @Test
    public void generateProviders() {
        List<Provider> allProviders = saltedgeAPI.custom.findAllProviders();

        Map<String, List<Provider>> providersByCountries = new LinkedHashMap<>();
        for(Provider p : allProviders) {
            String country = p.getCountry_code();
            if(!countries.contains(country)) {
                continue;
            }
            List<Provider> providers = providersByCountries.computeIfAbsent(country, k -> new ArrayList<>());
            providers.add(p);
        }


        List<String> props = new ArrayList<>();
        List<String> localized = new ArrayList<>();
        List<String> exampleSql = new ArrayList<>();

        Map<String, String> providersMeta = new LinkedHashMap<>();

        Map<String, String> urlWithCode = new HashMap<>();
        int similarCounter = 0;
        for(Map.Entry<String, List<Provider>> entry : providersByCountries.entrySet()) {
            exampleSql.add("\n-- COUNTRY " + entry.getKey() + "\n");
            for(Provider p : entry.getValue()) {
                String home_url = p.getHome_url() .replace("http://", "")
                        .replace("https://", "");
                String existCode = urlWithCode.get(home_url + p.getCountry_code());
                if(existCode != null ) {
                    similarCounter++;

                    String s = providersMeta.get(existCode);
                    System.out.println(p);
                    providersMeta.put(existCode, s + "," + p.getCode());
                    continue;
                }


                String name = p.getName();
                String code = p.getCode();

                String prop = propName + code;
                String propWithValue = propName + code + "=" + name + "\n";

                String result = String.format(trExampleInsert,
                        name,
                        p.getLogo_url(),
                        prop,
                        p.getCountry_code(),
                        code);

                exampleSql.add(result);
                props.add(propWithValue);
                providersMeta.put(code, code);
                urlWithCode.put(home_url + p.getCountry_code(), code);

                localized.add(String.format(localizedValuesInsert, prop, name));
            }
        }


        List<String> providersList = new ArrayList<>();
        for(Map.Entry<String, String> entry : providersMeta.entrySet()) {
            providersList.add(String.format(providerMetaInsert, entry.getKey(), entry.getValue()));

        }

        String seedProvidersMeta = String.join("", providersList);
        String seedExamples = String.join("", exampleSql);
        String seedProps = String.join("", props);
        String localizedResult = String.join("", localized);
    }


}
