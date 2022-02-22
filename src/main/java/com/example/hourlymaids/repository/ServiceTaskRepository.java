package com.example.hourlymaids.repository;

import com.example.hourlymaids.entity.ServiceTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceTaskRepository extends JpaRepository<ServiceTaskEntity, Long> {

//    @Query(value = "select s, d from ServiceTaskEntity st left join ServiceCompanyEntity s on st.serviceId = s.id" +
//            " left join DiscountEntity d on (st.serviceDiscountId = d.id ) where st.taskId = ?1")
//    List<Object[]> getServiceTaskEntitiesByTaskId(Long taskId);

    List<ServiceTaskEntity> findByTaskId(Long taskId);
}
