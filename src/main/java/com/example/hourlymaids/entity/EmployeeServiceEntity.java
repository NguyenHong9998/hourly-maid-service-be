package com.example.hourlymaids.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "EMPLOYEE_SERVICE")
@Where(clause = "is_deleted = 0")
public class EmployeeServiceEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "EMPLOYEE_SERVICE_SEQ", sequenceName = "EMPLOYEE_SERVICE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "EMPLOYEE_SERVICE_SEQ")
    private Long id;
    @Column(name = "USER_ID")
    private Long userId;
    @Column(name = "SERVICE_ID")
    private Long serviceId;
    @Column(name = "LEVEL")
    private Integer level;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
