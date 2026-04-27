package org.envycorp.graphqlgateway.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        log.error("[GraphQL] Exception during data fetch for field '{}': {}",
                env.getField().getName(), ex.getMessage());

        return GraphqlErrorBuilder.newError(env)
                .message("An unexpected error occurred")
                .errorType(ErrorType.INTERNAL_ERROR)
                .build();
    }
}
