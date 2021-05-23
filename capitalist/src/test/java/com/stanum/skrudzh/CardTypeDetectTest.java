package com.stanum.skrudzh;

import com.stanum.skrudzh.model.enums.CardTypeEnum;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
public class CardTypeDetectTest extends Assert {

    @Test
    public void visaDetect() {
        String cardNumber = "411111********11";
        Assert.isTrue(CardTypeEnum.visa.equals((CardTypeEnum.detect(cardNumber))));
    }

    @Test
    void dinersClubDetect() {
        String cardNumber = "553914******1282";
        Assert.isTrue(CardTypeEnum.diners_club.equals(CardTypeEnum.detect(cardNumber)));
    }

    @Test
    @Disabled
    void jcbDetect() {
        String cardNumber = "358934******4782";
        Assert.isTrue(CardTypeEnum.jcb.equals(CardTypeEnum.detect(cardNumber)));
    }

    @Test
    void unionPayDetect() {
        String cardNumber = "625094********16";
        Assert.isTrue(CardTypeEnum.china_unionpay.equals(CardTypeEnum.detect(cardNumber)));
    }

    @Test
    void maestroDetect() {
        String cardNumber = "677179******0008";
        Assert.isTrue(CardTypeEnum.maestro.equals(CardTypeEnum.detect(cardNumber)));
    }

    @Test
    void uatpDetect() {
        String cardNumber = "135410******955";
        Assert.isTrue(CardTypeEnum.uatp.equals(CardTypeEnum.detect(cardNumber)));
    }

    @Test
    void mirDetect() {
        String cardNumber = "220306******9623";
        Assert.isTrue(CardTypeEnum.mir.equals(CardTypeEnum.detect(cardNumber)));
    }







}
