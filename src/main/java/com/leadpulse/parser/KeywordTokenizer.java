package com.leadpulse.parser;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Text processing and tokenization utility that analyzes job descriptions
 * to extract high-intent growth and expansion signals.
 */
public class KeywordTokenizer {

    // Common English stop words to filter out
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "and", "or", "in", "on", "at", "to", "for", "with",
            "is", "are", "was", "were", "of", "by", "from", "be", "this", "that",
            "we", "you", "our", "as", "it", "will", "have", "has", "not", "but"
    ));

    // High-value growth signal dictionaries
    private static final Map<String, Integer> SIGNAL_DICTIONARY = new HashMap<>();

    static {
        // Leadership & Founding signals (+25 points)
        SIGNAL_DICTIONARY.put("founding", 25);
        SIGNAL_DICTIONARY.put("head of", 20);
        SIGNAL_DICTIONARY.put("vp", 20);
        SIGNAL_DICTIONARY.put("director", 18);
        SIGNAL_DICTIONARY.put("lead", 12);

        // Scaling & Capital expansion signals (+20 points)
        SIGNAL_DICTIONARY.put("scaling", 20);
        SIGNAL_DICTIONARY.put("series b", 25);
        SIGNAL_DICTIONARY.put("series a", 20);
        SIGNAL_DICTIONARY.put("hypergrowth", 25);
        SIGNAL_DICTIONARY.put("expansion", 18);
        SIGNAL_DICTIONARY.put("greenfield", 15);
        SIGNAL_DICTIONARY.put("building new team", 25);

        // Core Engineering & AI signals (+15 points)
        SIGNAL_DICTIONARY.put("distributed systems", 15);
        SIGNAL_DICTIONARY.put("machine learning", 18);
        SIGNAL_DICTIONARY.put("ai", 15);
        SIGNAL_DICTIONARY.put("llm", 20);
        SIGNAL_DICTIONARY.put("infrastructure", 12);
        SIGNAL_DICTIONARY.put("kubernetes", 10);
        SIGNAL_DICTIONARY.put("cloud architecture", 15);

        // Go-To-Market / Enterprise Sales signals (+15 points)
        SIGNAL_DICTIONARY.put("account executive", 15);
        SIGNAL_DICTIONARY.put("enterprise sales", 20);
        SIGNAL_DICTIONARY.put("customer success", 12);
        SIGNAL_DICTIONARY.put("revenue", 12);
        SIGNAL_DICTIONARY.put("b2b", 10);
        SIGNAL_DICTIONARY.put("inbound leads", 15);
    }

    private static final Pattern CLEAN_PATTERN = Pattern.compile("[^a-zA-Z0-9\\s]");

    /**
     * Tokenizes text and detects matching intent keywords.
     */
    public Set<String> extractSignals(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> matchedSignals = new HashSet<>();
        String lowerCaseText = text.toLowerCase();

        // 1. Check multi-word phrase patterns directly (e.g., "series b", "head of")
        for (String phrase : SIGNAL_DICTIONARY.keySet()) {
            if (phrase.contains(" ") && lowerCaseText.contains(phrase)) {
                matchedSignals.add(phrase);
            }
        }

        // 2. Tokenize single words using StringTokenizer
        String cleaned = CLEAN_PATTERN.matcher(lowerCaseText).replaceAll(" ");
        StringTokenizer tokenizer = new StringTokenizer(cleaned);

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.length() > 1 && !STOP_WORDS.contains(token)) {
                if (SIGNAL_DICTIONARY.containsKey(token)) {
                    matchedSignals.add(token);
                }
            }
        }

        return matchedSignals;
    }

    /**
     * Calculates total keyword signal weight for a collection of detected signals.
     */
    public int calculateKeywordWeight(Set<String> detectedSignals) {
        int totalWeight = 0;
        for (String signal : detectedSignals) {
            totalWeight += SIGNAL_DICTIONARY.getOrDefault(signal, 5);
        }
        return totalWeight;
    }
}
