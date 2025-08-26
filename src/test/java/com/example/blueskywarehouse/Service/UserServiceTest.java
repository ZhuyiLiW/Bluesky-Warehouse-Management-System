package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Entity.User;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Exception.InvalidParameterException;
import com.example.blueskywarehouse.Repository.UserRepository;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    UserService userService;
    // for method login
    @Test
    public void shouldThrowException_whenUsernameDoesNotExist() {
        when(userRepository.getUserName("testname")).thenReturn(null);

        assertThrows(BusinessException.class, () -> userService.login("testname", anyString()));

        verify(userRepository, never()).getPwd("testname");
        verify(userRepository, never()).getRoleId("testname");
    }

    @Test
    public void shouldThrowException_whenUsernameOrPasswordIsEmpty() {
        assertThrows(InvalidParameterException.class, () -> userService.login("", anyString()));
        assertThrows(InvalidParameterException.class, () -> userService.login(null, anyString()));
        assertThrows(InvalidParameterException.class, () -> userService.login(null, null));
        assertThrows(InvalidParameterException.class, () -> userService.login(null, ""));
        assertThrows(InvalidParameterException.class, () -> userService.login(anyString(), null));
        assertThrows(InvalidParameterException.class, () -> userService.login(anyString(), ""));
    }

    @Test
    public  void shouldThrowException_whenPasswordDoesNotMatch() {
        when(userRepository.getUserName("testname")).thenReturn("testname");
        when(userRepository.getPwd("testname")).thenReturn("encodedPassword");
        when(passwordEncoder.matches("falsepwd", "encodedPassword")).thenReturn(false);

        assertThrows(BusinessException.class, () -> userService.login("testname", "falsepwd"));

        verify(userRepository).getUserName("testname");
        verify(userRepository).getPwd("testname");
        verify(userRepository, never()).getRoleId("testname");
    }

    @Test
    public void shouldLoginSuccessfully_whenCredentialsAreCorrect() {
        when(userRepository.getUserName("testname")).thenReturn("testname");
        when(userRepository.getPwd("testname")).thenReturn("encodedPassword");
        when(passwordEncoder.matches("getpwdsuccess", "encodedPassword")).thenReturn(true);
        when(userRepository.getRoleId("testname")).thenReturn(1);

        assertDoesNotThrow(() -> userService.login("testname", "getpwdsuccess"));

        verify(userRepository).getUserName("testname");
        verify(userRepository).getPwd("testname");
        verify(userRepository).getRoleId("testname");
    }

    // for method  addNewUser
    @Test
    public void shouldThrowException_whenAddUsernameOrPasswordIsEmpty() {
        assertThrows(InvalidParameterException.class, () -> userService.addNewUser("", null, 1));
        assertThrows(InvalidParameterException.class, () -> userService.addNewUser(null, null, 1));
        assertThrows(InvalidParameterException.class, () -> userService.addNewUser(null, "", 1));
        assertThrows(InvalidParameterException.class, () -> userService.addNewUser("", "", 1));

        verify(userRepository, never()).getUserName(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void shouldThrowException_whenPasswordTooShort() {
        assertThrows(BusinessException.class, () -> userService.addNewUser("testuser", "short", 1));

        verify(userRepository, never()).getUserName(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void shouldThrowException_whenUsernameAlreadyExists() {
        when(userRepository.getUserName("existingUser")).thenReturn("existingUser");

        assertThrows(BusinessException.class, () ->
                userService.addNewUser("existingUser", "validPassword123", 1)
        );

        verify(userRepository).getUserName("existingUser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void shouldAddNewUserSuccessfully() {
        when(userRepository.getUserName("newUser")).thenReturn(null);
        when(passwordEncoder.encode("validPassword123")).thenReturn("encryptedPassword");

        ApiResponse<?> response = assertDoesNotThrow(() ->
                userService.addNewUser("newUser", "validPassword123", 2)
        );

        verify(userRepository).getUserName("newUser");
        verify(passwordEncoder).encode("validPassword123");
        verify(userRepository).save(any(User.class));

        assertNotNull(response);
        assertEquals("Benutzer erfolgreich hinzugefügt", response.getMessage());
    }
}
