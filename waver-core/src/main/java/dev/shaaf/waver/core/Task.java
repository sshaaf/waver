package dev.shaaf.waver.core;

public interface Task<I, O> {
    /**
     * Executes the main logic of the task.
     * @param input The input data for the task.
     * @return The output data from the task.
     */
    O execute(I input, PipelineContext context) throws TaskRunException;

    /**
     * A default implementation to get a unique name for the task.
     * @return The simple class name of the task implementation.
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Override and return true if the task's output should be cached.
     * Caching is based on the task's class and the input's hashcode.
     * @return false by default.
     */
    default boolean isCacheable() {
        return false;
    }
}
