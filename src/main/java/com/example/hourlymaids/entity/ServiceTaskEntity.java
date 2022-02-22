package com.example.hourlymaids.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "SERVICE_TASK")
@Where(clause = "is_deleted = 0")
public class ServiceTaskEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "SERVICE_TASK_SEQ", sequenceName = "SERVICE_TASK_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SERVICE_TASK_SEQ")
    private Long id;
    @Column(name = "SERVICE_ID")
    private Long serviceId;
    @Column(name = "TASK_ID")
    private Long taskId;
    @Column(name = "DISCOUNT_ID")
    private Long serviceDiscountId;

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

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getServiceDiscountId() {
        return serviceDiscountId;
    }

    public void setServiceDiscountId(Long serviceDiscountId) {
        this.serviceDiscountId = serviceDiscountId;
    }
}
