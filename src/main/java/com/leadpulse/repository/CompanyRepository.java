package com.leadpulse.repository;

import com.leadpulse.model.CompanySignal;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory repository storing aggregated company signals.
 */
public class CompanyRepository {

    private final Map<String, CompanySignal> companyStore = new ConcurrentHashMap<>();

    /**
     * Retrieves an existing company signal profile or creates a new one atomically.
     */
    public CompanySignal getOrCreate(String companyName) {
        return companyStore.computeIfAbsent(companyName, CompanySignal::new);
    }

    public Optional<CompanySignal> findByName(String companyName) {
        return Optional.ofNullable(companyStore.get(companyName));
    }

    public Collection<CompanySignal> getAll() {
        return companyStore.values();
    }

    /**
     * Returns top N companies ranked by intent score using a PriorityQueue (Max-Heap).
     */
    public List<CompanySignal> getTopRanked(int limit) {
        PriorityQueue<CompanySignal> maxHeap = new PriorityQueue<>(companyStore.values());
        List<CompanySignal> ranked = new ArrayList<>();

        while (!maxHeap.isEmpty() && ranked.size() < limit) {
            ranked.add(maxHeap.poll());
        }

        return ranked;
    }

    public int size() {
        return companyStore.size();
    }
}
