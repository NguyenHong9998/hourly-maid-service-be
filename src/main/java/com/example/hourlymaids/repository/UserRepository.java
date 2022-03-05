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
    UserEntity findByEmail(String email);

    UserEntity findByEmailAndRoleId(String email, Long role);

    UserEntity findByPhoneNumber(String phone);

    @Query("select u, r.name from UserEntity u " +
            " inner join RoleEntity r on ( u.roleId = r.id and u.roleId not in(4) ) where upper(u.email) like %?1% ")
    Page<Object[]> findAllUser(String valueSearch, Pageable pageable);

    @Query("select u, r.name from UserEntity u  " +
            " inner join RoleEntity r on ( u.roleId = r.id and u.roleId not in(4) ) where upper(u.email) like %?2% and r.id = ?1")
    Page<Object[]> findAllUserAndStatus(Long role, String valueSearch, Pageable pageable);

    @Query("select u from UserEntity u  where u.status = 1 and u.id not in ?1 and u.roleId = 3")
    List<UserEntity> findUserNotInListIds(List<Long> ids);

    @Query("select u from UserEntity u  where u.status = 1 and u.roleId = 3 ")
    List<UserEntity> findUserHasStatusNotBlock();
}
