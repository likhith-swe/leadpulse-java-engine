package com.leadpulse.engine;

import com.leadpulse.model.CompanySignal;
import com.leadpulse.model.JobPosting;
import com.leadpulse.parser.KeywordTokenizer;
import com.leadpulse.repository.CompanyRepository;
import com.leadpulse.scorer.IntentScorer;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Consumer worker thread that pulls job postings from the BlockingQueue,
 * executes tokenization, updates company profiles, and recalculates intent scores.
 */
public class SignalConsumer implements Runnable {

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);
    private final int workerId;
    private final BlockingQueue<JobPosting> queue;
    private final KeywordTokenizer tokenizer;
    private final IntentScorer scorer;
    private final CompanyRepository repository;
    private final JobPosting poisonPill;

    public SignalConsumer(BlockingQueue<JobPosting> queue, KeywordTokenizer tokenizer, 
                          IntentScorer scorer, CompanyRepository repository, JobPosting poisonPill) {
        this.workerId = ID_GENERATOR.getAndIncrement();
        this.queue = queue;
        this.tokenizer = tokenizer;
        this.scorer = scorer;
        this.repository = repository;
        this.poisonPill = poisonPill;
    }

    @Override
    public void run() {
        try {
            while (true) {
                JobPosting job = queue.take();

                // Poison pill check
                if (job == poisonPill) {
                    // Put it back for other consumer worker threads before exiting
                    queue.put(poisonPill);
                    break;
                }

                // 1. Tokenize signals from title + description
                String fullText = job.getTitle() + " " + job.getDescription();
                Set<String> detectedSignals = tokenizer.extractSignals(fullText);

                // 2. Atomically retrieve or create company profile
                CompanySignal company = repository.getOrCreate(job.getCompanyName());
                company.recordJob(job, detectedSignals);

                // 3. Recalculate Intent Score
                scorer.scoreCompany(company);

                System.out.printf("  [\u001B[35mWORKER-%d\u001B[0m] Scored \u001B[1m%-14s\u001B[0m | Signals: %s | Score: %d (%s)%n",
                        workerId, company.getCompanyName(), detectedSignals, company.getIntentScore(), company.getIntentTier());
            }
            System.out.printf("  [\u001B[33mWORKER-%d-IDLE\u001B[0m] Consumer completed queue processing.%n", workerId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.printf("  [WORKER-%d-ERROR] Interrupted: %s%n", workerId, e.getMessage());
        }
    }
}
