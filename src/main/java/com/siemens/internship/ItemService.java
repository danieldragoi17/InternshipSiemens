package com.siemens.internship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    private static ExecutorService executor = Executors.newFixedThreadPool(10);

    // Thread-safe counter and list to track processed items
    private final AtomicInteger processedCount = new AtomicInteger(0);
    private List<Item> processedItems = new CopyOnWriteArrayList<>();


    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }


    /**
     * Your Tasks
     * Identify all concurrency and asynchronous programming issues in the code
     * Fix the implementation to ensure:
     * All items are properly processed before the CompletableFuture completes
     * Thread safety for all shared state
     * Proper error handling and propagation
     * Efficient use of system resources
     * Correct use of Spring's @Async annotation
     * Add appropriate comments explaining your changes and why they fix the issues
     * Write a brief explanation of what was wrong with the original implementation
     * <p>
     * Hints
     * Consider how CompletableFuture composition can help coordinate multiple async operations
     * Think about appropriate thread-safe collections
     * Examine how errors are handled and propagated
     * Consider the interaction between Spring's @Async and CompletableFuture
     * <p>
     * <p>
     * Refactored method to wait for all items to be processed
     */
    @Async
    public List<Item> processItemsAsync() {

        List<Long> itemIds = itemRepository.findAllIds();

        // List of CompletableFutures to wait for all tasks to complete
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Long id : itemIds) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(100);

                    Item item = itemRepository.findById(id).orElse(null);
                    if (item == null) {
                        return;
                    }

                    processedCount.incrementAndGet();

                    item.setStatus("PROCESSED");
                    itemRepository.save(item);
                    processedItems.add(item);

                } catch (InterruptedException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }, executor);

            futures.add(future);
        }

        // Combine all futures and wait for them to finish
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        // Call the async method and wait for the result
        // Once all tasks are complete, return the processed items
        CompletableFuture<List<Item>> futureItems = allOf.thenApply(v -> processedItems);
        try {
            return futureItems.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}

