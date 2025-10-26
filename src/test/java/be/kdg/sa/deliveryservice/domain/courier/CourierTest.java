package be.kdg.sa.deliveryservice.domain.courier;

import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CourierTest {

    static CourierId courierId;
    static DeliveryId deliveryId;
    static DeliveryId secondDeliveryId;
    static final String name = "John";
    Courier courier;

    @BeforeAll
    static void beforeAll() {
        courierId = new CourierId(UUID.randomUUID());
        deliveryId = new DeliveryId(UUID.randomUUID());
        secondDeliveryId = new DeliveryId(UUID.randomUUID());
    }

    @BeforeEach
    void setUp() {
        this.courier = new Courier(courierId, name);
    }

    @Test
    void getCurrentDeliveryId() {
        // arrange
        courier.claim(deliveryId);

        // act
        var result = courier.getCurrentDeliveryId();

        // assert
        assertThat(result).isEqualTo(deliveryId);
    }

    @Test
    void getId() {
        // arrange

        // act
        var result = courier.getId();

        // assert
        assertThat(result).isEqualTo(courierId);

    }

    @Test
    void getPastDeliveries() {
        // arrange
        courier.addPastDelivery(deliveryId);
        courier.addPastDelivery(secondDeliveryId);
        List<DeliveryId> pastDeliveries = List.of(deliveryId, secondDeliveryId);

        // act
        var result = courier.getPastDeliveries();

        // assert
        assertThat(result).isEqualTo(pastDeliveries);
    }

    @Test
    void addPastDeliveryShouldSucceed() {
        // arrange

        // act
        courier.addPastDelivery(deliveryId);

        // assert
        assertThat(courier.getPastDeliveries()).isEqualTo(List.of(deliveryId));

    }

    @Test
    void addPastDeliveryShouldSucceedWithPastDeliveriesAlreadySet() {
        // arrange
        courier.addPastDelivery(deliveryId);

        // act
        courier.addPastDelivery(secondDeliveryId);

        // assert
        assertThat(courier.getPastDeliveries()).isEqualTo(List.of(deliveryId, secondDeliveryId));

    }

    @Test
    void claimShouldSucceedWhenNotClaimed() {
        // arrange


        // act
        courier.claim(deliveryId);

        // assert
        assertThat(courier.getCurrentDeliveryId()).isEqualTo(deliveryId);
    }

    @Test
    void claimShouldTrowExceptionWhenClaimed() {
        // arrange
        courier.claim(deliveryId);

        // act & assert
        assertThrows(IllegalStateException.class, () -> courier.claim(secondDeliveryId));

        assertThat(courier.getCurrentDeliveryId()).isEqualTo(deliveryId);
    }

    @Test
    void claimShouldThrowExceptionWhenAlreadyClaimed() {
        // arrange
        courier.claim(deliveryId);

        // act & assert
        assertThrows(IllegalStateException.class, () -> courier.claim(deliveryId));
    }

    @Test
    void unClaimShouldSucceedWhenClaimed() {
        // arrange
        courier.claim(deliveryId);

        // act
        courier.unClaim();

        // assert
        assertThat(courier.getCurrentDeliveryId()).isNull();

    }

    @Test
    void unClaimShouldThrowExceptionWhenNotClaimed() {
        // arrange

        // act
        assertThrows(IllegalStateException.class, () -> courier.unClaim());

        // assert
        assertThat(courier.getCurrentDeliveryId()).isNull();

    }

    @Test
    void getName() {
        // arrange

        // act
        var result = courier.getName();

        // assert
        assertThat(result).isEqualTo(name);

    }

    @Test
    void finishDeliveryShouldSucceedWhenClaimed() {
        // arrange
        courier.claim(deliveryId);

        // act
        courier.finishDelivery();

        // assert
        assertThat(courier.getCurrentDeliveryId()).isNull();
        assertThat(courier.getPastDeliveries()).isEqualTo(List.of(deliveryId));

    }

    @Test
    void finishDeliveryShouldThrowExceptionWhenNotClaimed() {
        // arrange

        // act
        assertThrows(IllegalStateException.class, () -> courier.finishDelivery());

        // assert
        assertThat(courier.getCurrentDeliveryId()).isNull();
        assertThat(courier.getPastDeliveries()).isEqualTo(List.of());

    }
}