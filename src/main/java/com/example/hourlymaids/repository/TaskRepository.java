package com.example.hourlymaids.repository;

import com.example.hourlymaids.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    @Query("select t from TaskEntity t where t.workDate = ?1 and t.completeTime < ?2")
    List<TaskEntity> findAllByDateAndEndTime(Date date, Date end);

    @Query("select t, c.avatar, c.fullName from TaskEntity t left join ClientEntity c on t.userId = c.id where c.fullName like %?1%")
    Page<Object[]> findAllWithPageable(String valueSearch, Pageable pageable);

    @Query("select t, c.avatar, c.fullName from TaskEntity t left join ClientEntity c on t.userId = c.id where c.fullName like %?1% and t.assignEmployeeTime is not null")
    Page<Object[]> findAssignedWithPageable(String valueSearch, Pageable pageable);

    @Query("select t, c.avatar, c.fullName from TaskEntity t left join ClientEntity c on t.userId = c.id where c.fullName like %?1% and t.assignEmployeeTime is null")
    Page<Object[]> findUnAssignedWithPageable(String valueSearch, Pageable pageable);

    @Query("select t, c.avatar, c.fullName from TaskEntity t left join ClientEntity c on t.userId = c.id where c.fullName like %?1% and t.cancelTime is not null ")
    Page<Object[]> findCanceledWithPageable(String valueSearch, Pageable pageable);

    @Query("select t, c.avatar, c.fullName from TaskEntity t left join ClientEntity c on t.userId = c.id where c.fullName like %?1% and t.paidTime is not null ")
    Page<Object[]> findPaidWithPageable(String valueSearch, Pageable pageable);
}
