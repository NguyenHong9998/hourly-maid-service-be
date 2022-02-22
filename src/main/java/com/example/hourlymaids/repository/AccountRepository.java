package com.example.hourlymaids.repository;

import com.example.hourlymaids.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    AccountEntity findByEmailAndRoleId(String email, Long roleId);

    AccountEntity findByEmail(String email);

}
