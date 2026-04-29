package org.envycorp.graphqlgateway.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envycorp.commonmodule.dto.request.users.UserCreateRequestDto;
import org.envycorp.commonmodule.dto.request.users.UserUpdateRequestDto;
import org.envycorp.commonmodule.dto.response.users.UserResponseDto;
import org.envycorp.graphqlgateway.client.UserClient;
import org.envycorp.graphqlgateway.model.User;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserQueryResolver {
private final UserClient userServiceClient;

    @QueryMapping
    public UserResponseDto me() {
        log.info("[GraphQL] Query me");
        return userServiceClient.getCurrentUser();
    }

    @QueryMapping
    public UserResponseDto user(@Argument Long id) {
        log.info("[GraphQL] Query user(id={})", id);
        return userServiceClient.getUserById(id);
    }

    @MutationMapping
    public UserResponseDto createUser(@Argument String email,
                                      @Argument String password,
                                      @Argument String name) {
        log.info("[GraphQL] Mutation createUser");
        return userServiceClient.createUser(new UserCreateRequestDto(email, password, name));
    }

    @MutationMapping
    public UserResponseDto updateUser(@Argument Long id,
                                      @Argument String name,
                                      @Argument String email,
                                      @Argument String password) {
        log.info("[GraphQL] Mutation updateUser(id={})", id);
        return userServiceClient.updateUser(id, new UserUpdateRequestDto(name, email, password));
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        log.info("[GraphQL] Mutation deleteUser(id={})", id);
        userServiceClient.deleteUser(id);
        return true;
    }
}
