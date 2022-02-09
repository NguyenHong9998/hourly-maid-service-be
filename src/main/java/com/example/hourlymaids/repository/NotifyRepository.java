package com.example.hourlymaids.repository;

import com.example.hourlymaids.entity.NotifyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotifyRepository extends JpaRepository<NotifyEntity, Long> {
    @Query("select n from NotifyEntity n where upper(n.title) like %?1%")
    Page<NotifyEntity> findAllNotify(String valueSearch, Pageable pageable);

    @Query("select n from NotifyEntity n where upper(n.title) like %?2% and n.status = ?1")
    Page<NotifyEntity> findAllNotifyAndStatus(Integer status, String valueSearch, Pageable pageable);

    @Query("select n from NotifyEntity n where n.id in ?1")
    List<NotifyEntity> findAllByIds(List<Long> ids);
}
