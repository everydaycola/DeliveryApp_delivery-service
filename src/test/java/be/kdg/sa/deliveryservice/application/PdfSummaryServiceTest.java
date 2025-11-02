package be.kdg.sa.deliveryservice.application;

import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.courier.CourierRepository;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryRepository;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryStatus;
import be.kdg.sa.deliveryservice.domain.order.OrderId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PdfSummaryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private CourierRepository courierRepository;

    @InjectMocks
    private PdfSummaryService sut;

    @Test
    public void generatePdfFromSummaryShouldSucceedAndReturnPdfBytes() {
        // arrange
        final var startDate = LocalDateTime.of(1900, 1, 1, 0, 0);
        final var endDate = LocalDateTime.of(2100, 1, 31, 23, 59);

        final var courierIdList = new ArrayList<CourierId>();
        courierIdList.add(new CourierId(UUID.randomUUID()));
        courierIdList.add(new CourierId(UUID.randomUUID()));
        final var courierList = new ArrayList<Courier>();
        courierList.add(new Courier(courierIdList.get(0), "Test Courier"));
        courierList.add(new Courier(courierIdList.get(1), "Other test Courier"));

        final var deliveryList = new ArrayList<Delivery>();
        for (int i = 0; i < 10; i++) {
            deliveryList.add(new Delivery(
                new DeliveryId(UUID.randomUUID()),
                new OrderId(UUID.randomUUID()),
                courierIdList.get(i % 2),
                DeliveryStatus.DELIVERED,
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 1, 1, 0, 20),
                10
                ));
        }

        when(deliveryRepository.findAllCompletedDeliveriesBetween(startDate, endDate)).thenReturn(deliveryList);
        when(courierRepository.findAllByIdIn(new HashSet<>(courierIdList))).thenReturn(courierList);

        // act
        final var pdfBytes = sut.generatePdfFromSummary(startDate, endDate);

        // assert
        // checking a pdf is hard but if there is a valid pdf, that means it most likely works
        assertThat(pdfBytes).isNotNull().isNotEmpty();

        assertThat(new String(pdfBytes, 0, 5)).isEqualTo("%PDF-");

        assertThat(pdfBytes.length).isGreaterThan(100);

    }

    @Test
    public void generatePdfFromSummaryShouldSucceedAndReturnPdfBytesWhenNoDeliveries() {
        // arrange
        final var startDate = LocalDateTime.of(1900, 1, 1, 0, 0);
        final var endDate = LocalDateTime.of(2100, 1, 31, 23, 59);


        given(deliveryRepository.findAllCompletedDeliveriesBetween(startDate, endDate)).willReturn(List.of());
        given(courierRepository.findAllByIdIn(Set.of())).willReturn(List.of());

        // act
        final var pdfBytes = sut.generatePdfFromSummary(startDate, endDate);

        // assert
        // checking a pdf is hard but if there is a valid pdf, that means it most likely works
        assertThat(pdfBytes).isNotNull().isNotEmpty();

        assertThat(new String(pdfBytes, 0, 5)).isEqualTo("%PDF-");

        assertThat(pdfBytes.length).isGreaterThan(100);

    }

}
