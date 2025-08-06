package dev.shaaf.waver.core;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * An asynchronous task pipeline that orchestrates the execution of a dependency graph of tasks.
 * It supports parallel execution of independent tasks, caching, and complex dependencies.
 */
public class TaskPipeline {
    private final Map<String, Task<?, ?>> tasks = new LinkedHashMap<>();
    private final Map<String, List<String>> graph = new HashMap<>();
    private final Map<CacheKey, Object> cache = new HashMap<>();
    private final ExecutorService executor;

    private String lastAddedTaskName;

    /**
     * Creates a new TaskPipeline with a default cached thread pool.
     */
    public TaskPipeline() {
        this.executor = Executors.newCachedThreadPool();
    }

    /**
     * Creates a new TaskPipeline with a custom thread pool for running tasks.
     * @param executor The executor service to run async tasks on.
     */
    public TaskPipeline(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Adds a task to the pipeline and makes it the end of a linear chain for the next `.then()` call.
     * @param task The task to add.
     * @return The pipeline instance for fluent chaining.
     */
    public TaskPipeline add(Task<?, ?> task) {
        String taskName = task.getName();
        System.out.println("ADDING: " + taskName);
        if (tasks.containsKey(taskName)) {
            throw new IllegalArgumentException("Task '" + taskName + "' has already been added.");
        }
        tasks.put(taskName, task);
        graph.put(taskName, new ArrayList<>());
        lastAddedTaskName = taskName;
        return this;
    }

    /**
     * Adds a task as a node to the graph without creating a connection.
     * Use this before creating connections with the `connect()` method.
     * @param task The task to add.
     * @return The pipeline instance for fluent chaining.
     */
    public TaskPipeline addTask(Task<?, ?> task) {
        String taskName = task.getName();
        if (tasks.containsKey(taskName)) {
            throw new IllegalArgumentException("Task '" + taskName + "' has already been added.");
        }
        tasks.put(taskName, task);
        graph.put(taskName, new ArrayList<>());
        return this;
    }

    /**
     * Creates a linear dependency between the previously added task and the next task.
     * @param nextTask The task to execute next.
     * @return The pipeline instance for fluent chaining.
     */
    public TaskPipeline then(Task<?, ?> nextTask) {
        if (lastAddedTaskName == null) {
            throw new IllegalStateException("You must call 'add()' before calling 'then()'.");
        }
        String fromTaskName = this.lastAddedTaskName;
        connect(tasks.get(fromTaskName), nextTask);
        this.lastAddedTaskName = nextTask.getName();
        return this;
    }

    /**
     * Creates an explicit dependency between any two tasks already added to the pipeline.
     * @param fromTask The parent task.
     * @param toTask The child task that depends on the parent.
     * @return The pipeline instance for fluent chaining.
     */
    public TaskPipeline connect(Task<?, ?> fromTask, Task<?, ?> toTask) {
        String fromTaskName = fromTask.getName();
        String toTaskName = toTask.getName();

        if (!tasks.containsKey(toTaskName)) {
            this.addTask(toTask);
        }

        if (!tasks.containsKey(fromTaskName)) {
            throw new IllegalStateException("The 'from' task '" + fromTaskName + "' must be added to the pipeline before connecting from it.");
        }

        graph.get(fromTaskName).add(toTaskName);
        return this;
    }

    /**
     * Runs the pipeline asynchronously, executing tasks in the correct dependency order.
     * @param initialInput The input for the first task(s) in the pipeline.
     * @return A CompletableFuture that will complete with the final result of the entire pipeline.
     */
    @SuppressWarnings("unchecked")
    public CompletableFuture<Object> run(Object initialInput) {
        PipelineContext context = new PipelineContext();
        List<String> executionOrder = topologicalSort();
        Map<String, CompletableFuture<Object>> results = new HashMap<>();

        for (String taskName : executionOrder) {
            Task<Object, Object> currentTask = (Task<Object, Object>) tasks.get(taskName);
            List<String> predecessors = findPredecessorsFor(taskName);

            CompletableFuture<?>[] parentFutures = predecessors.stream()
                    .map(results::get)
                    .toArray(CompletableFuture[]::new);

            CompletableFuture<Void> allParentsDone = CompletableFuture.allOf(parentFutures);

            CompletableFuture<Object> currentFuture = allParentsDone.thenComposeAsync(v -> {
                Object input = gatherInputsFromCompletedParents(predecessors, results, initialInput);

                if (currentTask.isCacheable()) {
                    CacheKey cacheKey = new CacheKey(taskName, input);
                    if (cache.containsKey(cacheKey)) {
                        System.out.println("HIT: Found '" + taskName + "' in cache.");
                        return CompletableFuture.completedFuture(cache.get(cacheKey));
                    }
                }

                System.out.println("EXEC: Executing '" + taskName + "'.");
                CompletableFuture<Object> taskResultFuture = currentTask.execute(input, context);

                if (currentTask.isCacheable()) {
                    return taskResultFuture.thenApply(result -> {
                        CacheKey cacheKey = new CacheKey(taskName, input);
                        cache.put(cacheKey, result);
                        return result;
                    });
                }
                return taskResultFuture;

            }, executor);

            results.put(taskName, currentFuture);
        }

        if (executionOrder.isEmpty()) {
            return CompletableFuture.completedFuture(initialInput);
        }

        String lastTaskName = executionOrder.get(executionOrder.size() - 1);
        return results.get(lastTaskName);
    }

    /**
     * Performs a topological sort on the task graph to determine the execution order.
     * @return A list of task names in a valid execution order.
     */
    private List<String> topologicalSort() {
        Map<String, Integer> inDegree = new HashMap<>();
        for (String taskName : tasks.keySet()) {
            inDegree.put(taskName, 0);
        }
        for (List<String> successors : graph.values()) {
            for (String successor : successors) {
                inDegree.put(successor, inDegree.getOrDefault(successor, 0) + 1);
            }
        }

        Queue<String> queue = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        List<String> sortedOrder = new ArrayList<>();
        while (!queue.isEmpty()) {
            String u = queue.poll();
            sortedOrder.add(u);
            for (String v : graph.getOrDefault(u, Collections.emptyList())) {
                inDegree.put(v, inDegree.get(v) - 1);
                if (inDegree.get(v) == 0) {
                    queue.add(v);
                }
            }
        }

        if (sortedOrder.size() != tasks.size()) {
            throw new RuntimeException("Cycle detected in the pipeline graph. The process cannot complete." + sortedOrder.toString());
        }
        return sortedOrder;
    }

    /**
     * Finds all direct parent tasks for a given task in the graph.
     */
    private List<String> findPredecessorsFor(String taskName) {
        List<String> predecessors = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            if (entry.getValue().contains(taskName)) {
                predecessors.add(entry.getKey());
            }
        }
        return predecessors;
    }

    /**
     * Gathers inputs from completed parent tasks.
     * If there is one parent, it returns the raw result from that parent.
     * If there are multiple parents, it returns a Map<String, Object> of parent results.
     */
    private Object gatherInputsFromCompletedParents(List<String> predecessors, Map<String, CompletableFuture<Object>> results, Object initialInput) {
        if (predecessors.isEmpty()) {
            return initialInput;
        }

        if (predecessors.size() == 1) {
            return results.get(predecessors.get(0)).join(); // .join() is safe as this runs after parent completion.
        }

        return predecessors.stream()
                .collect(Collectors.toMap(
                        name -> name,
                        name -> results.get(name).join()
                ));
    }
}