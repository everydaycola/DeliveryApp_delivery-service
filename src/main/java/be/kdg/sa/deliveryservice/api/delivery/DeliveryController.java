package be.kdg.sa.deliveryservice.api.delivery;

import be.kdg.sa.deliveryservice.api.OrderPickedUpAndDeliveredDto;
import be.kdg.sa.deliveryservice.api.delivery.dtos.DeliveryDto;
import be.kdg.sa.deliveryservice.application.DeliveryService;
import be.kdg.sa.deliveryservice.config.RabbitMQProperties;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.messages.OrderDeliveredMessage;
import be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.messages.OrderPickedUpMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/deliveries")
@PreAuthorize("hasAuthority('courier')")
public class DeliveryController {
    private static final String TOKEN_ID_NAME = "databaseid";
    
    private final DeliveryService deliveries;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties properties;

    public DeliveryController(DeliveryService deliveries, RabbitTemplate rabbitTemplate, RabbitMQProperties properties) {
        this.deliveries = deliveries;
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
    }

    @GetMapping("/unclaimed")
    public ResponseEntity <List <DeliveryDto>> findAllUnclaimed() {
        log.info("findAllUnclaimed");
        List <Delivery> foundDeliveries = this.deliveries.findAllUnclaimed();

        List <DeliveryDto> deliveryDtos = foundDeliveries.stream()
                                         .map(DeliveryDto::from)
                                         .toList();

        return ResponseEntity.ok(deliveryDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity <DeliveryDto> findDelivery(@PathVariable final UUID id,
                                                     @AuthenticationPrincipal Jwt token) {
        log.info("findDelivery: {}", id);
        final CourierId courierId = new CourierId(UUID.fromString(token.getClaimAsString(TOKEN_ID_NAME)));
        DeliveryId deliveryId = new DeliveryId(id);
        return ResponseEntity.ok(DeliveryDto.from(deliveries.findDelivery(deliveryId, courierId)));
    }

    @PostMapping("/{deliveryId}/claim")
    public ResponseEntity<DeliveryDto> claim(@PathVariable("deliveryId") final UUID deliveryUUID,
                                             @AuthenticationPrincipal Jwt token) {
        log.info("claim: {}", deliveryUUID);
        final DeliveryId deliveryId = new DeliveryId(deliveryUUID);
        final CourierId courierId = new CourierId(UUID.fromString(token.getClaimAsString(TOKEN_ID_NAME)));
        final Delivery delivery = deliveries.claim(deliveryId, courierId);
        final DeliveryDto dto = DeliveryDto.from(delivery);
        return ResponseEntity.ok(dto);
    }

    // this method should not exist and needs to be moved to message controller to be spoken to by restaurant
    @PostMapping("/{id}/ready")
    public ResponseEntity<DeliveryDto> setReady(@PathVariable final UUID id, @AuthenticationPrincipal Jwt token) {
        log.info("setReady: {}", id);
        log.warn("This method should not exist and needs to be moved to message controller to be spoken to by restaurant");
        final CourierId courierId = new CourierId(UUID.fromString(token.getClaimAsString(TOKEN_ID_NAME)));
        final DeliveryId deliveryId = new DeliveryId(id);
        final Delivery delivery = deliveries.ready(deliveryId, courierId);
        final DeliveryDto dto = DeliveryDto.from(delivery);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/pickup")
    public ResponseEntity<DeliveryDto> setInDelivery(@PathVariable final UUID id, @AuthenticationPrincipal Jwt token) {
        log.info("setInDelivery: {}", id);
        final CourierId courierId = new CourierId(UUID.fromString(token.getClaimAsString(TOKEN_ID_NAME)));
        final DeliveryId deliveryId = new DeliveryId(id);
        final Delivery delivery = deliveries.pickup(deliveryId, courierId);
        final DeliveryDto dto = DeliveryDto.from(delivery);

        rabbitTemplate.convertAndSend(properties.getExchangeName(),
                                      properties.getOrderPickedUpBinding(),
                                      new OrderPickedUpMessage(OrderPickedUpAndDeliveredDto.from(delivery)));

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<DeliveryDto> setDelivered(@PathVariable final UUID id, @AuthenticationPrincipal Jwt token) {
        log.info("setDelivered: {}", id);
        final CourierId courierId = new CourierId(UUID.fromString(token.getClaimAsString(TOKEN_ID_NAME)));
        final DeliveryId deliveryId = new DeliveryId(id);
        final Delivery delivery = deliveries.deliver(deliveryId, courierId);
        final DeliveryDto dto = DeliveryDto.from(delivery);

        rabbitTemplate.convertAndSend(properties.getExchangeName(),
                                      properties.getOrderDeliveredBinding(),
                                      new OrderDeliveredMessage(OrderPickedUpAndDeliveredDto.from(delivery)));

        return ResponseEntity.ok(dto);
    }
}
