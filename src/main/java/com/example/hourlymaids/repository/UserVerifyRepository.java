package com.example.hourlymaids.repository;

import com.example.hourlymaids.entity.VerifyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVerifyRepository extends JpaRepository<VerifyEntity, Long> {
    VerifyEntity findByUserId(Long userId);
    VerifyEntity findByVerifyEmailTokenAndUserId(String token, Long userId);
    VerifyEntity findByVerifyPhoneTokenAndUserId(String token, Long userId);

    VerifyEntity findByVerifyResetPassToken(String token);
}
