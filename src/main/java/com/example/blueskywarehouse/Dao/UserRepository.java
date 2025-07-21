package com.example.blueskywarehouse.Dao;

import com.example.blueskywarehouse.Entity.Task;
import com.example.blueskywarehouse.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT id FROM user WHERE name like concat('%', :userName,'%')", nativeQuery = true)
    Integer userId(@Param("userName") String userName);
    @Query(value = "SELECT name FROM user WHERE id=:userId", nativeQuery = true)
    String checkUserName(Integer userId);
    @Modifying
    @Transactional
    @Query(value = "Insert into user (name,pwd,role_id)values (:userName,:encryptPwd,:role)", nativeQuery = true)
    void insertNewUser(String userName, String encryptPwd, int role);
    @Query(value = "select name from user where name=:userName", nativeQuery = true)
    String getUserName(String userName);
    @Query(value = "select pwd from user where name=:userName", nativeQuery = true)
    String getPwd(String userName);
    @Query(value = "select role_id from user where name=:userName", nativeQuery = true)
    Integer getRoleId(String userName);
    @Query(value = "SELECT * FROM user", nativeQuery = true)
    List<User> getAllUser();
    @Modifying
    @Transactional
    @Query(value = "delete from user where id=:id", nativeQuery = true)
    void deleteUser(int id);
}
