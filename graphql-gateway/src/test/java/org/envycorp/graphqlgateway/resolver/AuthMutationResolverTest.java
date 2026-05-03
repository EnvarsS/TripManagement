package org.envycorp.graphqlgateway.resolver;

import org.envycorp.commonmodule.dto.request.users.UserCreateRequestDto;
import org.envycorp.commonmodule.dto.request.users.UserLoginRequestDto;
import org.envycorp.graphqlgateway.client.UserClient;
import org.envycorp.graphqlgateway.exception.GraphQLExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@GraphQlTest(AuthMutationResolver.class)
@Import(GraphQLExceptionHandler.class)
class AuthMutationResolverTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    private UserClient userServiceClient;

    // ---- login ----

    @Test
    void login_ValidCredentials_ReturnsToken() {
        when(userServiceClient.login(any(UserLoginRequestDto.class)))
                .thenReturn("jwt-token");

        graphQlTester.document("""
                mutation {
                    login(email: "alice@example.com", password: "password123")
                }
                """)
                .execute()
                .path("login")
                .entity(String.class)
                .isEqualTo("jwt-token");
    }

    @Test
    void login_PassesCorrectEmailAndPassword_ToClient() {
        when(userServiceClient.login(any())).thenReturn("jwt-token");

        graphQlTester.document("""
                mutation {
                    login(email: "alice@example.com", password: "password123")
                }
                """)
                .execute();

        verify(userServiceClient).login(new UserLoginRequestDto("alice@example.com", "password123"));
    }

    @Test
    void login_ClientThrowsException_ReturnsGraphQLError() {
        when(userServiceClient.login(any()))
                .thenThrow(new RuntimeException("Invalid credentials"));

        graphQlTester.document("""
                mutation {
                    login(email: "wrong@example.com", password: "wrongpass")
                }
                """)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                    assert errors.get(0).getMessage().equals("An unexpected error occurred");
                });
    }

    // ---- register ----

    @Test
    void register_ValidData_ReturnsToken() {
        when(userServiceClient.register(any(UserCreateRequestDto.class)))
                .thenReturn("new-jwt-token");

        graphQlTester.document("""
                mutation {
                    register(email: "alice@example.com", password: "password123", name: "Alice")
                }
                """)
                .execute()
                .path("register")
                .entity(String.class)
                .isEqualTo("new-jwt-token");
    }

    @Test
    void register_PassesCorrectArguments_ToClient() {
        when(userServiceClient.register(any())).thenReturn("new-jwt-token");

        graphQlTester.document("""
                mutation {
                    register(email: "alice@example.com", password: "password123", name: "Alice")
                }
                """)
                .execute();

        verify(userServiceClient).register(
                new UserCreateRequestDto("alice@example.com", "password123", "Alice"));
    }

    @Test
    void register_ClientThrowsException_ReturnsGraphQLError() {
        when(userServiceClient.register(any()))
                .thenThrow(new RuntimeException("Email already taken"));

        graphQlTester.document("""
                mutation {
                    register(email: "taken@example.com", password: "password123", name: "Alice")
                }
                """)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                    assert errors.get(0).getMessage().equals("An unexpected error occurred");
                });
    }
}
