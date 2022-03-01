package com.example.hourlymaids.repository;

import com.example.hourlymaids.entity.FeedbackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {

    @Query("select f, c.id from FeedbackEntity f inner join UserEntity c on f.userId = c.id where f.employeeId = ?1 and c.isDeleted = 0 and  f.isDeleted =0 order by f.createdDate desc ")
    List<Object[]> findAllFeedbackByEmployeeId(Long employeeId);

    List<FeedbackEntity> findFeedbackEntityByRateNumberAndEmployeeId(Integer rateNumber, Long employeeId);

     @Query("select distinct c.id from FeedbackEntity f inner join UserEntity c on f.userId = c.id where f.employeeId = ?1 and c.isDeleted = 0 and  f.isDeleted =0 ")
    List<Long> findAllFeedbackDistinctByEmployeeId(Long employeeId);

    List<FeedbackEntity> findByEmployeeId(Long employeeId);

}
