package be.kdg.sa.deliveryservice.api;

import be.kdg.sa.deliveryservice.application.DeliveryService;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
