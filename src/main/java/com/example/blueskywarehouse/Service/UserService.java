package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.TaskRepository;
import com.example.blueskywarehouse.Dao.UserRepository;
import com.example.blueskywarehouse.Entity.LoginUserDetails;
import com.example.blueskywarehouse.Entity.Task;
import com.example.blueskywarehouse.Entity.User;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Exception.InvalidParameterException;
import com.example.blueskywarehouse.Response.ApiResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    Logger logger = LoggerFactory.getLogger(OptimalStorageLocationService.class);

    public ApiResponse<?> getUserId(String userName){
        if (userName == null || userName.trim().isEmpty()) {
            logger.warn("获取用户ID失败：用户名为空");
            throw new InvalidParameterException("用户名不能为空");
        }

        // 获取用户ID
        Integer userId = userRepository.userId(userName);
        String checkUserName=userRepository.checkUserName(userId);

        // 判断是否存在该用户
        if (userId == null) {
            logger.info("用户名未找到：{}", userName);
            throw new BusinessException("未找到对应的用户");
        }

        logger.info("获取用户ID成功：userName={}, userId={}", userName, userId);
        return ApiResponse.success("获取用户ID成功", userId+":"+checkUserName);
    }
    // 建议把 PasswordEncoder 作为成员变量，减少重复创建

    @Transactional
    public ApiResponse<?> addNewUser(String userName, String password, int role) {

        logger.info("尝试添加新用户，用户名: {}", userName);

        if (userName == null || password == null || userName.trim().isEmpty() || password.trim().isEmpty()) {
            logger.warn("添加用户失败：用户名或密码为空");
            throw new InvalidParameterException("用户名或密码不能为空");
        }

        if (password.length() < 8) {
            logger.warn("添加用户失败：密码长度不足，用户名：{}", userName);
            throw new BusinessException("密码长度需大于8位");
        }

        String isUserNameExisted = userRepository.getUserName(userName);
        if (isUserNameExisted != null) {
            logger.warn("添加用户失败：用户名已存在，用户名：{}", userName);
            throw new BusinessException("用户名已存在");
        }

        String encryptPwd = passwordEncoder.encode(password);

        userRepository.insertNewUser(userName, encryptPwd, role);
        logger.info("成功添加新用户：{}", userName);
        return ApiResponse.success("用户添加成功", null);
    }

    public ApiResponse<?> login(String userName, String password) {

        logger.info("用户尝试登录，用户名: {}", userName);

        if (userName == null || userName.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            logger.warn("登录失败：用户名或密码为空");
            throw new InvalidParameterException("用户名或密码不能为空");
        }

        String getUserName = userRepository.getUserName(userName);
        if (getUserName == null) {
            logger.warn("登录失败：用户名 [{}] 不存在", userName);
            throw new BusinessException("用户名不存在");
        }

        String getPassword = userRepository.getPwd(userName);
        if (getPassword == null) {
            logger.warn("登录失败：用户名 [{}] 的密码未设置", userName);logger.warn("登录失败：用户名 [{}] 的密码未设置", userName);
            throw new BusinessException("用户密码未设置");
        }

        boolean matches = passwordEncoder.matches(password, getPassword);
        if (!matches) {
            logger.warn("登录失败：用户名 [{}] 密码错误", userName);
            throw new BusinessException("密码错误");
        }

        int getRoleId = userRepository.getRoleId(userName);
        logger.debug("用户名 [{}] 验证通过，角色ID: {}", userName, getRoleId);
        User thisUser = new User(userName, null, getRoleId);


        logger.info("用户 [{}] 登录成功", userName);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auth.getAuthorities().forEach(a -> System.out.println("权限: " + a.getAuthority()));
        return ApiResponse.success("登录成功", thisUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("尝试加载用户名为 [{}] 的用户", username);
        String userName = userRepository.getUserName(username); // 你要确保这个方法存在
        Integer userRole=userRepository.getRoleId(username);
        logger.debug("从数据库获取的角色ID: {}", userRole);
        User user=new User();
        user.setName(userName);
        user.setPwd(null);
        if(userRole!=null)
        user.setRoleId(userRole);
        if (userName == null||userName=="") {
            logger.warn("用户 [{}] 不存在", username);
            throw new UsernameNotFoundException("用户不存在");
        }
        logger.info("用户 [{}] 加载成功，准备返回 LoginUserDetails", userName);
        return new LoginUserDetails(user);
    }
    @Transactional
    public ApiResponse<?> roleChange(int userId, int role) {
        User user= userRepository.findById((long) userId).orElseThrow(()->new RuntimeException("用户不存在"+userId));
        user.setRoleId(role);
        userRepository.save(user);
        logger.info("用户角色已更新成功：{}", userId,role);
        return ApiResponse.success("更新成功",null);
    }

    public ApiResponse<?> getAllUser() {
       List<User>allUser= userRepository.getAllUser();
        logger.info("用户角色已获取成功：{}", allUser);
        return ApiResponse.success("获取所有用户成功",allUser);

    }
    @Transactional
    public ApiResponse<?> deleteUser(int id) {
        userRepository.deleteUser(id);
        logger.info("用户角色已删除，id为:", id);
        return ApiResponse.success("已删除完成",null);
    }
}
