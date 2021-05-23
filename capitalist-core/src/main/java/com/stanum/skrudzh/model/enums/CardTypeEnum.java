package com.stanum.skrudzh.model.enums;

import java.util.regex.Pattern;

public enum CardTypeEnum {
    american_express("^3[47][0-9\\*x]{13}$"),
    china_unionpay("^62[0-9\\*x]{14,17}$"),
    diners_club("^[35](?:0[0-59]{1}|[45689])[0-9\\*x]{0,}$"),
    jcb("^(?:2131|1800|35\\d{3})[0-9\\*x]{11}$"),
    maestro("^(5[06-8]|6\\d)[0-9\\*x]{14}(\\d{2,3})?$"),
    master_card("^(?:5[1-5]|2(?!2([01]|20)|7(2[1-9]|3))[2-7])[0-9\\*x]{14}$"),
    uatp("^1[0-9\\*x]{14}$"),
    visa("^4[0-9\\*x]{12}(?:[0-9\\*x]{3}){0,2}$"),
    mir("^220[0-4][0-9\\*x]{12}$");

    private final Pattern pattern;

    CardTypeEnum(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public static CardTypeEnum detect(String cardNumber) {
        for (CardTypeEnum cardType : CardTypeEnum.values()) {
            if (cardType.pattern.matcher(cardNumber).matches()) return cardType;
        }
        return null;
    }



}
