package be.kdg.sa.deliveryservice.api;

import be.kdg.sa.deliveryservice.application.DeliveryService;
import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/couriers")
@PreAuthorize("hasAuthority('courier')")
public class CourierController {
    private final DeliveryService deliveries;

    public CourierController(DeliveryService deliveries) {
        this.deliveries = deliveries;
    }

    @GetMapping
    public ResponseEntity <CourierDto> findById(@AuthenticationPrincipal Jwt token) {
        CourierId courierId = new CourierId(UUID.fromString(token.getSubject()));
        return ResponseEntity.ok(CourierDto.from(deliveries.findCourierById(courierId)));
    }

    @GetMapping("/completed")
    public ResponseEntity<CompletedDeliveriesDto> getCompletedDeliveries(@AuthenticationPrincipal Jwt token) {
        CourierId courierId = new CourierId(UUID.fromString(token.getSubject()));
        return ResponseEntity.ok(CompletedDeliveriesDto.from(deliveries.findCompletedDeliveries(courierId)));
    }

    @PostMapping("/unclaim")
    public ResponseEntity<CourierDto> confirm(@AuthenticationPrincipal Jwt token) {
        final CourierId courierId = new CourierId(UUID.fromString(token.getSubject()));
        final Courier courier = deliveries.unClaim(courierId);
        final CourierDto dto = CourierDto.from(courier);
        return ResponseEntity.ok(dto);
    }
}
