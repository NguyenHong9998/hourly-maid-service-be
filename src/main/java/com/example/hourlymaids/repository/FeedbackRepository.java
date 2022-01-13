package com.example.hourlymaids.repository;

import com.example.hourlymaids.entity.FeedbackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {

    @Query("select f from FeedbackEntity f where f.employeeId = ?1 and type = ?2")
    Page<FeedbackEntity> findAllFeedbackByEmployeeIdAndByType(Long employeeId, Integer type, Pageable pageable);
}
