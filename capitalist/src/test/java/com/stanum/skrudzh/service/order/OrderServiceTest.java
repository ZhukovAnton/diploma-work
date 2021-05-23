package com.stanum.skrudzh.service.order;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.TestConfig;
import com.stanum.skrudzh.TestUtils;
import com.stanum.skrudzh.controller.*;
import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.repository.*;
import com.stanum.skrudzh.model.dto.Borrow;
import com.stanum.skrudzh.model.enums.BorrowTypeEnum;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.OrderType;
import com.stanum.skrudzh.utils.RequestUtil;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@SpringBootTest(classes = TestConfig.class)
public class OrderServiceTest extends IntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private ActiveRepository activeRepository;

    @Autowired
    private DebtsApiController debtsApiController;

    @Autowired
    private ActivesApiController activesApiController;

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private CreditsApiController creditsApiController;

    @Autowired
    private LoansApiController loansApiController;

    @Autowired
    private BasketsApiController basketsApiController;

    @Autowired
    private UserRepository userRepository;

    private UserEntity userEntity;
    private OrderType orderType = OrderType.ACTIVE_BORROW;

    @BeforeEach
    public void init() {
        super.init();
        userEntity = new UserEntity();
        userEntity.setId(user.getId());
        RequestUtil.setIosBuild("v2");
    }

    @Test
    public void shouldFillOrder() {
        OrderEntity orderEntity1 = buildOrder(0L, EntityTypeEnum.Borrow, orderType, 0L);
        OrderEntity orderEntity2 = buildOrder(1L, EntityTypeEnum.Borrow, orderType, 10L);
        OrderEntity orderEntity3 = buildOrder(2L, EntityTypeEnum.Borrow, orderType, 20L);
        OrderEntity orderEntity4 = buildOrder(3L, EntityTypeEnum.Borrow, orderType, 30L);
        OrderEntity orderEntity5 = buildOrder(0L, EntityTypeEnum.Active, orderType, 40L);
        OrderEntity orderEntity6 = buildOrder(1L, EntityTypeEnum.Active, orderType, 50L);
        orderRepository.save(orderEntity1);
        orderRepository.save(orderEntity2);
        orderRepository.save(orderEntity3);
        orderRepository.save(orderEntity4);
        orderRepository.save(orderEntity5);
        orderRepository.save(orderEntity6);

        List<Borrow> borrows = Arrays.asList(createBorrow(1L), createBorrow(3L), createBorrow(2L), createBorrow(0L));

        orderService.fillOrder(userEntity, orderType, EntityTypeEnum.Borrow, borrows);

        for(Borrow borrow : borrows) {
            Assert.assertNotNull(borrow.getRowOrder());
            Assert.assertTrue((long)borrow.getId() == borrow.getRowOrder());
        }
    }

    @Test
    public void shouldInsertLastElement_ifZeroOrderPosition() {
        Long lastOrderPositionNumber = orderRepository.getLastOrderPositionNumber(userEntity, orderType);
        if(lastOrderPositionNumber == null) {
            lastOrderPositionNumber = 0L;
        }
        orderService.updateOrder(userEntity, orderType, EntityTypeEnum.Borrow, 101L, null);

        OrderEntity orderEntity = orderRepository.findByUserAndEntityIdAndEntityType(userEntity, 101L, EntityTypeEnum.Borrow).get();
        Assert.assertEquals(lastOrderPositionNumber + OrderServiceImpl.range, (long)orderEntity.getOrderPosition());
    }

    @Test
    public void shouldInsertInTheMiddle() {
        Long lastPos = orderRepository.getLastOrderPositionNumber(userEntity, orderType);
        if(lastPos == null) {
            lastPos = 0L;
        }
        OrderEntity orderEntity1 = buildOrder(0L, EntityTypeEnum.Borrow, orderType, lastPos); lastPos+=OrderServiceImpl.range;
        OrderEntity orderEntity2 = buildOrder(1L, EntityTypeEnum.Borrow, orderType, lastPos);lastPos+=OrderServiceImpl.range;
        OrderEntity orderEntity3 = buildOrder(2L, EntityTypeEnum.Borrow, orderType, lastPos);lastPos+=OrderServiceImpl.range;
        OrderEntity orderEntity4 = buildOrder(3L, EntityTypeEnum.Borrow, orderType, lastPos);lastPos+=OrderServiceImpl.range;
        OrderEntity orderEntity5 = buildOrder(0L, EntityTypeEnum.Active, orderType, lastPos);lastPos+=OrderServiceImpl.range;
        OrderEntity orderEntity6 = buildOrder(1L, EntityTypeEnum.Active, orderType, lastPos);lastPos+=OrderServiceImpl.range;
        orderRepository.save(orderEntity1);
        orderRepository.save(orderEntity2);
        orderRepository.save(orderEntity3);
        orderRepository.save(orderEntity4);
        orderRepository.save(orderEntity5);
        orderRepository.save(orderEntity6);

        List<OrderEntity> byUserAndOrderType = orderRepository.findByUserAndOrderType(userEntity, orderType);
        byUserAndOrderType.sort(Comparator.comparing(OrderEntity::getOrderPosition));

        Long posStart = byUserAndOrderType.get(3).getOrderPosition();
        Long posEnd = byUserAndOrderType.get(4).getOrderPosition();

        orderService.updateOrder(userEntity, orderType, EntityTypeEnum.Borrow, 190L, 3);
        orderService.updateOrder(userEntity, orderType, EntityTypeEnum.Borrow, 191L, 3);

        OrderEntity orderEntity = orderRepository.findByUserAndEntityIdAndEntityType(userEntity, 190L, EntityTypeEnum.Borrow).get();
        Assert.assertEquals(posStart + (posEnd - posStart)/2, (long)orderEntity.getOrderPosition());
        OrderEntity orderEntity11 = orderRepository.findByUserAndEntityIdAndEntityType(userEntity, 191L, EntityTypeEnum.Borrow).get();
        Assert.assertEquals(posStart + (posEnd - posStart)/2/2, (long)orderEntity11.getOrderPosition());
    }

    @Test
    public void shouldRearrangeOrder_ifNoSpaceBetweenValues() {
        OrderEntity orderEntity1 = buildOrder(0L, EntityTypeEnum.Borrow, orderType, 0L);
        OrderEntity orderEntity2 = buildOrder(1L, EntityTypeEnum.Borrow, orderType, 100L);
        OrderEntity orderEntity3 = buildOrder(2L, EntityTypeEnum.Borrow, orderType, 200L);
        OrderEntity orderEntity4 = buildOrder(3L, EntityTypeEnum.Borrow, orderType, 300L);
        OrderEntity orderEntity5 = buildOrder(0L, EntityTypeEnum.Active, orderType, 301L);
        OrderEntity orderEntity6 = buildOrder(1L, EntityTypeEnum.Active, orderType, 400L);
        orderRepository.save(orderEntity1);
        orderRepository.save(orderEntity2);
        orderRepository.save(orderEntity3);
        orderRepository.save(orderEntity4);
        orderRepository.save(orderEntity5);
        orderRepository.save(orderEntity6);

        List<Borrow> borrows = Arrays.asList(createBorrow(1L), createBorrow(3L), createBorrow(2L), createBorrow(0L));

        orderService.updateOrder(userEntity, orderType, EntityTypeEnum.Borrow, 10L, 3);
        List<OrderEntity> orders = orderRepository.findByUserAndOrderType(userEntity, orderType);
        Assert.assertEquals(7, orders.size());
        orders.sort(Comparator.comparing(OrderEntity::getOrderPosition));

        Assert.assertEquals(50L, (long)orders.get(orders.size() -1).getOrderPosition());

        OrderEntity orderEntity = orderRepository.findByUserAndEntityIdAndEntityType(userEntity, 10L, EntityTypeEnum.Borrow).get();
        Assert.assertEquals(35L, (long)orderEntity.getOrderPosition());
    }

    @Test
    public void shouldUpdateOrder_ifOrderEntityExist() {
        OrderEntity orderEntity1 = buildOrder(50L, EntityTypeEnum.Borrow, orderType, 0L);
        OrderEntity orderEntity2 = buildOrder(51L, EntityTypeEnum.Borrow, orderType, 10L);
        OrderEntity orderEntity3 = buildOrder(52L, EntityTypeEnum.Borrow, orderType, 20L);
        OrderEntity orderEntity4 = buildOrder(53L, EntityTypeEnum.Borrow, orderType, 30L);
        OrderEntity orderEntity5 = buildOrder(60L, EntityTypeEnum.Active, orderType, 31L);
        OrderEntity orderEntity6 = buildOrder(61L, EntityTypeEnum.Active, orderType, 40L);
        orderRepository.save(orderEntity1);
        orderRepository.save(orderEntity2);
        orderRepository.save(orderEntity3);
        orderRepository.save(orderEntity4);
        orderRepository.save(orderEntity5);
        orderRepository.save(orderEntity6);

        orderService.updateOrder(userEntity, orderType, EntityTypeEnum.Borrow, 51L, 3);
        List<OrderEntity> orders = orderRepository.findByUserAndOrderType(userEntity, orderType);
        Assert.assertEquals(6, orders.size());
        boolean checked = false;
        for(OrderEntity order : orders) {
            if(order.getEntityType() == EntityTypeEnum.Borrow && order.getEntityId() == 51L) {
                checked = true;
                Assert.assertEquals(35L, (long)order.getOrderPosition());
            }
        }
        Assert.assertTrue(checked);
    }

    @Test
    public void shouldFillOrder_whenGetActives() {
        BorrowEntity borrowEntity1 = saveBorrow(BorrowTypeEnum.Debt);
        BorrowEntity borrowEntity2 = saveBorrow(BorrowTypeEnum.Debt);
        BorrowEntity borrowEntity3 = saveBorrow(BorrowTypeEnum.Debt);
        BorrowEntity borrowEntity4 = saveBorrow(BorrowTypeEnum.Debt);

        ActiveEntity activeEntity1 = saveActive(0);
        ActiveEntity activeEntity2 = saveActive(1);
        ActiveEntity activeEntity3 = saveActive(2);

        List<OrderEntity> order = orderRepository.findByUserAndOrderType(userEntity, OrderType.ACTIVE_BORROW);
        Assert.assertTrue(order.isEmpty());

        activesApiController.getActivesByUser(userEntity.getId(), "");

        List<OrderEntity> filledOrder = orderRepository.findByUserAndOrderType(userEntity, OrderType.ACTIVE_BORROW);
        Assert.assertFalse(filledOrder.isEmpty());
        Assert.assertEquals(7, filledOrder.size());
        for(OrderEntity orderEntity : filledOrder) {
            if(orderEntity.getEntityType() == EntityTypeEnum.Active && orderEntity.getEntityId().equals(activeEntity1.getId())) {
                Assert.assertEquals(0L, (long) orderEntity.getOrderPosition());
            }
            if(orderEntity.getEntityType() == EntityTypeEnum.Active && orderEntity.getEntityId().equals(activeEntity2.getId())) {
                Assert.assertEquals(OrderServiceImpl.range, (long) orderEntity.getOrderPosition());
            }
            if(orderEntity.getEntityType() == EntityTypeEnum.Active && orderEntity.getEntityId().equals(activeEntity3.getId())) {
                Assert.assertEquals(OrderServiceImpl.range * 2, (long) orderEntity.getOrderPosition());
            }
        }
    }

    @Test
    public void shouldFillOrder_whenGetDebts() {
        BorrowEntity borrowEntity1 = saveBorrow(BorrowTypeEnum.Debt);
        BorrowEntity borrowEntity2 = saveBorrow(BorrowTypeEnum.Debt);
        BorrowEntity borrowEntity3 = saveBorrow(BorrowTypeEnum.Debt);
        BorrowEntity borrowEntity4 = saveBorrow(BorrowTypeEnum.Debt);

        ActiveEntity activeEntity1 = saveActive(0);
        ActiveEntity activeEntity2 = saveActive(1);
        ActiveEntity activeEntity3 = saveActive(2);

        List<OrderEntity> order = orderRepository.findByUserAndOrderType(userEntity, OrderType.ACTIVE_BORROW);
        Assert.assertTrue(order.isEmpty());

        debtsApiController.indexUsersDebts(userEntity.getId(), "");

        List<OrderEntity> filledOrder = orderRepository.findByUserAndOrderType(userEntity, OrderType.ACTIVE_BORROW);
        Assert.assertFalse(filledOrder.isEmpty());
        Assert.assertEquals(7, filledOrder.size());
        for(OrderEntity orderEntity : filledOrder) {
            if(orderEntity.getEntityType() == EntityTypeEnum.Active && orderEntity.getEntityId().equals(activeEntity1.getId())) {
                Assert.assertEquals(0L, (long) orderEntity.getOrderPosition());
            }
            if(orderEntity.getEntityType() == EntityTypeEnum.Active && orderEntity.getEntityId().equals(activeEntity2.getId())) {
                Assert.assertEquals(OrderServiceImpl.range, (long) orderEntity.getOrderPosition());
            }
            if(orderEntity.getEntityType() == EntityTypeEnum.Active && orderEntity.getEntityId().equals(activeEntity3.getId())) {
                Assert.assertEquals(OrderServiceImpl.range * 2, (long) orderEntity.getOrderPosition());
            }
        }
    }

    @Test
    public void shouldNotFillOrder_ifClient_DoesntHave_GlobalSorting() {
        RequestUtil.setIosBuild(null);

        BorrowEntity borrowEntity1 = saveBorrow(BorrowTypeEnum.Debt);
        BorrowEntity borrowEntity2 = saveBorrow(BorrowTypeEnum.Debt);
        BorrowEntity borrowEntity3 = saveBorrow(BorrowTypeEnum.Debt);
        BorrowEntity borrowEntity4 = saveBorrow(BorrowTypeEnum.Debt);

        ActiveEntity activeEntity1 = saveActive(0);
        ActiveEntity activeEntity2 = saveActive(1);
        ActiveEntity activeEntity3 = saveActive(2);

        List<OrderEntity> order = orderRepository.findByUserAndOrderType(userEntity, OrderType.ACTIVE_BORROW);
        Assert.assertTrue(order.isEmpty());

        debtsApiController.indexUsersDebts(userEntity.getId(), "");

        List<OrderEntity> filledOrder = orderRepository.findByUserAndOrderType(userEntity, OrderType.ACTIVE_BORROW);
        Assert.assertTrue(filledOrder.isEmpty());
    }

    @Test
    public void shouldReturnOldOrder_ifClient_DoesntHave_GlobalSorting() {
        RequestUtil.setIosBuild(null);

        BorrowEntity borrowEntity1 = saveBorrow(BorrowTypeEnum.Debt);
        BorrowEntity borrowEntity2 = saveBorrow(BorrowTypeEnum.Debt);
        BorrowEntity borrowEntity3 = saveBorrow(BorrowTypeEnum.Debt);
        BorrowEntity borrowEntity4 = saveBorrow(BorrowTypeEnum.Debt);
        BorrowEntity borrowEntity5 = saveBorrow(BorrowTypeEnum.Loan);
        BorrowEntity borrowEntity6 = saveBorrow(BorrowTypeEnum.Loan);

        ActiveEntity activeEntity3 = saveActive(2);
        ActiveEntity activeEntity1 = saveActive(0);
        ActiveEntity activeEntity2 = saveActive(1);

        List<OrderEntity> order = orderRepository.findByUserAndOrderType(userEntity, OrderType.ACTIVE_BORROW);
        Assert.assertTrue(order.isEmpty());

        debtsApiController.indexUsersDebts(userEntity.getId(), "");

        List<OrderEntity> filledOrder = orderRepository.findByUserAndOrderType(userEntity, OrderType.ACTIVE_BORROW);
        Assert.assertTrue(filledOrder.isEmpty());

        Set<ActiveEntity> activeEntities = activeRepository.getAllByUser(userEntity);
        for(ActiveEntity activeEntity : activeEntities) {
            if(activeEntity.getId().equals(activeEntity3.getId())) {
                Assert.assertEquals(2, (int)activeEntity.getRowOrder());
            }
            if(activeEntity.getId().equals(activeEntity2.getId())) {
                Assert.assertEquals(1, (int)activeEntity.getRowOrder());
            }
            if(activeEntity.getId().equals(activeEntity1.getId())) {
                Assert.assertEquals(0, (int)activeEntity.getRowOrder());
            }
        }
    }

    @Test
    public void shouldSaveOrder_whenCreateCreditAndLoans() {
        creditsApiController.createCreditByUser(user.getId(), "", TestUtils.createCreditForm("cr1"));
        creditsApiController.createCreditByUser(user.getId(), "", TestUtils.createCreditForm("cr2"));
        creditsApiController.createCreditByUser(user.getId(), "", TestUtils.createCreditForm("cr3"));
        creditsApiController.createCreditByUser(user.getId(), "", TestUtils.createCreditForm("cr4"));

        debtsApiController.usersUserIdDebtsPost(user.getId(), "", TestUtils.createDebtForm("debt1"));
        debtsApiController.usersUserIdDebtsPost(user.getId(), "", TestUtils.createDebtForm("debt2"));
        debtsApiController.usersUserIdDebtsPost(user.getId(), "", TestUtils.createDebtForm("debt3"));

        loansApiController.createLoan(user.getId(), "", TestUtils.createLoanForm("loan1"));
        loansApiController.createLoan(user.getId(), "", TestUtils.createLoanForm("loan2"));
        loansApiController.createLoan(user.getId(), "", TestUtils.createLoanForm("loan3"));

        List<OrderEntity> orderCredits = orderRepository.findByUserAndOrderType(userEntity, OrderType.CREDIT_BORROW);
        Assert.assertEquals(7, orderCredits.size());
        for(OrderEntity orderEntity : orderCredits) {
            Assert.assertNotNull(orderEntity.getOrderPosition());
        }

        List<OrderEntity> orderActives = orderRepository.findByUserAndOrderType(userEntity, OrderType.ACTIVE_BORROW);
        Assert.assertEquals(3, orderActives.size());
        for(OrderEntity orderEntity : orderActives) {
            Assert.assertNotNull(orderEntity.getOrderPosition());
        }
    }

    @Test
    public void shouldSaveOrder_whenCreateActiveAndDebt() {
        UserEntity userEntityFromRepo = userRepository.findById(userEntity.getId()).get();
        userEntityFromRepo.setHasActiveSubscription(true);
        userRepository.save(userEntityFromRepo);

        BasketEntity basketEntity = new BasketEntity();
        basketEntity.setUser(userEntityFromRepo);
        basketEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        basketEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        basketRepository.save(basketEntity);

        activesApiController.basketsBasketIdActivesPost(basketEntity.getId(), "", TestUtils.createActiveForm("ac1"));
        activesApiController.basketsBasketIdActivesPost(basketEntity.getId(), "", TestUtils.createActiveForm("ac2"));
        activesApiController.basketsBasketIdActivesPost(basketEntity.getId(), "", TestUtils.createActiveForm("ac3"));
        activesApiController.basketsBasketIdActivesPost(basketEntity.getId(), "", TestUtils.createActiveForm("ac4"));
        activesApiController.basketsBasketIdActivesPost(basketEntity.getId(), "", TestUtils.createActiveForm("ac5"));

        creditsApiController.createCreditByUser(user.getId(), "", TestUtils.createCreditForm("cr9"));
        creditsApiController.createCreditByUser(user.getId(), "", TestUtils.createCreditForm("cr10"));
        creditsApiController.createCreditByUser(user.getId(), "", TestUtils.createCreditForm("cr11"));
        creditsApiController.createCreditByUser(user.getId(), "", TestUtils.createCreditForm("cr12"));

        debtsApiController.usersUserIdDebtsPost(user.getId(), "", TestUtils.createDebtForm("debt6"));
        debtsApiController.usersUserIdDebtsPost(user.getId(), "", TestUtils.createDebtForm("debt7"));
        debtsApiController.usersUserIdDebtsPost(user.getId(), "", TestUtils.createDebtForm("debt8"));

        loansApiController.createLoan(user.getId(), "", TestUtils.createLoanForm("loan10"));
        loansApiController.createLoan(user.getId(), "", TestUtils.createLoanForm("loan21"));
        loansApiController.createLoan(user.getId(), "", TestUtils.createLoanForm("loan31"));

        List<OrderEntity> orderCredits = orderRepository.findByUserAndOrderType(userEntity, OrderType.ACTIVE_BORROW);
        Assert.assertEquals(8, orderCredits.size());
        for(OrderEntity orderEntity : orderCredits) {
            Assert.assertNotNull(orderEntity.getOrderPosition());
        }

        List<OrderEntity> orderActives = orderRepository.findByUserAndOrderType(userEntity, OrderType.CREDIT_BORROW);
        Assert.assertEquals(7, orderActives.size());
        for(OrderEntity orderEntity : orderActives) {
            Assert.assertNotNull(orderEntity.getOrderPosition());
        }
    }

    private OrderEntity buildOrder(Long entityId, EntityTypeEnum entityType, OrderType orderType, Long orderPosition) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setEntityId(entityId);
        orderEntity.setEntityType(entityType);
        orderEntity.setOrderType(orderType);
        orderEntity.setUser(userEntity);
        orderEntity.setOrderPosition(orderPosition);
        return orderEntity;
    }

    private Borrow createBorrow(Long id) {
        BorrowEntity entity = new BorrowEntity();
        entity.setUser(userEntity);
        entity.setAmountCents(new BigDecimal("1"));

        Borrow b = new Borrow(entity);
        b.setId(id);
        return b;
    }

    public BorrowEntity saveBorrow(BorrowTypeEnum borrowType) {
        BorrowEntity entity = new BorrowEntity();
        entity.setUser(userEntity);
        entity.setType(borrowType);
        entity.setAmountCents(new BigDecimal("1"));
        entity.setAmountCurrency("RUB");
        entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return borrowRepository.save(entity);
    }

    public ActiveEntity saveActive(int rowOrder) {
        BasketEntity basketEntity = new BasketEntity();
        basketEntity.setUser(userEntity);
        basketEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        basketEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        basketRepository.save(basketEntity);

        ActiveEntity activeEntity = new ActiveEntity();
        activeEntity.setRowOrder(rowOrder);
        activeEntity.setCostCents(new BigDecimal("1"));
        activeEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        activeEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        activeEntity.setAlreadyPaidCents(new BigDecimal("1"));
        activeEntity.setBasketEntity(basketEntity);
        activeEntity.setCurrency("RUB");
        return activeRepository.save(activeEntity);
    }

    public CreditEntity saveCredit() {
        CreditEntity creditEntity = new CreditEntity();
        creditEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        creditEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        creditEntity.setAlreadyPaidCents(new BigDecimal("1"));
        creditEntity.setCurrency("RUB");
        creditEntity.setReturnAmountCents(new BigDecimal("1"));
        creditEntity.setAmountCents(new BigDecimal("1"));
        return creditRepository.save(creditEntity);
    }

}
