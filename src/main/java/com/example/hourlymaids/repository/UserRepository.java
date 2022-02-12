package com.example.hourlymaids.repository;

import com.example.hourlymaids.entity.NotifyEntity;
import com.example.hourlymaids.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByAccountId(Long accountId);

    UserEntity findByPhoneNumber(String phone);

    @Query("select u, a.email, r.name from UserEntity u inner join AccountEntity a on u.accountId = a.id " +
            " inner join RoleEntity r on a.roleId = r.id where upper(a.email) like %?1%")
    Page<Object[]> findAllUser(String valueSearch, Pageable pageable);

    @Query("select u, a.email, r.name from UserEntity u inner join AccountEntity a on u.accountId = a.id " +
            " inner join RoleEntity r on a.roleId = r.id where upper(a.email) like %?2% and r.id = ?1")
    Page<Object[]> findAllUserAndStatus(Long role, String valueSearch, Pageable pageable);
}
