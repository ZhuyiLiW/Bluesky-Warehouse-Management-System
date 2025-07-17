package com.example.blueskywarehouse.Controller;

import com.example.blueskywarehouse.Entity.LoginUserDetails;
import com.example.blueskywarehouse.Entity.User;
import com.example.blueskywarehouse.Response.ApiResponse;
import com.example.blueskywarehouse.Service.TaskService;
import com.example.blueskywarehouse.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/UserController")
public class UserController {
    @Autowired
    private UserService userService;
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/searchUserId")
    public ApiResponse<?> searchUserId(@RequestParam String userName)  {
        return userService.getUserId(userName);
    }
    @PreAuthorize("hasRole('1') or hasRole('3')")
    @PostMapping("/addNewUser")
    public ApiResponse<?> addNewUser(@RequestParam String userName,@RequestParam String password,@RequestParam int role)  {
        return userService.addNewUser(userName,password,role);
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestParam String userName,@RequestParam String password,
                                HttpServletRequest request)  {ApiResponse<?> response = userService.login(userName, password);

        if (response.getStatus() == 200) { // 登录成功，code根据你的 ApiResponse 结构判断
            User user = (User) response.getData();

            // 构造认证信息
            LoginUserDetails loginUser = new LoginUserDetails(user);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

            // 设置 SecurityContext
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);

            // 保存 SecurityContext 到 Session
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        }

        return response;
    }
    @PreAuthorize("hasRole('1')  or hasRole('3')")
    @PostMapping("/roleChange")
    public ApiResponse<?> roleChange(@RequestParam int userId,@RequestParam int role)  {
        return userService.roleChange(userId,role);
    }

    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/getAllUser")
    public ApiResponse<?> getAllUser()  {
        return userService.getAllUser();
    }

    @PreAuthorize("hasRole('1') or hasRole('3')")
    @PostMapping("/deleteUser")
    public ApiResponse<?> deleteUser(int id)  {
        return userService.deleteUser(id);
    }
}
