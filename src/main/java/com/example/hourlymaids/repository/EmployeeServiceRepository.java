package com.example.hourlymaids.repository;

import com.example.hourlymaids.entity.EmployeeServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeServiceRepository extends JpaRepository<EmployeeServiceEntity, Long> {
    List<EmployeeServiceEntity> findByUserId(Long userId);

    @Query("select u, es.level from EmployeeServiceEntity es left join UserEntity u on es.userId = u.id where es.serviceId = ?1")
    Page<Object[]> findByServiceId(Long serviceId, Pageable pageable);

    List<EmployeeServiceEntity> findByServiceId(Long serviceId);

    void deleteByUserId(Long userId);

    EmployeeServiceEntity findByServiceIdAndAndUserId(Long serviceId, Long userId);

}
