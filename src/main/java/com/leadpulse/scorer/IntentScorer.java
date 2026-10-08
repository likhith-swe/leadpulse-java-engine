package com.leadpulse.scorer;

import com.leadpulse.model.CompanySignal;
import com.leadpulse.parser.KeywordTokenizer;

import java.util.Set;

/**
 * Calculates a composite B2B sales outreach score (0 - 100)
 * based on hiring volume, 7-day velocity, keyword weights, and department diversity.
 */
public class IntentScorer {

    private final KeywordTokenizer tokenizer;

    public IntentScorer(KeywordTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    /**
     * Recomputes the hiring intent score and updates the CompanySignal object.
     */
    public void scoreCompany(CompanySignal signal) {
        // Component 1: Volume Score (Max 30 points)
        // 1-3 jobs = 10 pts, 4-8 jobs = 20 pts, 9+ jobs = 30 pts
        int volumeScore = Math.min(30, signal.getTotalOpenings() * 4);

        // Component 2: 7-Day Velocity Score (Max 25 points)
        // Recent job postings indicate active budget expansion right now
        int velocityScore = Math.min(25, signal.getRecentVelocityCount() * 8);

        // Component 3: Keyword Weight Score (Max 30 points)
        int keywordWeight = tokenizer.calculateKeywordWeight(signal.getDetectedKeywords());
        int keywordScore = Math.min(30, (keywordWeight / 2));

        // Component 4: Multi-Department Diversity Bonus (Max 15 points)
        // If hiring across Engineering AND Sales AND Product, company is scaling fast
        Set<String> depts = signal.getDepartments();
        int diversityScore = Math.min(15, depts.size() * 5);

        // Composite Intent Score (0 - 100)
        int finalScore = Math.min(100, volumeScore + velocityScore + keywordScore + diversityScore);

        // Determine Tier
        String tier;
        if (finalScore >= 80) {
            tier = "VERY_HIGH";
        } else if (finalScore >= 60) {
            tier = "HIGH";
        } else if (finalScore >= 40) {
            tier = "MODERATE";
        } else {
            tier = "LOW";
        }

        // Generate Outreach Reason
        String reason = generateOutreachReason(signal, finalScore);
        signal.updateScore(finalScore, tier, reason);
    }

    private String generateOutreachReason(CompanySignal signal, int score) {
        Set<String> keywords = signal.getDetectedKeywords();
        Set<String> depts = signal.getDepartments();

        if (keywords.contains("series b") || keywords.contains("series a")) {
            return "Fresh Venture Funding: Aggressive team scale-out across " + String.join(", ", depts);
        }
        if (keywords.contains("head of") || keywords.contains("founding")) {
            return "Leadership Restructuring: Building new greenfield business lines";
        }
        if (keywords.contains("ai") || keywords.contains("llm") || keywords.contains("machine learning")) {
            return "AI Infrastructure Surge: Heavy technical talent acquisition";
        }
        if (signal.getRecentVelocityCount() >= 3) {
            return "Rapid 7-Day Spike: " + signal.getRecentVelocityCount() + " new roles opened this week";
        }
        if (score >= 60) {
            return "Cross-Functional Expansion across " + depts.size() + " departments";
        }
        return "Standard operational replenishment";
    }
}
