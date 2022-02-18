package com.example.hourlymaids.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "LEAVE_DATE")
@Where(clause = "is_deleted = 0")
public class LeaveDateEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "LEAVE_DATE_SEQ", sequenceName = "LEAVE_DATE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "LEAVE_DATE_SEQ")
    private Long id;
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "START_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;
    @Column(name = "END_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date end;
    @Column(name = "NOTE")
    private String note;
    @Column(name = "LEAVE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date leaveDate;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


    public Date getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
