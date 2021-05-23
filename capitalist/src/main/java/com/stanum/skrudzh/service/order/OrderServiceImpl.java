package com.stanum.skrudzh.service.order;

import com.stanum.skrudzh.jpa.model.OrderEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.OrderRepository;
import com.stanum.skrudzh.model.dto.base.Ordered;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.utils.TimeUtil;
import com.stanum.skrudzh.model.enums.OrderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    public static final long range = 10L;

    private final OrderRepository orderRepository;

    @Override
    public boolean fillOrder(UserEntity user,
                             OrderType orderType,
                             EntityTypeEnum entityType,
                             List<? extends Ordered> objects) {
        log.info("Fill order for userId = {}, orderType = {}, entityType = {}", user.getId(), orderType, entityType);
        List<OrderEntity> orders = orderRepository.findByUserAndOrderType(user, orderType);
        if(orders.isEmpty()) {
            return true;
        }
        orders.sort(Comparator.comparing(OrderEntity::getOrderPosition));
        Map<Long, Integer> map = createMap(orders, entityType);
        for(Ordered ordered : objects) {
            Integer rowOrder = map.get(ordered.getId());
            if(rowOrder != null) {
                ordered.setRowOrder(rowOrder);
            } else {
                log.info("OrderEntity with id = {} and type ={} , userId = {} does not exist, create new one.",
                        ordered.getId(),
                        entityType,
                        user.getId());

                saveLastOrderPosition(user, orderType, entityType, ordered.getId());
            }
        }
        return false;
    }

    @Override
    public void updateOrder(UserEntity user, OrderType orderType, EntityTypeEnum entityType, Long entityId, Integer order) {
        log.info("Update order for userId={}, entityType = {}, entityId={}, order={}",user.getId(), entityType, entityId, order);
        if(order == null) {
            saveLastOrderPosition(user, orderType, entityType, entityId);
        } else {
            List<OrderEntity> orders = orderRepository.findByUserAndOrderType(user, orderType);
            if(order >= orders.size() - 1) {
                saveLastOrderPosition(user, orderType, entityType, entityId);
                return;
            } else {
                orders.sort(Comparator.comparing(OrderEntity::getOrderPosition));
                Long leftOrderPosition = orders.get(order).getOrderPosition();
                Long rightOrderPosition = orders.get(order + 1).getOrderPosition();
                if ((rightOrderPosition - leftOrderPosition) < 2) {
                    reloadIndexes(orders);
                    updateOrder(user, orderType, entityType, entityId, order);
                } else {
                    Long orderPosition = leftOrderPosition + (rightOrderPosition - leftOrderPosition) / 2;
                    OrderEntity orderEntity;
                    Optional<OrderEntity> savedEntity = orderRepository.findByUserAndEntityIdAndEntityType(user, entityId ,entityType);
                    if(savedEntity.isPresent()) {
                        orderEntity = savedEntity.get();
                    } else {
                        orderEntity = new OrderEntity();
                        orderEntity.setEntityId(entityId);
                        orderEntity.setEntityType(entityType);
                        orderEntity.setUpdatedAt(TimeUtil.now());
                        orderEntity.setCreatedAt(TimeUtil.now());
                        orderEntity.setOrderType(orderType);
                        orderEntity.setUser(user);
                    }
                    orderEntity.setOrderPosition(orderPosition);
                    orderRepository.save(orderEntity);
                    return;
                }
            }
        }
    }

    @Override
    public void saveOrder(UserEntity user, OrderType orderType, EntityTypeEnum entityType, Long entityId, int order) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setEntityId(entityId);
        orderEntity.setEntityType(entityType);
        orderEntity.setUpdatedAt(TimeUtil.now());
        orderEntity.setCreatedAt(TimeUtil.now());
        orderEntity.setOrderType(orderType);
        orderEntity.setUser(user);
        orderEntity.setOrderPosition(order * range);

        orderRepository.save(orderEntity);
    }

    private void saveLastOrderPosition(UserEntity user, OrderType orderType, EntityTypeEnum entityType, Long entityId) {
        Long lastOrderPositionNumber = orderRepository.getLastOrderPositionNumber(user, orderType);
        if(lastOrderPositionNumber == null) {
            lastOrderPositionNumber = 0L;
        }

        OrderEntity orderEntity;
        Optional<OrderEntity> savedEntity = orderRepository.findByUserAndEntityIdAndEntityType(user, entityId, entityType);
        if(savedEntity.isPresent()) {
            orderEntity = savedEntity.get();
        } else {
            orderEntity = new OrderEntity();
            orderEntity.setEntityId(entityId);
            orderEntity.setEntityType(entityType);
            orderEntity.setUpdatedAt(TimeUtil.now());
            orderEntity.setOrderType(orderType);
            orderEntity.setUser(user);
        }
        orderEntity.setOrderPosition(lastOrderPositionNumber + range);
        orderRepository.save(orderEntity);
    }

    private Map<Long, Integer> createMap(List<OrderEntity> orders, EntityTypeEnum entityType) {
        Map<Long, Integer> orderedMap = new HashMap<>();
        for(int i = 0; i < orders.size(); i++) {
            OrderEntity orderEntity = orders.get(i);
            if(orderEntity.getEntityType() == entityType) {
                orderedMap.put(orderEntity.getEntityId(), i);
            }
        }
        return orderedMap;
    }

    private void reloadIndexes(List<OrderEntity> orders) {
        log.info("Reload order indexes");
        for(int i = 0; i < orders.size(); i++) {
            OrderEntity orderEntity = orders.get(i);
            orderEntity.setOrderPosition(i*range);
            orderRepository.save(orderEntity);
        }
    }
}
