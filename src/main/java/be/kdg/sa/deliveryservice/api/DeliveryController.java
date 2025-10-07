package be.kdg.sa.deliveryservice.api;

import be.kdg.sa.deliveryservice.application.DeliveryService;
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

    @GetMapping("/unclaimed")
    public ResponseEntity <List <DeliveryDto>> findAllUnclaimed() {
        List <Delivery> foundDeliveries = this.deliveries.findAllUnclaimed();

        List <DeliveryDto> deliveryDtos = foundDeliveries.stream()
                                         .map(DeliveryDto::from)
                                         .toList();

        return ResponseEntity.ok(deliveryDtos);
    }

    @PostMapping("/{id}/ready")
    public ResponseEntity<DeliveryDto> setReady(@PathVariable final UUID id) {
        final DeliveryId deliveryId = new DeliveryId(id);
        final Delivery delivery = deliveries.ready(deliveryId);
        final DeliveryDto dto = DeliveryDto.from(delivery);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/pickup")
    public ResponseEntity<DeliveryDto> setInDelivery(@PathVariable final UUID id) {
        final DeliveryId deliveryId = new DeliveryId(id);
        final Delivery delivery = deliveries.pickup(deliveryId);
        final DeliveryDto dto = DeliveryDto.from(delivery);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<DeliveryDto> setDelivered(@PathVariable final UUID id) {
        final DeliveryId deliveryId = new DeliveryId(id);
        final Delivery delivery = deliveries.deliver(deliveryId);
        final DeliveryDto dto = DeliveryDto.from(delivery);
        return ResponseEntity.ok(dto);
    }
}
