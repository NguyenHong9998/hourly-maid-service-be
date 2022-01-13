package com.example.hourlymaids.repository;

import com.example.hourlymaids.entity.DiscountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository

public interface DiscountRepository extends JpaRepository<DiscountEntity, Long> {
    @Query("select d from DiscountEntity d where upper(d.title) like %?1%")
    Page<DiscountEntity> findAllDiscount(String valueSearch, Pageable pageable);
}
