package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.OrderEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>  {

    List<OrderEntity> findByUserAndOrderType(UserEntity user, OrderType orderType);

    Optional<OrderEntity> findByUserAndEntityIdAndEntityType(UserEntity user, Long entityId, EntityTypeEnum entityType);

    @Query("select max(orderPosition) from OrderEntity where orderType = :orderType and user = :userEntity")
    Long getLastOrderPositionNumber(UserEntity userEntity, OrderType orderType);

}
