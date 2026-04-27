package org.envycorp.graphqlgateway.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envycorp.graphqlgateway.client.UserClient;
import org.envycorp.graphqlgateway.model.User;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserQueryResolver {

    private final UserClient userClient;

    @QueryMapping
    public User user(@Argument Long id) {
        log.info("[GraphQL] Query user(id={})", id);
        return userClient.getUserById(id);
    }
}
