package com.leadpulse;

import com.leadpulse.engine.SignalPipeline;
import com.leadpulse.model.CompanySignal;
import com.leadpulse.model.JobPosting;
import com.leadpulse.repository.CompanyRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Main application entry point for the LeadPulse Java Intelligence Engine.
 */
public class Main {

    public static void main(String[] args) {
        printBanner();

        // 1. Prepare simulated live job feed (20 representative postings)
        List<JobPosting> sampleJobs = createSampleJobFeed();

        System.out.println("  Initialising Multi-Threaded Signal Pipeline...");
        System.out.println("  • Producer Threads: 1 (Career Portal Ingestor)");
        System.out.println("  • Consumer Workers: 3 (Concurrent Text Tokenizers)");
        System.out.println("  • Concurrency Sync: LinkedBlockingQueue<JobPosting> (Capacity: 50)");
        System.out.println("  • Data Store:       ConcurrentHashMap<String, CompanySignal>");
        System.out.println("  • Leaderboard Heap: PriorityQueue<CompanySignal> (Max-Heap)");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("  STARTING CONCURRENT INGESTION & INTENT SCORING PIPELINE");
        System.out.println("--------------------------------------------------------------------------------");

        long startTime = System.currentTimeMillis();

        try {
            // 2. Launch pipeline with 3 concurrent consumer workers
            SignalPipeline pipeline = new SignalPipeline(50, 3);
            CompanyRepository repository = pipeline.processFeed(sampleJobs);

            long elapsed = System.currentTimeMillis() - startTime;

            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("  PIPELINE COMPLETE in %d ms | Processed %d jobs across %d companies%n",
                    elapsed, sampleJobs.size(), repository.size());
            System.out.println("--------------------------------------------------------------------------------\n");

            // 3. Display Ranked B2B Sales Outreach Leaderboard (via PriorityQueue)
            printLeaderboard(repository);

            // 4. Print detailed breakdown of Top Lead
            printTopLeadBreakdown(repository);

            printFooter();

        } catch (InterruptedException e) {
            System.err.println("Pipeline execution interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private static List<JobPosting> createSampleJobFeed() {
        List<JobPosting> jobs = new ArrayList<>();

        // Scale AI (Aggressive AI & Series B expansion)
        jobs.add(new JobPosting("J1", "Scale AI", "Founding AI Infrastructure Engineer", "Engineering", 
                "Building new team for next-gen llm distributed systems and machine learning infrastructure", "San Francisco / Remote", 2));
        jobs.add(new JobPosting("J2", "Scale AI", "Head of Enterprise Sales", "Sales", 
                "Drive series b enterprise sales expansion targeting Fortune 500 b2b accounts", "New York", 3));
        jobs.add(new JobPosting("J3", "Scale AI", "Director of Product Management", "Product", 
                "Lead hypergrowth AI product roadmap and developer ecosystem", "San Francisco", 5));

        // Razorpay (Bangalore Fintech Scale)
        jobs.add(new JobPosting("J4", "Razorpay", "VP of Engineering - Cloud Platform", "Engineering", 
                "Oversee distributed systems and cloud architecture handling millions of transactions", "Bangalore, India", 1));
        jobs.add(new JobPosting("J5", "Razorpay", "Senior Account Executive", "Sales", 
                "Accelerate b2b revenue growth and enterprise merchant acquisitions", "Bangalore, India", 4));
        jobs.add(new JobPosting("J6", "Razorpay", "Lead Machine Learning Engineer", "AI/Data", 
                "Deploy ai models for real-time fraud detection and risk analytics", "Bangalore, India", 6));

        // Anthropic (LLM research & Infrastructure)
        jobs.add(new JobPosting("J7", "Anthropic", "Founding Cloud Infrastructure Architect", "Infrastructure", 
                "Design massive kubernetes clusters and scalable cloud architecture for llm clusters", "San Francisco", 2));
        jobs.add(new JobPosting("J8", "Anthropic", "Head of Commercial Partnerships", "Business", 
                "Lead enterprise sales negotiations and b2b customer success strategy", "San Francisco", 4));

        // Vercel (Developer Experience & Platform)
        jobs.add(new JobPosting("J9", "Vercel", "Director of Developer Relations", "Marketing", 
                "Lead global outreach and evangelize frontend cloud architecture", "Remote", 5));
        jobs.add(new JobPosting("J10", "Vercel", "Strategic Account Executive", "Sales", 
                "Drive enterprise sales and expand inbound leads into high-acv software contracts", "San Francisco", 7));

        // Zepto (Bangalore Quick Commerce Scaling)
        jobs.add(new JobPosting("J11", "Zepto", "Lead Backend Architect", "Engineering", 
                "Building new team to redesign supply chain distributed systems in greenfield setup", "Bangalore, India", 1));
        jobs.add(new JobPosting("J12", "Zepto", "Director of Supply Operations", "Operations", 
                "Managing hypergrowth expansion across 15 new Indian tier-1 cities", "Bangalore, India", 3));

        // Stripe (Global payments & infrastructure)
        jobs.add(new JobPosting("J13", "Stripe", "Staff Systems Engineer", "Engineering", 
                "Maintain core payment infrastructure and high-availability distributed systems", "Dublin / Remote", 8));
        jobs.add(new JobPosting("J14", "Stripe", "Enterprise Account Executive", "Sales", 
                "Target global enterprise platforms for cross-border b2b billing expansion", "London", 9));

        // Supabase (Postgres & Cloud)
        jobs.add(new JobPosting("J15", "Supabase", "Lead Database Engineer", "Engineering", 
                "Greenfield development on distributed postgres and open source cloud tools", "Remote", 4));

        // Datadog (Observability & Cloud)
        jobs.add(new JobPosting("J16", "Datadog", "Kubernetes Reliability Engineer", "Infrastructure", 
                "Scaling cloud infrastructure observability across multi-region cloud architecture", "Boston", 10));

        // Swiggy (Consumer tech Bangalore)
        jobs.add(new JobPosting("J17", "Swiggy", "Senior Data Scientist", "Analytics", 
                "Optimise logistics algorithms and real-time demand prediction models", "Bangalore, India", 11));

        return jobs;
    }

    private static void printBanner() {
        System.out.println("================================================================================");
        System.out.println("      \u001B[1;36mLEADPULSE AI — B2B HIRING SIGNAL & JOB INTELLIGENCE ENGINE\u001B[0m");
        System.out.println("      Java Micro Project | Concurrency & Text Processing Architecture");
        System.out.println("      Student: Likhith S | Alliance University B.Tech CSE (Bangalore)");
        System.out.println("================================================================================\n");
    }

    private static void printLeaderboard(CompanyRepository repository) {
        System.out.println("\u001B[1;32m=== HIGH-INTENT B2B SALES OUTREACH LEADERBOARD (PriorityQueue Max-Heap) ===\u001B[0m");
        System.out.printf("%-4s | %-15s | %-6s | %-10s | %-13s | %s%n",
                "RANK", "COMPANY", "SCORE", "TIER", "JOBS (7-DAY)", "OUTREACH TRIGGER");
        System.out.println("--------------------------------------------------------------------------------");

        List<CompanySignal> ranked = repository.getTopRanked(10);
        int rank = 1;
        for (CompanySignal signal : ranked) {
            String color = signal.getIntentScore() >= 80 ? "\u001B[32m" : 
                          signal.getIntentScore() >= 60 ? "\u001B[34m" : "\u001B[37m";
            System.out.printf("%s#%-3d | %-15s | %3d/100 | %-10s | %2d total (%d new)| %s\u001B[0m%n",
                    color, rank++, signal.getCompanyName(), signal.getIntentScore(), 
                    signal.getIntentTier(), signal.getTotalOpenings(), 
                    signal.getRecentVelocityCount(), signal.getOutreachReason());
        }
        System.out.println("--------------------------------------------------------------------------------\n");
    }

    private static void printTopLeadBreakdown(CompanyRepository repository) {
        List<CompanySignal> top = repository.getTopRanked(1);
        if (top.isEmpty()) return;

        CompanySignal leader = top.get(0);
        System.out.println("\u001B[1;33m>>> #1 TOP PROSPECT DEEP-DIVE INSPECTION <<<\u001B[0m");
        System.out.printf("  Company Name:        \u001B[1m%s\u001B[0m%n", leader.getCompanyName());
        System.out.printf("  Sales Intent Score:  \u001B[32m%d / 100\u001B[0m (%s)%n", 
                leader.getIntentScore(), leader.getIntentTier());
        System.out.printf("  Hiring Velocity:     %d total open roles (%d posted in last 7 days)%n", 
                leader.getTotalOpenings(), leader.getRecentVelocityCount());
        System.out.printf("  Active Departments:  %s%n", leader.getDepartments());
        System.out.printf("  Extracted Signals:   \u001B[36m%s\u001B[0m%n", leader.getDetectedKeywords());
        System.out.printf("  Recommended Pitch:   \"%s\"%n", leader.getOutreachReason());
        System.out.println();
    }

    private static void printFooter() {
        System.out.println("================================================================================");
        System.out.println("  Full-Stack Cloud Companion: https://leadpulse-build-2.vercel.app");
        System.out.println("  GitHub Repository:          https://github.com/likhith-swe/leadpulse-b2b-saas");
        System.out.println("================================================================================");
    }
}
