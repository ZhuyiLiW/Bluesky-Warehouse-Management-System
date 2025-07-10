package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.TaskRepository;
import com.example.blueskywarehouse.Dao.UserRepository;
import com.example.blueskywarehouse.Entity.LoginUserDetails;
import com.example.blueskywarehouse.Entity.User;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Exception.InvalidParameterException;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;
    @Test
    void testGetUserId_success() {
        String username = "testUser";
        int mockUserId = 42;
        String checkUserName = "testUser";

        // 模拟 userRepository 行为
        when(userRepository.userId(username)).thenReturn(mockUserId);
        when(userRepository.checkUserName(mockUserId)).thenReturn(checkUserName);

        // 调用方法
        ApiResponse<?> response = userService.getUserId(username);

        // 验证调用与结果
        verify(userRepository).userId(username);
        verify(userRepository).checkUserName(mockUserId);

        assertEquals("获取用户ID成功", response.getMessage());
        assertEquals(mockUserId + ":" + checkUserName, response.getData());
    }

    @Test
    void testGetUserId_nullOrEmptyUsername() {
        assertThrows(InvalidParameterException.class, () -> userService.getUserId(null));
        assertThrows(InvalidParameterException.class, () -> userService.getUserId(""));
        assertThrows(InvalidParameterException.class, () -> userService.getUserId("   "));
    }

    @Test
    void testGetUserId_userNotFound() {
        String username = "nonexistentUser";

        when(userRepository.userId(username)).thenReturn(null);

        assertThrows(BusinessException.class, () -> userService.getUserId(username));
        verify(userRepository).userId(username);
    }


    @Test
    void testLogin_emptyUsernameOrPassword() {
        assertThrows(InvalidParameterException.class, () -> userService.login(null, "12345678"));
        assertThrows(InvalidParameterException.class, () -> userService.login("   ", "12345678"));
        assertThrows(InvalidParameterException.class, () -> userService.login("test", null));
        assertThrows(InvalidParameterException.class, () -> userService.login("test", " "));
    }

    @Test
    void testLogin_userNotExist() {
        String username = "notExist";
        when(userRepository.getUserName(username)).thenReturn(null);
        assertThrows(BusinessException.class, () -> userService.login(username, "123456"));
        verify(userRepository).getUserName(username);
    }

    @Test
    void testLogin_passwordNotSet() {
        String username = "testuser";
        when(userRepository.getUserName(username)).thenReturn(username);
        when(userRepository.getPwd(username)).thenReturn(null);

        assertThrows(BusinessException.class, () -> userService.login(username, "123456"));
        verify(userRepository).getUserName(username);
        verify(userRepository).getPwd(username);
    }

    @Test
    void testLogin_wrongPassword() {
        String username = "testuser";
        String inputPassword = "wrongPassword";
        String encodedPassword = "$2a$10$encoded";

        when(userRepository.getUserName(username)).thenReturn(username);
        when(userRepository.getPwd(username)).thenReturn(encodedPassword);
        when(passwordEncoder.matches(inputPassword, encodedPassword)).thenReturn(false);

        assertThrows(BusinessException.class, () -> userService.login(username, inputPassword));

        verify(userRepository).getUserName(username);
        verify(userRepository).getPwd(username);
        verify(passwordEncoder).matches(inputPassword, encodedPassword);
    }

}
