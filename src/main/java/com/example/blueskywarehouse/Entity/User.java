package com.example.blueskywarehouse.Entity;

import jakarta.persistence.*;

@Table(name = "user")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    @Column(name="name")
    private String name;

    @Column(name="pwd")
    private String pwd;

    @Column(name="role_id")
    private int roleId;
    @Version
    private Integer version;
    public User() {
    }
    public User(Long id, String name, String pwd, int roleId) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.roleId = roleId;
    }
    public User(String name, String pwd, int roleId) {
        this.name = name;
        this.pwd = pwd;
        this.roleId = roleId;
    }

    public User(Long id, String name, String pwd, int roleId, Integer version) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.roleId = roleId;
        this.version = version;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
