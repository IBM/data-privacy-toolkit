/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OSIdentifier extends AbstractIdentifier {
    private final Set<String> terms;
    private final Set<String> testDetectionPrefixes = new HashSet<>(Arrays.asList("mac os", "macos", "mac", "windows"));
    private final Set<String> testDetectionSuffixes = new HashSet<>(List.of("linux"));

    private final int minimumLength;
    private final int maximumLength;

    public OSIdentifier() {
        terms = populateTerms();

        this.minimumLength = terms.stream().mapToInt(String::length).min().orElseGet(() -> 0);
        this.maximumLength = terms.stream().mapToInt(String::length).max().orElseGet(() -> Integer.MAX_VALUE);
    }

    private Set<String> populateTerms() {
        try (
                InputStream inputStream = OSIdentifier.class.getResourceAsStream("/os_processed.csv")
        ) {
            assert inputStream != null;
            try (Reader reader = new InputStreamReader(inputStream);
                 BufferedReader bufferedReader = new BufferedReader(reader)
            ) {
                return bufferedReader.lines()
                        .map(String::trim)
                        .map(this::normalize)
                        .collect(Collectors.toSet());

            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to load the processed terms", e);
        }
    }

    @Override
    public ProviderType getType() {
        return ProviderType.valueOf("OS-NAMES");
    }

    private String normalize(String text) {
        return text.toLowerCase()
                .replaceAll("\\s's", "'s")
                .replaceAll("\n", " ")
                .replaceAll("\\s+", " ");
    }

    @Override
    public boolean isOfThisType(String data) {
        String normalized = normalize(data);
        return terms.contains(normalized) || checkPrefixSuffixPatterns(data);
    }

    private boolean checkPrefixSuffixPatterns(String data) {
        String normalized = data.toLowerCase();

        for (String prefix : testDetectionPrefixes) {
            if (normalized.startsWith(prefix)) {
                String candidate = normalized.substring(prefix.length());
                if (candidate.length() >= 2 && Character.isWhitespace(candidate.charAt(0))) {
                    candidate = candidate.trim();
                    if (this.terms.contains(candidate)) {
                        return true;
                    }
                }
            }
        }

        String[] tokens = data.split("[\\s+|:]");
        if (tokens.length < 2) {
            return false;
        }

        for (String suffix : testDetectionSuffixes) {
            if (normalized.endsWith(suffix)) {
                String candidate = normalized.substring(0, normalized.length() - suffix.length());
                if (candidate.length() >= 2) {
                    candidate = candidate.trim();
                    if (this.terms.contains(candidate)) {
                        return true;
                    }
                }
            }
        }


        return false;
    }

    @Override
    public String getDescription() {
        return "Look up mechanism for ";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.NONE;
    }

    @Override
    public int getMinimumLength() {
        return minimumLength;
    }

    @Override
    public int getMaximumLength() {
        return maximumLength;
    }
}

