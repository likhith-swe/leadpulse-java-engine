package com.leadpulse.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Aggregated growth and hiring signal profile for a target B2B company.
 */
public class CompanySignal implements Comparable<CompanySignal> {
    private final String companyName;
    private int totalOpenings;
    private int recentVelocityCount; // jobs posted within last 7 days
    private final Set<String> departments;
    private final Set<String> detectedKeywords;
    private int intentScore; // 0 to 100
    private String intentTier; // VERY_HIGH, HIGH, MODERATE, LOW
    private String outreachReason;

    public CompanySignal(String companyName) {
        this.companyName = companyName;
        this.totalOpenings = 0;
        this.recentVelocityCount = 0;
        this.departments = new HashSet<>();
        this.detectedKeywords = new HashSet<>();
        this.intentScore = 0;
        this.intentTier = "LOW";
        this.outreachReason = "Standard hiring activity";
    }

    public synchronized void recordJob(JobPosting job, Set<String> keywords) {
        this.totalOpenings++;
        if (job.getDaysAgo() <= 7) {
            this.recentVelocityCount++;
        }
        this.departments.add(job.getDepartment());
        this.detectedKeywords.addAll(keywords);
    }

    public synchronized void updateScore(int score, String tier, String reason) {
        this.intentScore = score;
        this.intentTier = tier;
        this.outreachReason = reason;
    }

    public String getCompanyName() { return companyName; }
    public int getTotalOpenings() { return totalOpenings; }
    public int getRecentVelocityCount() { return recentVelocityCount; }
    public Set<String> getDepartments() { return Collections.unmodifiableSet(departments); }
    public Set<String> getDetectedKeywords() { return Collections.unmodifiableSet(detectedKeywords); }
    public int getIntentScore() { return intentScore; }
    public String getIntentTier() { return intentTier; }
    public String getOutreachReason() { return outreachReason; }

    @Override
    public int compareTo(CompanySignal other) {
        // Descending order of intent score for PriorityQueue ranking
        return Integer.compare(other.intentScore, this.intentScore);
    }

    @Override
    public String toString() {
        return String.format("%-16s | Score: %3d | Tier: %-9s | Jobs: %2d (%2d new) | Reason: %s",
                companyName, intentScore, intentTier, totalOpenings, recentVelocityCount, outreachReason);
    }
}
