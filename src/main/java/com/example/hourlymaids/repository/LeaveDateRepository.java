package com.example.hourlymaids.repository;

import com.example.hourlymaids.entity.LeaveDateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LeaveDateRepository extends JpaRepository<LeaveDateEntity, Long> {
    @Query("select ld, u.fullName, u.avatar from LeaveDateEntity ld left join UserEntity u on ld.userId = u.id where ld.leaveDate =?1 and u.fullName like %?2%")
    Page<Object[]> findByLeaveDate(Date start, String valueSearch, Pageable pageable);

    @Query("select distinct l.leaveDate from  LeaveDateEntity l")
    List<Date> getListLeaveDate();
//
//    @Query("select ld, u.fullName, u.avatar, u.id from LeaveDateEntity ld left join UserEntity u on ld.userId = u.id where ld.id =?1")
//    List<Object> getLeaveDateInform(Long leaveId);
}
