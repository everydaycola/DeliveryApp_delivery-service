package be.kdg.sa.deliveryservice.api.courier;

import be.kdg.sa.deliveryservice.api.courier.dtos.CourierDto;
import be.kdg.sa.deliveryservice.api.courier.dtos.CompletedDeliveriesDto;
import be.kdg.sa.deliveryservice.application.DeliveryService;
import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/couriers")
@PreAuthorize("hasAuthority('courier')")
@Slf4j
public class CourierController {
    private static final String TOKEN_ID_NAME = "databaseid";

    private final DeliveryService deliveries;


    public CourierController(DeliveryService deliveries) {
        this.deliveries = deliveries;
    }

    @GetMapping
    public ResponseEntity <CourierDto> findById(@AuthenticationPrincipal Jwt token) {
        log.info("findById: {}", token.getClaimAsString(TOKEN_ID_NAME));
        CourierId courierId = new CourierId(UUID.fromString(token.getClaimAsString(TOKEN_ID_NAME)));
        return ResponseEntity.ok(CourierDto.from(deliveries.findCourierById(courierId)));
    }

    @GetMapping("/completed")
    public ResponseEntity<CompletedDeliveriesDto> getCompletedDeliveries(@AuthenticationPrincipal Jwt token) {
        log.info("getCompletedDeliveries: {}", token.getClaimAsString(TOKEN_ID_NAME));
        CourierId courierId = new CourierId(UUID.fromString(token.getClaimAsString(TOKEN_ID_NAME)));
        return ResponseEntity.ok(CompletedDeliveriesDto.from(deliveries.findCompletedDeliveries(courierId)));
    }

    @PostMapping("/unclaim")
    public ResponseEntity<CourierDto> confirm(@AuthenticationPrincipal Jwt token) {
        log.info("confirm: {}", token.getClaimAsString(TOKEN_ID_NAME));
        final CourierId courierId = new CourierId(UUID.fromString(token.getClaimAsString(TOKEN_ID_NAME)));
        final Courier courier = deliveries.unClaim(courierId);
        final CourierDto dto = CourierDto.from(courier);
        return ResponseEntity.ok(dto);
    }
}
