package com.example.hourlymaids.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.hourlymaids.entity.RoleEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByName(String name);
}
