package com.example.hourlymaids.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;


@Entity
@Table(name = "ACCOUNT")
@Where(clause = "is_deleted = 0")
public class AccountEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "ACCOUNT_SEQ", sequenceName = "ACCOUNT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ACCOUNT_SEQ")
    private Long id;
    @Column(name = "ROLE_ID")
    private Long roleId;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "PASSWORD")
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
