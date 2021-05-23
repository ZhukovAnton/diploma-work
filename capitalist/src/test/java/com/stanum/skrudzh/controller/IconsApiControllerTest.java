package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.model.dto.Icon;
import com.stanum.skrudzh.model.enums.IconCategoryEnum;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class IconsApiControllerTest {

    @Autowired
    private IconsApiController iconsApiController;

    @Test
    public void shouldReturnIcons() {
        List<Icon> expenseSourceCategories = iconsApiController.getIcons("", IconCategoryEnum.expense_source.name()).getBody().getIcons();
        List<Icon> commonCategories = iconsApiController.getIcons("", IconCategoryEnum.common.name()).getBody().getIcons();
        Assert.assertFalse(expenseSourceCategories.isEmpty());
        Assert.assertFalse(commonCategories.isEmpty());
    }

}
