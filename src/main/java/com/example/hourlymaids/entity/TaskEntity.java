package com.example.hourlymaids.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "TASK")
@Where(clause = "is_deleted = 0")
public class TaskEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "TASK_SEQ", sequenceName = "TASK_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "TASK_SEQ")
    private Long id;
    @Column(name = "SERVICE_ID")
    private Long serviceId;
    @Column(name = "ADDRESS_ID")
    private Long addressId;
    @Column(name = "USER_ID")
    private Long userId;
    @Column(name = "PRICE")
    private Long price;
    @Column(name = "ASSIGN_EMPLOYEE_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assignEmployeeTime;
    @Column(name = "START_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Column(name = "NUMBER_USEr")
    private Integer numberUser;
    @Column(name = "COMPLETE_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completeTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Date getAssignEmployeeTime() {
        return assignEmployeeTime;
    }

    public void setAssignEmployeeTime(Date assignEmployeeTime) {
        this.assignEmployeeTime = assignEmployeeTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getNumberUser() {
        return numberUser;
    }

    public void setNumberUser(Integer numberUser) {
        this.numberUser = numberUser;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }
}
