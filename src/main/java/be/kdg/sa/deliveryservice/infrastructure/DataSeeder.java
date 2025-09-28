
package be.kdg.sa.deliveryservice.infrastructure;

import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import be.kdg.sa.deliveryservice.domain.order.OrderId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataSeeder {

    private final static String Courier1Id = "550e8400-e29b-41d4-a716-446655440001";
    private final static String Courier2Id = "550e8400-e29b-41d4-a716-446655440002";
    private final static String Order1Id = "660e8400-e29b-41d4-a716-446655440001";
    private final static String Order2Id = "660e8400-e29b-41d4-a716-446655440002";
    private final static String Order3Id = "660e8400-e29b-41d4-a716-446655440003";
    private final static String Order4Id = "660e8400-e29b-41d4-a716-446655440004";
    private final static String Delivery1Id = "770e8400-e29b-41d4-a716-446655440001";
    private final static String Delivery2Id = "770e8400-e29b-41d4-a716-446655440002";
    private final static String Delivery3Id = "770e8400-e29b-41d4-a716-446655440003";
    private final static String Delivery4Id = "770e8400-e29b-41d4-a716-446655440004";


    public static List<Delivery> seedDeliveries() {
        List<Delivery> deliveries = new ArrayList<>();

        // Create courier IDs
        CourierId courierId1 = new CourierId(UUID.fromString(Courier1Id));
        CourierId courierId2 = new CourierId(UUID.fromString(Courier2Id));

        // Create delivery 1 - successful delivery by courier 2
        deliveries.add(createDelivery(courierId2, Delivery1Id, Order1Id));

        // Create delivery 2 - successful delivery by courier 2
        deliveries.add(createDelivery(courierId2, Delivery2Id, Order2Id));

        // Create delivery 3 - successful delivery by courier 1
        deliveries.add(createDelivery(courierId1, Delivery3Id, Order3Id));

        // Create delivery 4 - active delivery by courier 2 (not successful yet)
        deliveries.add(createDelivery(courierId2, Delivery4Id, Order4Id));

        return deliveries;
    }

    private static Delivery createDelivery(CourierId courierId2, String deliveryId, String orderId) {
        Delivery delivery1 = new Delivery(
                new DeliveryId(UUID.fromString(deliveryId)),
                new OrderId(UUID.fromString(orderId))
        );
        delivery1.setCourierId(courierId2);
        return delivery1;
    }

    public static List<Courier> seedCouriers() {
        List<Courier> couriers = new ArrayList<>();

        // Courier 1 - no active delivery
        Courier courier1 = new Courier(new CourierId(UUID.fromString(Courier1Id)));
        // currentOrderId remains null (no active delivery)
        couriers.add(courier1);

        // Courier 2 - has active delivery
        Courier courier2 = new Courier(new CourierId(UUID.fromString(Courier2Id)));
        courier2.setOrderId(new OrderId(UUID.fromString(Order4Id)));
        couriers.add(courier2);

        return couriers;
    }
}