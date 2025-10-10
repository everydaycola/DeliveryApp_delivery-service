package be.kdg.sa.deliveryservice.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class DeliveryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductCatalog productCatalog;

    @Test
    void findAll() {
    }

    @Test
    void findAllUnclaimed() {
    }

    @Test
    void findDelivery() {
    }

    @Test
    void setReady() {
    }

    @Test
    void setInDelivery() {
    }

    @Test
    void setDelivered() {
    }
}