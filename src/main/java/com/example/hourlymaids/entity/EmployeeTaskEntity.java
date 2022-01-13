package com.example.hourlymaids.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "EMPLOYEE_TASK")
@Where(clause = "is_deleted = 0")
public class EmployeeTaskEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "EMPLOYEE_TASK_SEQ", sequenceName = "EMPLOYEE_TASK_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "EMPLOYEE_TASK_SEQ")
    private Long id;
    @Column(name = "TASK_ID")
    private Long taskId;
    @Column(name = "EMPLOYEE_ID")
    private Long employeeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
}
