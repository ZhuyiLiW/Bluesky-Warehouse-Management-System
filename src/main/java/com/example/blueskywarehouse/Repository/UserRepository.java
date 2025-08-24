package com.example.blueskywarehouse.Repository;

import com.example.blueskywarehouse.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query( "SELECT t.id FROM User t WHERE t.name like concat('%', :userName,'%')")
    Integer userId(@Param("userName") String userName);
    @Query("SELECT t.name FROM User t WHERE t.id=:userId")
    String checkUserName(@Param("userId")Integer userId);
    @Query( "select t.name from User t where t.name=:userName")
    String getUserName(@Param("userName")String userName);
    @Query("select t.pwd from User t where t.name=:userName")
    String getPwd(@Param("userName")String userName);
    @Query( "select t.roleId from User t where t.name=:userName")
    Integer getRoleId(@Param("userName") String userName);

}
