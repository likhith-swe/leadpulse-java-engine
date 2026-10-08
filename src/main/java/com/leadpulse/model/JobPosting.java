package com.leadpulse.model;

import java.time.LocalDateTime;

/**
 * Encapsulates raw job listing data ingested from company careers pages.
 */
public class JobPosting {
    private final String id;
    private final String companyName;
    private final String title;
    private final String department;
    private final String description;
    private final String location;
    private final int daysAgo;
    private final LocalDateTime ingestedAt;

    public JobPosting(String id, String companyName, String title, String department, 
                      String description, String location, int daysAgo) {
        this.id = id;
        this.companyName = companyName;
        this.title = title;
        this.department = department;
        this.description = description;
        this.location = location;
        this.daysAgo = daysAgo;
        this.ingestedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getCompanyName() { return companyName; }
    public String getTitle() { return title; }
    public String getDepartment() { return department; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public int getDaysAgo() { return daysAgo; }
    public LocalDateTime getIngestedAt() { return ingestedAt; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s, %d days ago)", 
                companyName, title, department, location, daysAgo);
    }
}
