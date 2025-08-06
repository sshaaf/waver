package dev.shaaf.waver.core;

import java.util.concurrent.CompletableFuture;

/**
 * An asynchronous task that can be part of a pipeline.
 *
 * @param <I> The input type.
 * @param <O> The output type.
 */
public interface Task<I, O> {

    /**
     * Executes the main logic of the task asynchronously.
     * The method returns immediately with a promise of a future result.
     * Exceptions should be handled by completing the future exceptionally.
     */
    CompletableFuture<O> execute(I input, PipelineContext context);

    default String getName() {
        return this.getClass().getSimpleName();
    }

    default boolean isCacheable() {
        return false;
    }
}