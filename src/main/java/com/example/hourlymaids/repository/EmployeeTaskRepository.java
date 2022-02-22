package com.example.hourlymaids.repository;

import com.example.hourlymaids.entity.EmployeeTaskEntity;
import com.example.hourlymaids.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeTaskRepository extends JpaRepository<EmployeeTaskEntity, Long> {
    @Query(value = "select sd.employee_id, count(sd.task_id) as x from \"EMPLOYEE_TASK\" sd where sd.employee_id in ?1 " +
            "group by sd.employee_id order by x asc", nativeQuery = true)
    List<Object[]> findEmployeeGroupByTask(List<Long> userId);

    @Query(value = "select u from EmployeeTaskEntity et left join UserEntity u on (et.employeeId = u.id) where et.taskId = ?1 ")
    List<UserEntity> findEmployeeOfTask(Long taskId);

    @Modifying
    @Query(value = "delete from EmployeeTaskEntity et where et.taskId = ?1")
    void deleteAllByTaskId(Long taskId);
}
