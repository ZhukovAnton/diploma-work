package com.stanum.skrudzh.utils.logic;

import java.util.regex.Pattern;

public class CardNumberUtil {

    private static final String lastFourDigitsRegex = "^(.+)[0-9]{4}$";

    private static final String lastTwoDigitsRegex = "^(.+)[0-9]{2}$";

    public static String getFormattedCardNumber(String cardNumber) {
        Pattern lastFourPattern = Pattern.compile(lastFourDigitsRegex);
        Pattern lastTwoPattern = Pattern.compile(lastTwoDigitsRegex);
        int len = cardNumber.length();
        if (lastFourPattern.matcher(cardNumber).matches()) {
            return "*" + cardNumber.charAt(len - 4) + cardNumber.charAt(len - 3)
                    + cardNumber.charAt(len - 2) + cardNumber.charAt(len - 1);
        } else if (lastTwoPattern.matcher(cardNumber).matches()) {
            return "*" + cardNumber.charAt(len - 2) + cardNumber.charAt(len - 1);
        } else return cardNumber;
    }
}
