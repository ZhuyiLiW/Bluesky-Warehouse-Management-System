package com.example.blueskywarehouse.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

@Entity
public class Role {
    @Id
    private Long id;
    @Column(name="role_name")
    private String roleName;
    @Version
    private Integer version;

    public Role() {
    }
    public Role(Long id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    public Role(Long id, String roleName, Integer version) {
        this.id = id;
        this.roleName = roleName;
        this.version = version;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
