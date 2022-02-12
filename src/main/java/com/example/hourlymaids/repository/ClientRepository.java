package com.example.hourlymaids.repository;

import com.example.hourlymaids.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
}
