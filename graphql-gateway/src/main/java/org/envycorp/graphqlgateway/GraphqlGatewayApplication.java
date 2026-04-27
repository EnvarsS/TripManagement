package org.envycorp.graphqlgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GraphqlGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphqlGatewayApplication.class, args);
    }
}
