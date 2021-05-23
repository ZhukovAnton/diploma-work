package com.stanum.skrudzh.service.order;

import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.dto.base.Ordered;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.OrderType;

import java.util.List;

public interface OrderService {

    boolean fillOrder(UserEntity user, OrderType orderType, EntityTypeEnum entityType, List<? extends Ordered> objects);

    void updateOrder(UserEntity user, OrderType orderType, EntityTypeEnum entityType, Long entityId, Integer order);

    void saveOrder(UserEntity user, OrderType orderType, EntityTypeEnum entityType, Long entityId, int order);

}
