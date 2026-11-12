package io.github.jlmc.firststep.adapter.in.graphql.ex;

import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected List<GraphQLError> resolveToMultipleErrors(Throwable ex, DataFetchingEnvironment env) {
        return super.resolveToMultipleErrors(ex, env);
    }
}
