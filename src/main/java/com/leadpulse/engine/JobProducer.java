package com.leadpulse.engine;

import com.leadpulse.model.JobPosting;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Producer thread that ingests job postings and feeds them into the shared BlockingQueue.
 */
public class JobProducer implements Runnable {

    private final String producerName;
    private final List<JobPosting> jobFeed;
    private final BlockingQueue<JobPosting> queue;
    private final JobPosting poisonPill;

    public JobProducer(String producerName, List<JobPosting> jobFeed, 
                       BlockingQueue<JobPosting> queue, JobPosting poisonPill) {
        this.producerName = producerName;
        this.jobFeed = jobFeed;
        this.queue = queue;
        this.poisonPill = poisonPill;
    }

    @Override
    public void run() {
        try {
            System.out.printf("  [\u001B[34mPRODUCER\u001B[0m] %s started. Streaming %d jobs into queue...%n", 
                    producerName, jobFeed.size());

            for (JobPosting job : jobFeed) {
                // Thread.sleep simulates network ingestion latency
                Thread.sleep(60);
                queue.put(job);
                System.out.printf("  [\u001B[34mPRODUCER\u001B[0m] Pushed to queue: %s (%s)%n", 
                        job.getTitle(), job.getCompanyName());
            }

            // Signal to consumers that this producer has finished
            if (poisonPill != null) {
                queue.put(poisonPill);
            }

            System.out.printf("  [\u001B[32mPRODUCER-DONE\u001B[0m] %s finished stream.%n", producerName);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.printf("  [PRODUCER-ERROR] %s interrupted: %s%n", producerName, e.getMessage());
        }
    }
}
