package com.example.hourlymaids.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "FEEDBACK")
@Where(clause = "is_deleted = 0")
public class FeedbackEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "FEEDBACK_SEQ", sequenceName = "FEEDBACK_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "FEEDBACK_SEQ")
    private Long id;
    @Column(name = "RATE_NUMBER")
    private Integer rateNumber;
    @Column(name = "CONTENT")
    private String content;
    @Column(name = "TYPE")
    private Integer type;
    @Column(name = "USER_ID")
    private Long userId;
    @Column(name = "EMPLOYEE_ID")
    private Long employeeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRateNumber() {
        return rateNumber;
    }

    public void setRateNumber(Integer rateNumber) {
        this.rateNumber = rateNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
}
