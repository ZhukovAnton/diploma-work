package com.stanum.skrudzh;

import com.stanum.skrudzh.controller.BasketsApiController;
import com.stanum.skrudzh.controller.form.SessionCredentialsForm;
import com.stanum.skrudzh.jpa.model.BasketEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.UserRepository;
import com.stanum.skrudzh.model.dto.Basket;
import com.stanum.skrudzh.model.dto.Session;
import com.stanum.skrudzh.model.dto.User;
import com.stanum.skrudzh.model.enums.BasketTypeEnum;
import com.stanum.skrudzh.saltage.SaltedgeAPI;
import com.stanum.skrudzh.saltage.model.Response;
import com.stanum.skrudzh.service.basket.BasketFinder;
import com.stanum.skrudzh.service.user.UserRequestService;
import com.stanum.skrudzh.utils.RequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;

import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class IntegrationTest {
    protected HttpServletRequest request;

    @Autowired
    private BasketsApiController basketsApiController;

    @Autowired
    private UserRequestService userRequestService;

    @MockBean
    protected SaltedgeAPI saltedgeAPI;

    protected User user;
    protected UserEntity userEntity;

    @Autowired
    protected BasketFinder  basketFinder;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void init() {
        request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("");
        when(request.getRequestURL()).thenReturn(new StringBuffer());

        RequestContextHolder.currentRequestAttributes().setAttribute("locale", Locale.ENGLISH, RequestAttributes.SCOPE_REQUEST);
        RequestContextHolder.currentRequestAttributes().setAttribute("timezone", ZoneId.of("Europe/Paris"), RequestAttributes.SCOPE_REQUEST);
        when(saltedgeAPI.doCall(any(), any(), any())).thenReturn(new Response<>());
        saltedgeAPI.customer = saltedgeAPI.new CustomerAPI();
        saltedgeAPI.custom = saltedgeAPI.new CustomAPI();

        SessionCredentialsForm form = new SessionCredentialsForm();
        SessionCredentialsForm.SessionForm sessionForm = form.new SessionForm();

        Session session = userRequestService.login(sessionForm);
        user = session.getUser();

        userEntity = userRepository.findById(user.getId()).get();
        userEntity.setHasActiveSubscription(true);
        
        RequestUtil.setToken(session.getToken());
        RequestUtil.setUser(userEntity);
    }

    protected Basket getBasket() {
        return basketsApiController.getBasketsForUser(user.getId(), "").getBody().getBaskets().get(0);
    }

    protected BasketEntity getJoyBasket() {
        return basketFinder.findBasketByUserAndType(userEntity, BasketTypeEnum.joy);
    }

    protected List<Basket> getUserBaskets() {
        return basketsApiController.getBasketsForUser(user.getId(), "").getBody().getBaskets();
    }

    public void postInit() {

    }
}

