package be.kdg.sa.deliveryservice;

import be.kdg.sa.deliveryservice.infrastructure.courier.jpa.JpaCourierRepository;
import be.kdg.sa.deliveryservice.infrastructure.delivery.jpa.JpaDeliveryEntity;
import be.kdg.sa.deliveryservice.infrastructure.delivery.jpa.JpaDeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestHelper {
    @Autowired
    private JpaCourierRepository JpaCourierRepository;

    @Autowired
    private JpaDeliveryRepository jpaDeliveryRepository;



    public void saveDelivery() {
//        paOrderEntity orderEntity = jpaOrderRepository.findById(orderId.id()).orElseThrow(RuntimeException::new);
        JpaDeliveryEntity deliveryEntity = jpaDeliveryRepository.findById()
//        JpaOrderLineEntity orderLineEntity = new JpaOrderLineEntity(orderId.id(), productId.id(), price.amount(), quantity);
//        orderEntity.setLines(List.of(orderLineEntity));
//        jpaOrderRepository.save(orderEntity);
    }







    public void cleanUpCouriers() {
        JpaCourierRepository.deleteAll();
    }
    public void cleanUpDeliveries() {
        jpaDeliveryRepository.deleteAll();
    }
}
