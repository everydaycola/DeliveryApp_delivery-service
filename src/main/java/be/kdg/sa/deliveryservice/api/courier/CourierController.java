package be.kdg.sa.deliveryservice.api.courier;

import be.kdg.sa.deliveryservice.api.courier.dtos.CourierDto;
import be.kdg.sa.deliveryservice.api.courier.dtos.CompletedDeliveriesDto;
import be.kdg.sa.deliveryservice.api.courier.dtos.FullSummaryDto;
import be.kdg.sa.deliveryservice.api.courier.dtos.TimeSpan;
import be.kdg.sa.deliveryservice.application.DeliveryService;
import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/couriers")

@Slf4j
public class CourierController {
    private static final String TOKEN_ID_NAME = "databaseid";

    private final DeliveryService deliveries;

    public CourierController(DeliveryService deliveries) {
        this.deliveries = deliveries;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('courier')")
    public ResponseEntity <CourierDto> findById(@AuthenticationPrincipal Jwt token) {
        log.info("findById: {}", token.getClaimAsString(TOKEN_ID_NAME));
        CourierId courierId = new CourierId(UUID.fromString(token.getClaimAsString(TOKEN_ID_NAME)));
        return ResponseEntity.ok(CourierDto.from(deliveries.findCourierById(courierId)));
    }

    @GetMapping("/completed")
    @PreAuthorize("hasAuthority('courier')")
    public ResponseEntity<CompletedDeliveriesDto> getCompletedDeliveries(@AuthenticationPrincipal Jwt token) {
        log.info("getCompletedDeliveries: {}", token.getClaimAsString(TOKEN_ID_NAME));
        CourierId courierId = new CourierId(UUID.fromString(token.getClaimAsString(TOKEN_ID_NAME)));
        return ResponseEntity.ok(CompletedDeliveriesDto.from(deliveries.findCompletedDeliveries(courierId)));
    }

    @PostMapping("/unclaim")
    @PreAuthorize("hasAuthority('courier')")
    public ResponseEntity<CourierDto> confirm(@AuthenticationPrincipal Jwt token) {
        log.info("confirm: {}", token.getClaimAsString(TOKEN_ID_NAME));
        final CourierId courierId = new CourierId(UUID.fromString(token.getClaimAsString(TOKEN_ID_NAME)));
        final Courier courier = deliveries.unClaim(courierId);
        final CourierDto dto = CourierDto.from(courier);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<FullSummaryDto> getSummary(@AuthenticationPrincipal Jwt token,
                                                     @RequestBody TimeSpan timeSpan) {
        log.info("getSummary: {}", token.getClaimAsString(TOKEN_ID_NAME));

        Map<Courier, List<Delivery>> couriersWithDeliveries = deliveries.findAllCouriersWithCompletedDeliveries(
                timeSpan.start(),
                timeSpan.end()
        );
        final FullSummaryDto fullSummaryDto = FullSummaryDto.from(couriersWithDeliveries);
        return ResponseEntity.ok(fullSummaryDto);
    }
}
