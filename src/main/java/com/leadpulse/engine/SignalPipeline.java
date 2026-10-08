package com.leadpulse.engine;

import com.leadpulse.model.JobPosting;
import com.leadpulse.parser.KeywordTokenizer;
import com.leadpulse.repository.CompanyRepository;
import com.leadpulse.scorer.IntentScorer;

import java.util.List;
import java.util.concurrent.*;

/**
 * Coordinates the Producer-Consumer concurrency pipeline.
 */
public class SignalPipeline {

    private static final JobPosting POISON_PILL = new JobPosting(
            "__POISON__", "__TERMINATE__", "", "", "", "", 0);

    private final BlockingQueue<JobPosting> queue;
    private final CompanyRepository repository;
    private final KeywordTokenizer tokenizer;
    private final IntentScorer scorer;
    private final int consumerWorkerCount;

    public SignalPipeline(int queueCapacity, int consumerWorkerCount) {
        this.queue = new LinkedBlockingQueue<>(queueCapacity);
        this.repository = new CompanyRepository();
        this.tokenizer = new KeywordTokenizer();
        this.scorer = new IntentScorer(tokenizer);
        this.consumerWorkerCount = consumerWorkerCount;
    }

    public CompanyRepository processFeed(List<JobPosting> jobFeed) throws InterruptedException {
        ExecutorService consumerPool = Executors.newFixedThreadPool(consumerWorkerCount);
        ExecutorService producerPool = Executors.newSingleThreadExecutor();

        // 1. Launch Consumer Worker Threads
        for (int i = 0; i < consumerWorkerCount; i++) {
            consumerPool.submit(new SignalConsumer(queue, tokenizer, scorer, repository, POISON_PILL));
        }

        // 2. Launch Producer Thread
        producerPool.submit(new JobProducer("JobFeedIngestor-1", jobFeed, queue, POISON_PILL));

        // 3. Await Producer completion
        producerPool.shutdown();
        producerPool.awaitTermination(30, TimeUnit.SECONDS);

        // 4. Await Consumers processing remaining queue items
        consumerPool.shutdown();
        consumerPool.awaitTermination(30, TimeUnit.SECONDS);

        return repository;
    }

    public CompanyRepository getRepository() {
        return repository;
    }
}
