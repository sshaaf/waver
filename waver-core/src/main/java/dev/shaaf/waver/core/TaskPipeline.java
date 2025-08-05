package dev.shaaf.waver.core;

// Pipeline.java
import java.util.*;

public class TaskPipeline {
    private final Map<String, Task<?, ?>> tasks = new LinkedHashMap<>();
    private final Map<String, List<String>> graph = new HashMap<>();
    private final Map<CacheKey, Object> cache = new HashMap<>();

    private String lastAddedTaskName;

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

    public TaskPipeline then(Task<?, ?> nextTask) {
        if (lastAddedTaskName == null) {
            throw new IllegalStateException("You must call 'add()' before calling 'then()'.");
        }

        // Store the 'from' task before it can be changed by the add() method
        String fromTaskName = this.lastAddedTaskName;
        String nextTaskName = nextTask.getName();

        if (!tasks.containsKey(nextTaskName)) {
            this.add(nextTask);
        }

        // Use the stored 'from' variable to create the correct connection
        graph.get(fromTaskName).add(nextTaskName);

        // Now, update the last added task for the next link in the chain
        this.lastAddedTaskName = nextTaskName;

        return this;
    }

    private List<String> topologicalSort() {
        Map<String, Integer> inDegree = new HashMap<>();
        for (String taskName : tasks.keySet()) {
            inDegree.put(taskName, 0);
        }
        for (List<String> successors : graph.values()) {
            for (String successor : successors) {
                inDegree.put(successor, inDegree.get(successor) + 1);
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
            throw new RuntimeException("Cycle detected in the pipeline graph. The process cannot complete."+sortedOrder.toString());
        }
        return sortedOrder;
    }

    @SuppressWarnings("unchecked")
    public <T> T run(Object initialInput) {
        PipelineContext context = new PipelineContext();
        List<String> executionOrder = topologicalSort();
        Map<String, Object> results = new HashMap<>();

        for (String taskName : executionOrder) {
            Task<Object, ?> currentTask = (Task<Object, ?>) tasks.get(taskName);
            Object input = findInputFor(taskName, initialInput, results);

            Object output; // To hold the result from cache or execution

            // Caching logic starts here
            if (currentTask.isCacheable()) {
                CacheKey cacheKey = new CacheKey(taskName, input);
                if (cache.containsKey(cacheKey)) {
                    System.out.println("HIT: Found '" + taskName + "' in cache.");
                    output = cache.get(cacheKey);
                } else {
                    System.out.println("MISS: Executing '" + taskName + "'.");
                    output = currentTask.execute(input, context);
                    cache.put(cacheKey, output); // Store new result in cache
                }
            } else {
                // Not a cacheable task, execute normally
                output = currentTask.execute(input, context);
            }
            results.put(taskName, output);
        }

        return (T) results.get(executionOrder.get(executionOrder.size() - 1));
    }

    private Object findInputFor(String taskName, Object initialInput, Map<String, Object> results) {
        // Find if this task is a successor to any other task
        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            if (entry.getValue().contains(taskName)) {
                String predecessor = entry.getKey();
                return results.get(predecessor);
            }
        }
        // If no predecessor, it must be a starting task
        return initialInput;
    }
}
