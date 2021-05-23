package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.controller.form.IncomeSourceCreationForm;
import com.stanum.skrudzh.controller.form.IncomeSourceUpdatingForm;
import com.stanum.skrudzh.controller.response.IncomeSourceResponse;
import com.stanum.skrudzh.jpa.model.IncomeSourceEntity;
import com.stanum.skrudzh.jpa.repository.IncomeSourcesRepository;
import com.stanum.skrudzh.model.dto.IncomeSources;
import net.bytebuddy.utility.RandomString;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.when;

@SpringBootTest
public class IncomeSourceApiControllerTest extends IntegrationTest {

    @Autowired
    private IncomeSourcesApiController incomeSourcesApiController;

    @Autowired
    private IncomeSourcesRepository incomeSourcesRepository;

    @Mock
    private HttpServletRequest httpRequest;

    @Override
    public void postInit() {
        when(httpRequest.getMethod()).thenReturn(HttpMethod.PUT.name());
    }

    @Test
    public void shouldCreateIncomeSource() {
        IncomeSourceCreationForm form = createForm();
        ResponseEntity<IncomeSourceResponse> response = incomeSourcesApiController.createIncomeSource(user.getId(), form, null);

        IncomeSourceEntity savedIncome = incomeSourcesRepository.findById(response.getBody().getIncomeSource().getId()).get();
        Assert.assertNotNull(savedIncome);
    }

    @Test
    public void shouldFindById() {
        IncomeSourceCreationForm form = createForm();
        ResponseEntity<IncomeSourceResponse> response = incomeSourcesApiController.createIncomeSource(user.getId(), form, null);

        IncomeSourceResponse savedIncome = incomeSourcesApiController.getIncomeSourcebyId(response.getBody().getIncomeSource().getId(), "").getBody();
        Assert.assertNotNull(savedIncome.getIncomeSource());
    }

    @Test
    public void shouldFindByUser() {
        IncomeSourceCreationForm form = createForm();
        ResponseEntity<IncomeSourceResponse> response = incomeSourcesApiController.createIncomeSource(user.getId(), form, null);
        ResponseEntity<IncomeSourceResponse> response2 = incomeSourcesApiController.createIncomeSource(user.getId(), form, null);

        IncomeSources savedIncomes = incomeSourcesApiController.getIncomeSources(user.getId(), false, "").getBody();
        Assert.assertEquals(2, savedIncomes.getIncomeSources().size());
    }

    @Test
    public void shouldUpdateIncomeSource() {
        IncomeSourceCreationForm form = createForm();
        ResponseEntity<IncomeSourceResponse> response = incomeSourcesApiController.createIncomeSource(user.getId(), form, null);

        IncomeSourceUpdatingForm updateForm = updateForm();
        incomeSourcesApiController.updateIncomeSource(response.getBody().getIncomeSource().getId(),
                "", updateForm, httpRequest);

        IncomeSourceEntity updatedIncome = incomeSourcesRepository.findById(response.getBody().getIncomeSource().getId()).get();
        Assert.assertNotNull(updatedIncome);
        Assert.assertEquals("Updated name", updatedIncome.getName());
    }

    @Test
    public void shouldUpdateIncomeSourcePrototype() {
        IncomeSourceCreationForm form = createForm();
        ResponseEntity<IncomeSourceResponse> response = incomeSourcesApiController.createIncomeSource(user.getId(), form, null);

        IncomeSourceUpdatingForm updateForm = updateForm();
        String prototype = RandomString.make();
        updateForm.getIncomeSource().setPrototypeKey(prototype);
        incomeSourcesApiController.updateIncomeSource(response.getBody().getIncomeSource().getId(),
                "", updateForm, httpRequest);

        IncomeSourceEntity updatedIncome = incomeSourcesRepository.findById(response.getBody().getIncomeSource().getId()).get();
        Assert.assertNotNull(updatedIncome);
        Assert.assertEquals(prototype, updatedIncome.getPrototypeKey());
        Assert.assertEquals("Updated name", updatedIncome.getName());
    }

    private IncomeSourceCreationForm createForm() {
        IncomeSourceCreationForm form = new IncomeSourceCreationForm();
        IncomeSourceCreationForm.IncomeSourceCF incomeSourceCF = form.new IncomeSourceCF();
        incomeSourceCF.setCurrency("RUB");
        incomeSourceCF.setName("Source name");

        form.setIncomeSource(incomeSourceCF);
        return form;
    }

    private IncomeSourceUpdatingForm updateForm() {
        IncomeSourceUpdatingForm form = new IncomeSourceUpdatingForm();
        IncomeSourceUpdatingForm.IncomeSourceUF incomeSourceCF = form.new IncomeSourceUF();
        incomeSourceCF.setName("Updated name");

        form.setIncomeSource(incomeSourceCF);
        return form;
    }
}
