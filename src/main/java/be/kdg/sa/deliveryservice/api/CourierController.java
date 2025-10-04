package be.kdg.sa.deliveryservice.api;

import be.kdg.sa.deliveryservice.application.DeliveryService;
import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.infrastructure.DataSeeder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
    public ResponseEntity<List<DeliveryDto>> getCompletedDeliveries(@PathVariable final UUID id) {
        return ResponseEntity.ok(deliveries.findCompletedDeliveries(new CourierId(id)).stream().map(DeliveryDto::from).toList());
    }

}
