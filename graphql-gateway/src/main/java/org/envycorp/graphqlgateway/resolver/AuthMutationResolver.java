package org.envycorp.graphqlgateway.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envycorp.commonmodule.dto.request.users.UserCreateRequestDto;
import org.envycorp.commonmodule.dto.request.users.UserLoginRequestDto;
import org.envycorp.graphqlgateway.client.UserClient;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthMutationResolver {

    private final UserClient userServiceClient;

    @MutationMapping
    public String login(@Argument String email, @Argument String password) {
        log.info("[GraphQL] Mutation login");
        return userServiceClient.login(new UserLoginRequestDto(email, password));
    }

    @MutationMapping
    public String register(@Argument String email,
                           @Argument String password,
                           @Argument String name) {
        log.info("[GraphQL] Mutation register");
        return userServiceClient.register(new UserCreateRequestDto(email, password, name));
    }
}
