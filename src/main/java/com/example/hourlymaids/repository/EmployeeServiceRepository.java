package com.example.hourlymaids.repository;

import com.example.hourlymaids.entity.EmployeeServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeServiceRepository extends JpaRepository<EmployeeServiceEntity, Long> {
    List<EmployeeServiceEntity> findByUserId(Long userId);

    List<EmployeeServiceEntity> findByServiceId(Long serviceId);


    void deleteByUserId(Long userId);
}
