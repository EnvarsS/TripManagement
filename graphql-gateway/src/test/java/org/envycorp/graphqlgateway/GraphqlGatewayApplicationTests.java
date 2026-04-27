package org.envycorp.graphqlgateway;

import org.envycorp.graphqlgateway.client.BookingClient;
import org.envycorp.graphqlgateway.client.TripClient;
import org.envycorp.graphqlgateway.client.UserClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class GraphqlGatewayApplicationTests {

    @MockBean
    private UserClient userClient;

    @MockBean
    private TripClient tripClient;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void contextLoads() {
    }
}
