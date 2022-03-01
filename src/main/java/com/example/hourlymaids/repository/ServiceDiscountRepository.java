package com.example.hourlymaids.repository;

import com.example.hourlymaids.entity.DiscountEntity;
import com.example.hourlymaids.entity.ServiceDiscountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = "select d, sd.salePercentage from ServiceDiscountEntity  sd inner join DiscountEntity d on sd.discountId = d.id where sd.serviceId = ?1 ")
    Page<Object[]> getListDiscountOfService(Long serviceId, Pageable pageable);

    @Query(value = "select sd from ServiceDiscountEntity  sd left join DiscountEntity d on sd.discountId = d.id where sd.serviceId = ?1 and d.isPublic = 1")
    List<ServiceDiscountEntity> findByServiceId(Long serviceId);


    @Query(value = "select d, sd.salePercentage from ServiceDiscountEntity  sd left join DiscountEntity d on sd.discountId = d.id where sd.serviceId = ?1 and d.isPublic = 1 ")
    List<Object[]> findDiscountByServiceId(Long serviceId);


}
