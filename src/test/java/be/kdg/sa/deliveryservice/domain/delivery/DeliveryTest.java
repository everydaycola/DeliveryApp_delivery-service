package be.kdg.sa.deliveryservice.domain.delivery;

import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.order.OrderId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class DeliveryTest {

    static final double basePayout=3.0;
    static final double perMinutePayout=0.3;
    static final double minimumMinutes=5.0;
    static final double maximumMinutes=30.0;

    static OrderId orderId;
    static DeliveryId deliveryId;
    static CourierId courierId;
    static LocalDateTime startTime;
    static LocalDateTime endTime;
    Delivery delivery;

    @BeforeAll
    static void beforeAll() {
        orderId = new OrderId(UUID.randomUUID());
        deliveryId = new DeliveryId(UUID.randomUUID());
        courierId = new CourierId(UUID.randomUUID());
        startTime = LocalDateTime.now().minusMinutes(1);
        endTime = LocalDateTime.now().plusMinutes(30);
    }

    @BeforeEach
    void setUp() {
        this.delivery = new Delivery(deliveryId, orderId);
    }

    @Test
    void getIdShouldReturnDeliveryId() {
        // arrange


        // act
        final var result = delivery.getId();

        // assert
        assertThat(result).isEqualTo(deliveryId);
    }

    @Test
    void getOrderIdShouldReturnOrderIdOfDelivery() {
        // arrange


        // act
        final var result = delivery.getOrderId();

        // assert
        assertThat(result).isEqualTo(orderId);
    }

    @Test
    void getCourierIdShouldReturnCourierIdOfDelivery() {
        // arrange
        delivery.claim(courierId);

        // act
        final var result = delivery.getCourierId();

        // assert
        assertThat(result).isEqualTo(courierId);
    }

    @Test
    void getStatusShouldReturnDeliveryStatusOfDelivery() {
        // arrange


        // act
        final var result = delivery.getStatus();

        // assert
        assertThat(result).isEqualTo(DeliveryStatus.UNCLAIMED);
    }

    @Test
    void getStartTimeShouldReturnStartTimeOfDelivery() {
        // arrange
        delivery = new Delivery(deliveryId, orderId, courierId, DeliveryStatus.READY_FOR_PICKUP, startTime, null, 0);

        // act
        final var result = delivery.getStartTime();

        // assert
        assertThat(result).isEqualTo(startTime);
    }

    @Test
    void getEndTimeShouldReturnEndTimeOfDelivery() {
        // arrange
        delivery = new Delivery(
                deliveryId,
                orderId,
                courierId,
                DeliveryStatus.DELIVERED,
                startTime,
                endTime,
                4.5
        );

        // act
        final var result = delivery.getEndTime();


        // assert
        assertThat(result).isEqualTo(endTime);

    }

    @Test
    void claimShouldSucceed() {
        // arrange

        // act
        delivery.claim(courierId);

        // assert
        assertDelivery(true, DeliveryStatus.CLAIMED, false, false, false);
    }

    @Test
    void claimShouldThrowExceptionWhenClaimed() {
        // arrange
        delivery.claim(courierId);

        // act & assert
        assertThrows(IllegalStateException.class, () -> delivery.claim(courierId));

        assertDelivery(true, DeliveryStatus.CLAIMED, false, false, false);
    }

    @Test
    void unClaimShouldSucceed() {
        // arrange
        delivery.claim(courierId);

        // act
        delivery.unClaim();

        // assert
        assertDelivery(false, DeliveryStatus.UNCLAIMED, false, false, false);
    }

    @Test
    void unClaimShouldThrowExceptionWhenUnclaimed() {
        // arrange


        // act & assert
        assertThrows(IllegalStateException.class, () -> delivery.unClaim());

        assertDelivery(false, DeliveryStatus.UNCLAIMED, false, false, false);
    }

    @Test
    void setReadyNowShouldSucceed() {
        // arrange
        delivery.claim(courierId);

        // act
        delivery.setReadyNow();

        // assert
        assertDelivery(true, DeliveryStatus.READY_FOR_PICKUP, true, false, false);
    }

    @Test
    void unClaimShouldThrowExceptionWhenReady() {
        // arrange
        delivery.claim(courierId);
        delivery.setReadyNow();


        // act & assert
        assertThrows(IllegalStateException.class, () -> delivery.unClaim());

        assertDelivery(true, DeliveryStatus.READY_FOR_PICKUP, true, false, false);
    }

    @Test
    void setReadyNowShouldThrowExceptionWhenUnclaimed() {
        // arrange


        // act & assert
        assertThrows(IllegalStateException.class, () -> delivery.setReadyNow());

        assertDelivery(false, DeliveryStatus.UNCLAIMED, false, false, false);

    }

    @Test
    void setReadyNowShouldThrowExceptionWhenAlreadyReady() {
        // arrange
        delivery.claim(courierId);
        delivery.setReadyNow();

        // act & assert
        assertThrows(IllegalStateException.class, () -> delivery.setReadyNow());

        assertDelivery(true, DeliveryStatus.READY_FOR_PICKUP, true, false, false);

    }

    @Test
    void pickUpShouldSucceed() {
        // arrange
        delivery.claim(courierId);
        delivery.setReadyNow();


        // act
        delivery.pickUp();

        // assert
        assertDelivery(true, DeliveryStatus.IN_DELIVERY, true, false, false);
    }

    @Test
    void pickUpShouldThrowExceptionWhenNotReady() {
        // arrange
        delivery.claim(courierId);

        // act & assert
        assertThrows(IllegalStateException.class, () -> delivery.pickUp());

        assertDelivery(true, DeliveryStatus.CLAIMED, false, false, false);
    }

    @Test
    void pickUpShouldThrowExceptionWhenAlreadyPickedUp() {
        // arrange
        delivery.claim(courierId);
        delivery.setReadyNow();
        delivery.pickUp();

        // act & assert
        assertThrows(IllegalStateException.class, () -> delivery.pickUp());

        assertDelivery(true, DeliveryStatus.IN_DELIVERY, true, false, false);
    }

    @Test
    void finishNowShouldSucceed() {
        // arrange
        delivery.claim(courierId);
        delivery.setReadyNow();
        delivery.pickUp();

        // act
        delivery.finishNow(basePayout, perMinutePayout, minimumMinutes, maximumMinutes);

        // assert
        assertDelivery(true, DeliveryStatus.DELIVERED, true, true, true);
    }

    @Test
    void finishNowShouldThrowExceptionWhenNotInDelivery() {
        // arrange
        delivery.claim(courierId);
        delivery.setReadyNow();

        // act & assert
        assertThrows(IllegalStateException.class, () -> delivery.finishNow(basePayout, perMinutePayout, minimumMinutes, maximumMinutes));

        assertDelivery(true, DeliveryStatus.READY_FOR_PICKUP, true, false, false);
    }

    @Test
    void finishNowShouldThrowExceptionWhenAlreadyFinished() {
        // arrange
        delivery.claim(courierId);
        delivery.setReadyNow();
        delivery.pickUp();
        delivery.finishNow(basePayout, perMinutePayout, minimumMinutes, maximumMinutes);

        // act & assert
        assertThrows(IllegalStateException.class, () -> delivery.finishNow(basePayout, perMinutePayout, minimumMinutes, maximumMinutes));

        assertDelivery(true, DeliveryStatus.DELIVERED, true, true, true);
    }

    private void assertDelivery(boolean hasCourier, DeliveryStatus status, boolean hasStartTime, boolean hasEndTime, boolean hasPayout) {
        if (hasCourier) {
            assertThat(delivery.getCourierId()).isEqualTo(courierId);
        } else {
            assertThat(delivery.getCourierId()).isNull();
        }

        assertThat(delivery.getStatus()).isEqualTo(status);

        if (hasStartTime) {
            assertThat(delivery.getStartTime()).isNotNull();
        } else {
            assertThat(delivery.getStartTime()).isNull();
        }

        if (hasEndTime) {
            assertThat(delivery.getEndTime()).isNotNull();
        } else {
            assertThat(delivery.getEndTime()).isNull();
        }

        if (hasPayout) {
            assertThat(delivery.getPayout()).isGreaterThan(0);
        } else {
            assertThat(delivery.getPayout()).isZero();
        }
    }
}