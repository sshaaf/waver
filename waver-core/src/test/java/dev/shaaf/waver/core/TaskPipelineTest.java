package dev.shaaf.waver.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.concurrent.*;

// Use the standard JUnit 5 assertions
import static org.junit.jupiter.api.Assertions.*;

class TaskPipelineTest {

    private ExecutorService executor;
    private TaskPipeline pipeline;

    // --- Test Setup ---

    @BeforeEach
    void setUp() {
        executor = Executors.newFixedThreadPool(4);
        pipeline = new TaskPipeline(executor);
    }

    @AfterEach
    void tearDown() {
        executor.shutdownNow();
    }

    // --- Stub Task Implementations for Testing ---

    static class StringToLengthTask implements Task<String, Integer> {
        @Override
        public CompletableFuture<Integer> execute(String input, PipelineContext context) {
            return CompletableFuture.supplyAsync(() -> input.length());
        }
    }

    static class LLMKindOfTask implements Task<Integer, Integer> {
        private final int durationMs = 600;

        @Override
        public CompletableFuture<Integer> execute(Integer input, PipelineContext context) {
            return CompletableFuture.supplyAsync(() -> {
                try { Thread.sleep(durationMs); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                return input * 2;
            });
        }
    }

    static class SlowTask implements Task<Integer, Integer> {
        private final int durationMs;
        public SlowTask(int durationMs) { this.durationMs = durationMs; }
        @Override
        public CompletableFuture<Integer> execute(Integer input, PipelineContext context) {
            return CompletableFuture.supplyAsync(() -> {
                try { Thread.sleep(durationMs); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                return input * 2;
            });
        }
    }
    
    static class MultiInputTask implements Task<Map<String, Object>, String> {
        @Override
        public CompletableFuture<String> execute(Map<String, Object> input, PipelineContext context) {
            return CompletableFuture.supplyAsync(() -> {
                // Combines results from two known parent tasks
                Integer length = (Integer) input.get("StringToLengthTask");
                Integer doubled = (Integer) input.get("SlowTask");
                return length + ":" + doubled;
            });
        }
    }

    static class FailingTask implements Task<Object, Object> {
        @Override
        public CompletableFuture<Object> execute(Object input, PipelineContext context) {
            return CompletableFuture.failedFuture(new TaskRunException("This task was designed to fail."));
        }
    }

    // --- The Actual Test Cases ---

    @Test
    void shouldExecuteSimpleLinearChain() {
        // Arrange
        var taskA = new StringToLengthTask();
        var taskB = new SlowTask(10);
        pipeline.add(taskA).then(taskB);

        // Act
        Object finalResult = pipeline.run("hello").join();

        // Assert
        assertNotNull(finalResult, "The final result should not be null.");
        // "hello" -> length 5 -> 5 * 2 = 10
        assertEquals(10, finalResult, "The final result of the chain is incorrect.");
    }

    @Test
    void shouldRunIndependentTasksInParallel() {
        // Arrange: A -> B and A -> C, where B and C are slow.
        var taskA = new StringToLengthTask();
        var taskB = new SlowTask(500);
        var taskC = new LLMKindOfTask();
        pipeline.addTask(taskA).addTask(taskB).addTask(taskC);
        pipeline.connect(taskA, taskB);
        pipeline.connect(taskA, taskC);

        // Act
        long startTime = System.currentTimeMillis();
        // We can't get a final result because B and C are leaf nodes, so we create a dummy joiner task
        var joiner = new Task<Map<String, Object>, Integer>() {
            public CompletableFuture<Integer> execute(Map<String, Object> i, PipelineContext c) { return CompletableFuture.completedFuture(1); }
        };
        pipeline.addTask(joiner);
        pipeline.connect(taskB, joiner);
        pipeline.connect(taskC, joiner);
        
        pipeline.run("A long string").join();
        long duration = System.currentTimeMillis() - startTime;
        
        System.out.println("Parallel execution took: " + duration + "ms");

        // Assert
        // If B and C ran sequentially, it would take >1000ms.
        // If they run in parallel, it should take ~500ms + overhead.
        assertTrue(duration < 800, "Execution should be parallel and take less than 800ms.");
    }

    @Test
    void shouldHandleFanInWithMultipleInputs() {
        // Arrange: A -> C and B -> C
        var taskA = new StringToLengthTask();
        var taskB = new SlowTask(10);
        var taskC = new MultiInputTask();

        pipeline.addTask(taskA).addTask(taskB).addTask(taskC);
        pipeline.connect(taskA, taskC);
        pipeline.connect(taskB, taskC);

        // Act
        // This is tricky because taskA and taskB have different input types.
        // We'll use a wrapper to provide a common input type for the pipeline's start.
        var initialTask = new Task<String, Map<String,Object>>() {
            public CompletableFuture<Map<String,Object>> execute(String i, PipelineContext c) {
                return CompletableFuture.completedFuture(Map.of("string", "hello", "integer", 50));
            }
        };
        // This test setup is more complex and shows the need for careful pipeline design.
        // A simpler approach for this test would be to have tasks A and B take the same input type.
        // For now, let's assume a simplified graph where inputs match.
        // We'll test the gatherInputsFromCompletedParents logic.
        
        // Let's create a simpler graph for a clean test:
        pipeline = new TaskPipeline(executor); // Reset pipeline
        var startA = new Task<String, String>() { public String getName() { return "StartA"; } public CompletableFuture<String> execute(String i, PipelineContext c) { return CompletableFuture.completedFuture("hello"); }};
        var startB = new Task<String, Integer>() { public String getName() { return "StartB"; } public CompletableFuture<Integer> execute(String i, PipelineContext c) { return CompletableFuture.completedFuture(100); }};
        var joinerTask = new Task<Map<String, Object>, String>() { public CompletableFuture<String> execute(Map<String, Object> i, PipelineContext c) { return CompletableFuture.completedFuture(i.get("StartA") + ":" + i.get("StartB")); }};

        pipeline.addTask(startA).addTask(startB).addTask(joinerTask);
        pipeline.connect(startA, joinerTask);
        pipeline.connect(startB, joinerTask);

        // Act
        Object finalResult = pipeline.run("dummy").join();
        
        // Assert
        assertEquals("hello:100", finalResult);
    }

    @Test
    void shouldCompleteExceptionallyWhenTaskFails() {
        // Arrange
        var taskA = new StringToLengthTask();
        var taskB = new FailingTask();
        var taskC = new SlowTask(10);
        pipeline.add(taskA).then(taskB).then(taskC);

        // Act
        CompletableFuture<Object> future = pipeline.run("test");

        // Assert
        // Use assertThrows to catch the expected top-level exception
        CompletionException thrown = assertThrows(
            CompletionException.class,
            future::join, // Method reference to the code that should throw
            "Expected pipeline.run().join() to throw CompletionException, but it didn't."
        );

        // Assert on the cause of the exception
        Throwable cause = thrown.getCause();
        assertNotNull(cause, "The CompletionException should have a cause.");
        assertTrue(cause instanceof TaskRunException, "The cause should be a TaskRunException.");
        assertEquals("This task was designed to fail.", cause.getMessage());
    }
}