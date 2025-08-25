package sit.tuvarna.bg.orderservice.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sit.tuvarna.bg.orderservice.bill.model.Bill;
import sit.tuvarna.bg.orderservice.bill.repository.BillRepository;
import sit.tuvarna.bg.orderservice.onlineOrder.model.DeliveryMethod;
import sit.tuvarna.bg.orderservice.onlineOrder.model.OnlineOrder;
import sit.tuvarna.bg.orderservice.onlineOrder.model.OnlineOrderStatus;
import sit.tuvarna.bg.orderservice.onlineOrder.model.PaymentMethod;
import sit.tuvarna.bg.orderservice.onlineOrder.repository.OnlineOrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class TableSeeder implements CommandLineRunner {

    private final BillRepository billRepository;
    private final OnlineOrderRepository onlineOrderRepository;

    @Override
    public void run(String... args) {
        //seedBills();
        //seedOnlineOrders();
    }

    private void seedBills() {

            Bill bill1 = new Bill();
            bill1.setUserId(UUID.randomUUID());
            bill1.setTotal(BigDecimal.valueOf(50));
            bill1.setDiscount(BigDecimal.ZERO);
            bill1.setTax(BigDecimal.valueOf(5));
            bill1.setFinalTotal(BigDecimal.valueOf(55));
            bill1.setPaymentMethod(PaymentMethod.CASH);
            bill1.setIssuedAt(LocalDateTime.now().minusHours(2)); // today

            Bill bill2 = new Bill();
            bill2.setUserId(UUID.randomUUID());
            bill2.setTotal(BigDecimal.valueOf(80));
            bill2.setDiscount(BigDecimal.valueOf(10));
            bill2.setTax(BigDecimal.valueOf(7));
            bill2.setFinalTotal(BigDecimal.valueOf(77));
            bill2.setPaymentMethod(PaymentMethod.CARD);
            bill2.setIssuedAt(LocalDateTime.now().minusHours(1)); // today

            billRepository.saveAll(List.of(bill1, bill2));
            System.out.println("Seeded Bills for today.");

    }

    private void seedOnlineOrders() {

            OnlineOrder order1 = new OnlineOrder();
            order1.setUserId(UUID.randomUUID());
            order1.setOnlineOrderStatus(OnlineOrderStatus.DELIVERED);
            order1.setTotal(BigDecimal.valueOf(30));
            order1.setDiscount(BigDecimal.ZERO);
            order1.setTax(BigDecimal.valueOf(3));
            order1.setFinalTotal(BigDecimal.valueOf(33));
            order1.setDeliveryMethod(DeliveryMethod.DELIVERY);
            order1.setPaymentMethod(PaymentMethod.CARD);
            order1.setCreatedAt(LocalDateTime.now().minusHours(3)); // today
            order1.setDeliveredAt(LocalDateTime.now().minusHours(2));

            OnlineOrder order2 = new OnlineOrder();
            order2.setUserId(UUID.randomUUID());
            order2.setOnlineOrderStatus(OnlineOrderStatus.DELIVERED);
            order2.setTotal(BigDecimal.valueOf(45));
            order2.setDiscount(BigDecimal.valueOf(5));
            order2.setTax(BigDecimal.valueOf(4));
            order2.setFinalTotal(BigDecimal.valueOf(44));
            order2.setDeliveryMethod(DeliveryMethod.PICKUP);
            order2.setPaymentMethod(PaymentMethod.CASH);
            order2.setCreatedAt(LocalDateTime.now().minusHours(4)); // today
            order2.setDeliveredAt(LocalDateTime.now().minusHours(3));

            onlineOrderRepository.saveAll(List.of(order1, order2));
            System.out.println("Seeded Online Orders for today.");

    }
}

