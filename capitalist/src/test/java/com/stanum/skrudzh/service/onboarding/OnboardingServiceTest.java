package com.stanum.skrudzh.service.onboarding;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.jpa.model.ExpenseCategoryEntity;
import com.stanum.skrudzh.jpa.model.ExpenseSourceEntity;
import com.stanum.skrudzh.jpa.model.IncomeSourceEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.UserRepository;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryFinder;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceFinder;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest
public class OnboardingServiceTest extends IntegrationTest {

    @Autowired
    private OnboardingService onboardingService;

    @Autowired
    private ExpenseSourceFinder expenseSourceFinder;

    @Autowired
    private IncomeSourceFinder incomeSourceFinder;

    @Autowired
    private ExpenseCategoryFinder expenseCategoryFinder;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldOnboardUser() {
        onboardingService.onboarding(userEntity);
        Set<ExpenseSourceEntity> expenseSourceEntities = expenseSourceFinder.findAllByUserEntity(userEntity);
        Set<IncomeSourceEntity> incomeSourceEntities = incomeSourceFinder.findAllByUser(userEntity);
        Set<ExpenseCategoryEntity> categoryEntities = expenseCategoryFinder.findAllByUser(userEntity);

        Assert.assertEquals(1, expenseSourceEntities.size());
        Assert.assertEquals(8, incomeSourceEntities.size());
        Assert.assertEquals(20, categoryEntities.size());
    }
}
