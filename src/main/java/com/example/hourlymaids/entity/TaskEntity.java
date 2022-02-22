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
    @Column(name = "ADDRESS")
    private String address;
    @Column(name = "USER_ID")
    private Long userId;
    @Column(name = "ASSIGN_EMPLOYEE_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assignEmployeeTime;
    @Column(name = "WORK_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date workDate;
    @Column(name = "START_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Column(name = "NUMBER_USEr")
    private Integer numberUser;
    @Column(name = "COMPLETE_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completeTime;
    @Column(name = "CANCEL_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cancelTime;
    @Column(name = "PAID_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paidTime;
    @Column(name = "NOTE")
    private String note;

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

    public Date getWorkDate() {
        return workDate;
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
    }

    public Date getPaidTime() {
        return paidTime;
    }

    public void setPaidTime(Date paidTime) {
        this.paidTime = paidTime;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
