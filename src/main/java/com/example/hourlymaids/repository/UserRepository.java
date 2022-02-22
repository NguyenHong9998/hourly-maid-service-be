package com.example.hourlymaids.repository;

import com.example.hourlymaids.entity.NotifyEntity;
import com.example.hourlymaids.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Query("select u from UserEntity u left join AccountEntity c on (u.accountId = c.id and c.roleId = 3)where u.status = 1 and u.id not in ?1")
    List<UserEntity> findUserNotInListIds(List<Long> ids);

    @Query("select u from UserEntity u left join AccountEntity c on (u.accountId = c.id and c.roleId = 3)where u.status = 1 ")
    List<UserEntity> findUserHasStatusNotBlock();


}
