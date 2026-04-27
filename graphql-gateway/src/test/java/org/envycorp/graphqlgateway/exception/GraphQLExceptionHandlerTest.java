package org.envycorp.graphqlgateway.exception;

import graphql.GraphQLError;
import graphql.execution.ExecutionStepInfo;
import graphql.execution.ResultPath;
import graphql.language.Field;
import graphql.language.SourceLocation;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLOutputType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.graphql.execution.ErrorType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GraphQLExceptionHandlerTest {

    private GraphQLExceptionHandler handler;

    @Mock
    private DataFetchingEnvironment environment;

    @Mock
    private Field field;

    @Mock
    private ExecutionStepInfo executionStepInfo;

    @Mock
    private GraphQLFieldDefinition fieldDefinition;

    @Mock
    private GraphQLOutputType outputType;

    @BeforeEach
    void setUp() {
        handler = new GraphQLExceptionHandler();

        when(environment.getField()).thenReturn(field);
        when(field.getName()).thenReturn("testField");
        when(field.getSourceLocation()).thenReturn(new SourceLocation(1, 1));
        when(environment.getExecutionStepInfo()).thenReturn(executionStepInfo);
        when(executionStepInfo.getPath()).thenReturn(ResultPath.rootPath().segment("testField"));
        when(executionStepInfo.getFieldDefinition()).thenReturn(fieldDefinition);
        when(fieldDefinition.getType()).thenReturn(outputType);
    }

    @Test
    void resolveToSingleError_UnexpectedError() {
        RuntimeException exception = new RuntimeException("Unexpected");

        GraphQLError error = handler.resolveToSingleError(exception, environment);

        assertThat(error).isNotNull();
        assertThat(error.getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(error.getErrorType()).isEqualTo(ErrorType.INTERNAL_ERROR);
    }

    @Test
    void resolveToSingleError_NullPointerException() {
        NullPointerException exception = new NullPointerException("Null value");

        GraphQLError error = handler.resolveToSingleError(exception, environment);

        assertThat(error).isNotNull();
        assertThat(error.getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(error.getErrorType()).isEqualTo(ErrorType.INTERNAL_ERROR);
    }

    @Test
    void resolveToSingleError_IllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        GraphQLError error = handler.resolveToSingleError(exception, environment);

        assertThat(error).isNotNull();
        assertThat(error.getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(error.getErrorType()).isEqualTo(ErrorType.INTERNAL_ERROR);
    }
}
