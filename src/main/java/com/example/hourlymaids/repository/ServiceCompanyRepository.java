package com.example.hourlymaids.repository;

import com.example.hourlymaids.entity.ServiceCompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCompanyRepository extends JpaRepository<ServiceCompanyEntity, Long> {
    @Query("select s from ServiceCompanyEntity s where upper(s.serviceName) like %?1%")
    Page<ServiceCompanyEntity> findAllService(String valueSearch, Pageable pageable);

    ServiceCompanyEntity findByServiceName(String name);
}
