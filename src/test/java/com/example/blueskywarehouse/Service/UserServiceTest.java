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

    // Testfall: Wenn Benutzername nicht existiert → BusinessException
    @Test
    public void shouldThrowException_whenUsernameDoesNotExist() {
        // Arrange
        when(userRepository.getUserName("testname")).thenReturn(null);

        // Act
        assertThrows(BusinessException.class, () -> userService.login("testname", anyString()));

        verify(userRepository, never()).getPwd("testname");
        verify(userRepository, never()).getRoleId("testname");
    }

    // Testfall: Wenn Benutzername oder Passwort leer ist → InvalidParameterException
    @Test
    public void shouldThrowException_whenUsernameOrPasswordIsEmpty() {
        // Act
        assertThrows(InvalidParameterException.class, () -> userService.login("", anyString()));
        assertThrows(InvalidParameterException.class, () -> userService.login(null, anyString()));
        assertThrows(InvalidParameterException.class, () -> userService.login(null, null));
        assertThrows(InvalidParameterException.class, () -> userService.login(null, ""));
        assertThrows(InvalidParameterException.class, () -> userService.login(anyString(), null));
        assertThrows(InvalidParameterException.class, () -> userService.login(anyString(), ""));
    }

    // Testfall: Wenn Passwort nicht übereinstimmt → BusinessException
    @Test
    public  void shouldThrowException_whenPasswordDoesNotMatch() {
        // Arrange
        when(userRepository.getUserName("testname")).thenReturn("testname");
        when(userRepository.getPwd("testname")).thenReturn("encodedPassword");
        when(passwordEncoder.matches("falsepwd", "encodedPassword")).thenReturn(false);

        // Act
        assertThrows(BusinessException.class, () -> userService.login("testname", "falsepwd"));

        verify(userRepository).getUserName("testname");
        verify(userRepository).getPwd("testname");
        verify(userRepository, never()).getRoleId("testname");
    }

    // Testfall: Erfolgreiches Login wenn Benutzername und Passwort korrekt sind
    @Test
    public void shouldLoginSuccessfully_whenCredentialsAreCorrect() {
        // Arrange
        when(userRepository.getUserName("testname")).thenReturn("testname");
        when(userRepository.getPwd("testname")).thenReturn("encodedPassword");
        when(passwordEncoder.matches("getpwdsuccess", "encodedPassword")).thenReturn(true);
        when(userRepository.getRoleId("testname")).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> userService.login("testname", "getpwdsuccess"));

        verify(userRepository).getUserName("testname");
        verify(userRepository).getPwd("testname");
        verify(userRepository).getRoleId("testname");
    }

    // Testfall: Wenn neuer Benutzername oder Passwort leer → InvalidParameterException
    @Test
    public void shouldThrowException_whenAddUsernameOrPasswordIsEmpty() {
        // Act
        assertThrows(InvalidParameterException.class, () -> userService.addNewUser("", null, 1));
        assertThrows(InvalidParameterException.class, () -> userService.addNewUser(null, null, 1));
        assertThrows(InvalidParameterException.class, () -> userService.addNewUser(null, "", 1));
        assertThrows(InvalidParameterException.class, () -> userService.addNewUser("", "", 1));

        verify(userRepository, never()).getUserName(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    // Testfall: Wenn Passwort zu kurz → BusinessException
    @Test
    public void shouldThrowException_whenPasswordTooShort() {
        // Act
        assertThrows(BusinessException.class, () -> userService.addNewUser("testuser", "short", 1));

        verify(userRepository, never()).getUserName(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    // Testfall: Wenn Benutzername bereits existiert → BusinessException
    @Test
    public void shouldThrowException_whenUsernameAlreadyExists() {
        // Arrange
        when(userRepository.getUserName("existingUser")).thenReturn("existingUser");

        // Act
        assertThrows(BusinessException.class, () ->
                userService.addNewUser("existingUser", "validPassword123", 1)
        );

        verify(userRepository).getUserName("existingUser");
        verify(userRepository, never()).save(any(User.class));
    }

    // Testfall: Erfolgreiches Hinzufügen eines neuen Benutzers
    @Test
    public void shouldAddNewUserSuccessfully() {
        // Arrange
        when(userRepository.getUserName("newUser")).thenReturn(null);
        when(passwordEncoder.encode("validPassword123")).thenReturn("encryptedPassword");

        // Act
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

