package com.example.hourlymaids.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "ROLE")
@Where(clause = "is_deleted = 0")
public class RoleEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "ROLE_SEQ", sequenceName = "ROLE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ROLE_SEQ")
    private Long id;
    @Column(name = "NAME")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
