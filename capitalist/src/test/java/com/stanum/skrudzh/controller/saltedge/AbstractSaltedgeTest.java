package com.stanum.skrudzh.controller.saltedge;

import com.stanum.skrudzh.TestUtils;
import com.stanum.skrudzh.controller.ConnectionApiController;
import com.stanum.skrudzh.controller.ExpenseSourcesApiController;
import com.stanum.skrudzh.controller.form.ExpenseSourceCreationForm;
import com.stanum.skrudzh.controller.form.SessionCredentialsForm;
import com.stanum.skrudzh.controller.form.saltedge.AccountConnectionAttributes;
import com.stanum.skrudzh.controller.response.ConnectionResponse;
import com.stanum.skrudzh.jpa.model.AccountEntity;
import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.AccountRepository;
import com.stanum.skrudzh.jpa.repository.UserRepository;
import com.stanum.skrudzh.model.dto.ExpenseSource;
import com.stanum.skrudzh.model.dto.Session;
import com.stanum.skrudzh.model.dto.User;
import com.stanum.skrudzh.saltage.model.Connection;
import com.stanum.skrudzh.service.saltedge.connection.ConnectionFinder;
import com.stanum.skrudzh.service.saltedge.connection.ConnectionService;
import com.stanum.skrudzh.service.user.UserManagementService;
import com.stanum.skrudzh.service.user.UserRequestService;
import com.stanum.skrudzh.utils.RequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.time.ZoneId;
import java.util.Locale;

import static org.mockito.Mockito.when;

public class AbstractSaltedgeTest {
    public static final String TEST_CONNECTION_ID = "303325766685297152";
    public static final String ACCOUNT_ID = "303325861258463823";
    public static final String USER_DEVICE_TOKEN = "device_token";
    public static AccountEntity accountEntity;
    public static ExpenseSource expenseSource;
    public static boolean isRegistered = false;
    public static User user;
    public static String token;
    protected static Connection connection;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ConnectionApiController connectionApiController;

    @Autowired
    private UserRequestService userRequestService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    private ExpenseSourcesApiController expenseSourcesApiController;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private ConnectionFinder connectionFinder;

    @BeforeEach
    public void init() throws Exception {
        if(isRegistered) {
            UserEntity userEntity = userRepository.findById(user.getId()).get();
            userEntity.setDeviceToken(USER_DEVICE_TOKEN);

//            userEntity.setSaltEdgeCustomerId("");

            RequestUtil.setToken(token);
            RequestUtil.setUser(userEntity);
            return;
        }
        Field env = userManagementService.getClass().getDeclaredField("environment");
        env.setAccessible(true);
        env.set(userManagementService, String.valueOf(System.currentTimeMillis()));

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("");
        when(request.getRequestURL()).thenReturn(new StringBuffer());

        RequestContextHolder.currentRequestAttributes().setAttribute("locale", Locale.ENGLISH, RequestAttributes.SCOPE_REQUEST);
        RequestContextHolder.currentRequestAttributes().setAttribute("timezone", ZoneId.of("Europe/Paris"), RequestAttributes.SCOPE_REQUEST);


        SessionCredentialsForm formGuest = new SessionCredentialsForm();
        SessionCredentialsForm.SessionForm sessionFormGuest = formGuest.new SessionForm();

        Session sessionGuest = userRequestService.login(sessionFormGuest);
        user = sessionGuest.getUser();

        UserEntity userEntity = userRepository.findById(user.getId()).get();
        userEntity.setDeviceToken(USER_DEVICE_TOKEN);
//        userEntity.setSaltEdgeCustomerId("");


        token = sessionGuest.getToken();
        RequestUtil.setToken(token);
        RequestUtil.setUser(userEntity);

        String email = "saltEmail@mail.com";
        String pass = "qwe123asdad!";
        ConnectionResponse response = connectionApiController.createConnectionEntity(user.getId(), "",
                TestUtils.connectionCreateForm(TEST_CONNECTION_ID)).getBody();
        userEntity = userRequestService.register(TestUtils.userCreationForm(email, pass), "");

        ConnectionEntity connectionEntity = connectionFinder.findBySaltEdgeId(TEST_CONNECTION_ID).get();
        connectionService.refreshAccounts(connectionEntity);

        accountEntity = accountRepository.findBySaltEdgeAccountId(ACCOUNT_ID).get();

        String name = "Expense Source Name";
//        expenseSource = expenseSourcesApiController
//                .usersUserIdExpenseSourcesPost("",
//                        createExpenseSource("RUB", name, 100L, accountEntity.getId()), user.getId())
//                .getBody().getExpenseSource();

        isRegistered = true;
    }

    private ExpenseSourceCreationForm createExpenseSource(String currency, String name, Long amountCents, Long accountId) {
        ExpenseSourceCreationForm form = new ExpenseSourceCreationForm();
        ExpenseSourceCreationForm.ExpenseSourceCF cf = form.new ExpenseSourceCF();
        cf.setAmountCents(amountCents);
        cf.setCurrency(currency);
        cf.setName(name);

        AccountConnectionAttributes attributes = new AccountConnectionAttributes();
        attributes.setAccountId(accountId);

        cf.setAccountConnectionAttributes(attributes);
        form.setExpenseSource(cf);
        return form;
    }
}
