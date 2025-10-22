package be.kdg.sa.deliveryservice.api;

import be.kdg.sa.deliveryservice.application.DeliveryService;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries")
@PreAuthorize("hasAuthority('courier')")
public class DeliveryController {
    private final DeliveryService deliveries;

    public DeliveryController(DeliveryService deliveries) {
        this.deliveries = deliveries;
    }

    @GetMapping("/unclaimed")
    public ResponseEntity <List <DeliveryDto>> findAllUnclaimed() {
        List <Delivery> foundDeliveries = this.deliveries.findAllUnclaimed();

        List <DeliveryDto> deliveryDtos = foundDeliveries.stream()
                                         .map(DeliveryDto::from)
                                         .toList();

        return ResponseEntity.ok(deliveryDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity <DeliveryDto> findDelivery(@PathVariable final UUID id,
                                                     @AuthenticationPrincipal Jwt token) {
        final CourierId courierId = new CourierId(UUID.fromString(token.getClaimAsString("databaseid")));
        DeliveryId deliveryId = new DeliveryId(id);
        return ResponseEntity.ok(DeliveryDto.from(deliveries.findDelivery(deliveryId, courierId)));
    }

    @PostMapping("/{deliveryId}/claim")
    public ResponseEntity<DeliveryDto> claim(@PathVariable("deliveryId") final UUID deliveryUUID,
                                             @AuthenticationPrincipal Jwt token) {
        final DeliveryId deliveryId = new DeliveryId(deliveryUUID);
        final CourierId courierId = new CourierId(UUID.fromString(token.getClaimAsString("databaseid")));
        final Delivery delivery = deliveries.claim(deliveryId, courierId);
        final DeliveryDto dto = DeliveryDto.from(delivery);
        return ResponseEntity.ok(dto);
    }

    // this method should not exist and needs to be moved to message controller to be spoken to by restaurant
    @PostMapping("/{id}/ready")
    public ResponseEntity<DeliveryDto> setReady(@PathVariable final UUID id, @AuthenticationPrincipal Jwt token) {
        final CourierId courierId = new CourierId(UUID.fromString(token.getClaimAsString("databaseid")));
        final DeliveryId deliveryId = new DeliveryId(id);
        final Delivery delivery = deliveries.ready(deliveryId, courierId);
        final DeliveryDto dto = DeliveryDto.from(delivery);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/pickup")
    public ResponseEntity<DeliveryDto> setInDelivery(@PathVariable final UUID id, @AuthenticationPrincipal Jwt token) {
        final CourierId courierId = new CourierId(UUID.fromString(token.getClaimAsString("databaseid")));
        final DeliveryId deliveryId = new DeliveryId(id);
        final Delivery delivery = deliveries.pickup(deliveryId, courierId);
        final DeliveryDto dto = DeliveryDto.from(delivery);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<DeliveryDto> setDelivered(@PathVariable final UUID id, @AuthenticationPrincipal Jwt token) {
        final CourierId courierId = new CourierId(UUID.fromString(token.getClaimAsString("databaseid")));
        final DeliveryId deliveryId = new DeliveryId(id);
        final Delivery delivery = deliveries.deliver(deliveryId, courierId);
        final DeliveryDto dto = DeliveryDto.from(delivery);
        return ResponseEntity.ok(dto);
    }
}
