package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.controller.form.UserCreationForm;
import com.stanum.skrudzh.controller.form.UserPasswordUpdatingForm;
import com.stanum.skrudzh.controller.form.UserUpdatingForm;
import com.stanum.skrudzh.controller.response.UserResponse;
import com.stanum.skrudzh.exception.ValidationException;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.UserRepository;
import com.stanum.skrudzh.model.dto.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UsersApiControllerTests extends IntegrationTest {

    @Autowired
    private UsersApiController usersApiController;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldReturnUserById() {
        ResponseEntity<UserResponse> response = usersApiController.getUserById(user.getId(), "", null);
        Assert.assertEquals(user.getId(), response.getBody().getUser().getId());
    }

    @Test
    public void shouldRegisterUser() throws Exception {

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("");
        when(request.getRequestURL()).thenReturn(new StringBuffer());

        UserCreationForm cf = createUserForm("Ivan","Ivanov", "ex@mail213.com", "StrongPass123");
        ResponseEntity<UserResponse> response = usersApiController.usersPost(",",
                cf,
                null,
                request);

        User registeredUser = response.getBody().getUser();
        Assert.assertEquals(registeredUser.getFirstname(), cf.getUser().getFirstname());
    }

    @Test
    public void shouldNotRegisterUser_ifPasswordMismatch() {
        assertThrows(
                ValidationException.class,
                () -> {
                    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
                    when(request.getRequestURI()).thenReturn("");
                    when(request.getRequestURL()).thenReturn(new StringBuffer());

                    UserCreationForm cf = createUserForm("Ivan","Ivanov", "ex@mail.com", "StrongPass123");
                    cf.passwordConfirmation("WrongPass");
                    ResponseEntity<UserResponse> response = usersApiController.usersPost(",",
                            cf,
                            null,
                            request);
                }
        );
    }

    @Test
    public void shouldNotRegisterUser_ifPasswordLessThan_6_characters() {
        assertThrows(
                ValidationException.class,
                () -> {
                    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
                    when(request.getRequestURI()).thenReturn("");
                    when(request.getRequestURL()).thenReturn(new StringBuffer());

                    UserCreationForm cf = createUserForm("Ivan","Ivanov", "ex@mail.com", "StrongPass123");
                    cf.passwordConfirmation("1");
                    cf.passwordConfirmation("1");
                    ResponseEntity<UserResponse> response = usersApiController.usersPost(",",
                            cf,
                            null,
                            request);
                }
        );
    }

    @Test
    public void shouldNotRegisterUser_ifEmailExists() {
        assertThrows(
                ValidationException.class,
                () -> {
                    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
                    when(request.getRequestURI()).thenReturn("");
                    when(request.getRequestURL()).thenReturn(new StringBuffer());

                    UserCreationForm cf = createUserForm("Ivan","Ivanov", "ExaSDa@mail.com", "StrongPass123");
                    UserCreationForm cf2 = createUserForm("Ivan","Ivanov", "exaSda@mail.com", "StrongPass123");

                    ResponseEntity<UserResponse> response = usersApiController.usersPost(",",
                            cf,
                            null,
                            request);

                    ResponseEntity<UserResponse> response2 = usersApiController.usersPost(",",
                            cf2,
                            null,
                            request);
                    }
        );
    }

    @Test
    public void shouldUpdatePassword() throws Exception {
        String currentPassword = "CurrentPass000";
        String newPassword = "NewPass000";
        UserCreationForm cf = createUserForm("FirstName", "LastName", "email2@mail.com", currentPassword);
        User user = createUser(cf);

        UserEntity currentUser = userRepository.findById(user.getId()).get();

        UserPasswordUpdatingForm form = new UserPasswordUpdatingForm();
        UserPasswordUpdatingForm.PasswordUpdatingForm passwordUpdatingForm = form.new PasswordUpdatingForm();
        passwordUpdatingForm.setOldPassword(currentPassword);
        passwordUpdatingForm.setNewPassword(newPassword);
        passwordUpdatingForm.setNewPasswordConfirmation(newPassword);
        form.setUser(passwordUpdatingForm);

        usersApiController.updatePassword(user.getId(),
                "",
                form);

        UserEntity updatedUser = userRepository.findById(user.getId()).get();
        Assert.assertNotEquals(currentUser.getPasswordDigest(), updatedUser.getPasswordDigest());
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        String currentFirstName = "CurrentFirstName";
        UserCreationForm cf = createUserForm(currentFirstName, "LastName", "email@mail.com", "password123");
        User user = createUser(cf);

        String newFirstName = "NewFirstName";

        UserEntity currentUser = userRepository.findById(user.getId()).get();

        UserUpdatingForm form = new UserUpdatingForm();
        UserUpdatingForm.UserUF userUF = form.new UserUF();
        userUF.setFirstname(newFirstName);
        form.setUser(userUF);

        usersApiController.updateUser(user.getId(),
                "",
                form);

        UserEntity updatedUser = userRepository.findById(user.getId()).get();
        Assert.assertEquals(newFirstName, updatedUser.getFirstname());
    }

    @Test
    public void shouldConfirmEmail() throws Exception {
        String currentFirstName = "CurrentFirstName";
        UserCreationForm cf = createUserForm(currentFirstName, "LastName", "email@mail123.com", "password123");
        User user = createUser(cf);
        String newFirstName = "NewFirstName";
        UserEntity currentUser = userRepository.findById(user.getId()).get();

        usersApiController.confirmEmail(currentUser.getEmailConfirmationCode());

        UserEntity userWithConfirmedEmail = userRepository.findById(user.getId()).get();
        Assert.assertTrue(newFirstName, userWithConfirmedEmail.getEmailConfirmedAt() != null);
    }

    private UserCreationForm createUserForm(String firstName,
                                            String lastName,
                                            String email,
                                            String pass) {
        UserCreationForm cf = new UserCreationForm();
        cf.setUser(cf.new UserCF());
        cf.email(email);
        cf.firstname(firstName);
        cf.password(pass);
        cf.passwordConfirmation(pass);
        cf.lastname(lastName);
        return cf;
    }

    private User createUser(UserCreationForm cf) throws Exception{
        ResponseEntity<UserResponse> response = usersApiController.usersPost(",",
                cf,
                null,
                request);
        return response.getBody().getUser();
    }

}
