package com.example.hourlymaids.repository;

import com.example.hourlymaids.entity.ServiceCompanyEntity;
import com.example.hourlymaids.entity.ServiceDiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceDiscountRepository extends JpaRepository<ServiceDiscountEntity, Long> {
    void deleteByDiscountId(Long discountId);

    @Query(value = "select s, sd.salePercentage from ServiceDiscountEntity sd inner join ServiceCompanyEntity s " +
            " on (sd.serviceId = s.id) where sd.discountId = ?1")
    List<Object[]> findByDiscountId(Long discountId);
}
