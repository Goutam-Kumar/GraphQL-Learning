package com.droidnext.learning

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.stereotype.Component

@Component
class GraphQlExceptionResolver : DataFetcherExceptionResolverAdapter() {

    override fun resolveToSingleError(
        ex: Throwable,
        env: DataFetchingEnvironment
    ): GraphQLError {
        return GraphqlErrorBuilder.newError(env)
            .message(ex.message ?: "Internal server error")
            .build()
    }
}
