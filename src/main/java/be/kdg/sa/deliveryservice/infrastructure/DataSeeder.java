
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

    public final static UUID Courier1Id = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    public final static UUID Courier2Id = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    public final static UUID Order1Id = UUID.fromString("660e8400-e29b-41d4-a716-446655440001");
    public final static UUID Order2Id = UUID.fromString("660e8400-e29b-41d4-a716-446655440002");
    public final static UUID Order3Id = UUID.fromString("660e8400-e29b-41d4-a716-446655440003");
    public final static UUID Order4Id = UUID.fromString("660e8400-e29b-41d4-a716-446655440004");
    public final static UUID Delivery1Id = UUID.fromString("770e8400-e29b-41d4-a716-446655440001");
    public final static UUID Delivery2Id = UUID.fromString("770e8400-e29b-41d4-a716-446655440002");
    public final static UUID Delivery3Id = UUID.fromString("770e8400-e29b-41d4-a716-446655440003");
    public final static UUID Delivery4Id = UUID.fromString("770e8400-e29b-41d4-a716-446655440004");


    public static List<Delivery> seedDeliveries() {
        List<Delivery> deliveries = new ArrayList<>();

        // Create courier IDs
        CourierId courierId1 = new CourierId(Courier1Id);
        CourierId courierId2 = new CourierId(Courier2Id);

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

    private static Delivery createDelivery(CourierId courierId2, UUID deliveryId, UUID orderId) {
        Delivery delivery1 = new Delivery(
                new DeliveryId(deliveryId),
                new OrderId(orderId)
        );
        delivery1.setCourierId(courierId2);
        return delivery1;
    }

    public static List<Courier> seedCouriers() {
        List<Courier> couriers = new ArrayList<>();

        // Courier 1 - no active delivery
        Courier courier1 = new Courier(new CourierId(Courier1Id));
        // currentOrderId remains null (no active delivery)
        couriers.add(courier1);

        // Courier 2 - has active delivery
        Courier courier2 = new Courier(new CourierId(Courier2Id));
        courier2.setOrderId(new OrderId(Order4Id));
        couriers.add(courier2);

        return couriers;
    }
}