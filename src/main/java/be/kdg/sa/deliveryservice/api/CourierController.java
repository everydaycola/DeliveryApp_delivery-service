package be.kdg.sa.deliveryservice.api;

import be.kdg.sa.deliveryservice.application.DeliveryService;
import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/couriers")
public class CourierController {
    private final DeliveryService deliveries;

    public CourierController(DeliveryService deliveries) {
        this.deliveries = deliveries;
    }

    @GetMapping("/{id}")
    public ResponseEntity <CourierDto> findById(@PathVariable final UUID id) {
        return ResponseEntity.ok(CourierDto.from(deliveries.findCourierById(new CourierId(id))));
    }

    @GetMapping("/{id}/completed")
    public ResponseEntity<CompletedDeliveriesDto> getCompletedDeliveries(@PathVariable final UUID id) {
        return ResponseEntity.ok(CompletedDeliveriesDto.from(deliveries.findCompletedDeliveries(new CourierId(id))));
    }

    @PostMapping("/{courierId}/claim/{deliveryId}")
    public ResponseEntity<DeliveryDto> claim(@PathVariable("courierId") final UUID courierUUID,
                                               @PathVariable("deliveryId") final UUID deliveryUUID) {
        final DeliveryId deliveryId = new DeliveryId(deliveryUUID);
        final CourierId courierId = new CourierId(courierUUID);
        final Delivery delivery = deliveries.claim(deliveryId, courierId);
        final DeliveryDto dto = DeliveryDto.from(delivery);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/unclaim")
    public ResponseEntity<CourierDto> confirm(@PathVariable final UUID id) {
        final CourierId courierId = new CourierId(id);
        final Courier courier = deliveries.unClaim(courierId);
        final CourierDto dto = CourierDto.from(courier);
        return ResponseEntity.ok(dto);
    }
}
