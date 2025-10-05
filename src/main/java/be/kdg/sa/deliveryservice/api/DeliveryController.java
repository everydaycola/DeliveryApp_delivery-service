package be.kdg.sa.deliveryservice.api;

import be.kdg.sa.deliveryservice.application.DeliveryService;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {
    private final DeliveryService deliveries;

    public DeliveryController(DeliveryService deliveries) {
        this.deliveries = deliveries;
    }

    @GetMapping
    public ResponseEntity <List <DeliveryDto>> findAll() {
        List <Delivery> foundDeliveries = this.deliveries.findAll();

        List <DeliveryDto> deliveryDtos = foundDeliveries.stream()
                                         .map(DeliveryDto::from)
                                         .toList();

        return ResponseEntity.ok(deliveryDtos);
    }

    @PostMapping("/{deliveryId}/claim/{courierId}")
    public ResponseEntity<DeliveryDto> confirm(@PathVariable("deliveryId") final UUID deliveryUUID,
                                               @PathVariable("courierId") final UUID courierUUID) {
        final DeliveryId deliveryId = new DeliveryId(deliveryUUID);
        final CourierId courierId = new CourierId(courierUUID);
        final Delivery delivery = deliveries.confirm(deliveryId, courierId);
        final DeliveryDto dto = DeliveryDto.from(delivery);
        return ResponseEntity.ok(dto);
    }




}
