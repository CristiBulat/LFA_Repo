package org.lab4;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexProcessor {
    public List<String> generateCombinations(String regex) {
        List<String> combinations = new ArrayList<>();
        processDynamicRegex(regex, "", combinations);
        return combinations;
    }

    private void processDynamicRegex(String regex, String currentString, List<String> combinations) {
        // Base case: if no more regex to process
        if (regex.isEmpty()) {
            combinations.add(currentString);
            return;
        }

        // Handle complex patterns step by step
        // First, check for parenthesized groups
        Matcher groupMatcher = Pattern.compile("\\(([^()]+)\\)").matcher(regex);
        if (groupMatcher.find()) {
            String group = groupMatcher.group(1);
            // Handle alternation within group
            if (group.contains("|")) {
                String[] alternatives = group.split("\\|");
                for (String alt : alternatives) {
                    String remainingRegex = regex.replaceFirst("\\(" + Pattern.quote(group) + "\\)", alt);
                    processDynamicRegex(remainingRegex, currentString, combinations);
                }
                return;
            }
        }

        // Handle optional character (?)
        if (regex.contains("?")) {
            String[] parts = regex.split("\\?", 2);
            // Try without the optional character
            processDynamicRegex(parts.length > 1 ? parts[1] : "", currentString, combinations);
            // Try with the optional character
            processDynamicRegex(parts.length > 1 ? parts[1] : "", currentString + parts[0], combinations);
            return;
        }

        // Handle character repetition (^)
        Matcher repetitionMatcher = Pattern.compile("(.)\\^(\\d+)").matcher(regex);
        if (repetitionMatcher.find()) {
            char character = repetitionMatcher.group(1).charAt(0);
            int repetitions = Integer.parseInt(repetitionMatcher.group(2));

            for (int i = 1; i <= Math.min(repetitions, 5); i++) {
                String repeatedChar = String.valueOf(character).repeat(i);
                String remainingRegex = regex.replaceFirst(Pattern.quote(character + "^" + repetitions), "");
                processDynamicRegex(remainingRegex, currentString + repeatedChar, combinations);
            }
            return;
        }

        // Handle Kleene star (*)
        Matcher starMatcher = Pattern.compile("(.)\\*").matcher(regex);
        if (starMatcher.find()) {
            char character = starMatcher.group(1).charAt(0);
            // Try 0 to 5 repetitions
            for (int i = 0; i <= 5; i++) {
                String repeatedChar = String.valueOf(character).repeat(i);
                String remainingRegex = regex.replaceFirst(Pattern.quote(character + "*"), "");
                processDynamicRegex(remainingRegex, currentString + repeatedChar, combinations);
            }
            return;
        }

        // Handle Kleene plus (+)
        Matcher dynamicRepMatcher = Pattern.compile("(.)\\^\\+").matcher(regex);
        if (dynamicRepMatcher.find()) {
            char character = dynamicRepMatcher.group(1).charAt(0);
            // Try 1 to 5 repetitions for the + case
            for (int i = 1; i <= 5; i++) {
                String repeatedChar = String.valueOf(character).repeat(i);
                String remainingRegex = regex.replaceFirst(Pattern.quote(character + "^+"), "");
                processDynamicRegex(remainingRegex, currentString + repeatedChar, combinations);
            }
            return;
        }

        // Handle single character
        if (regex.length() == 1) {
            combinations.add(currentString + regex);
            return;
        }

        // Recursive case
        char firstChar = regex.charAt(0);
        processDynamicRegex(regex.substring(1), currentString + firstChar, combinations);
    }

    // Bonus: Detailed processing sequence method
    public void showProcessingSequence(String regex) {
        System.out.println("Detailed Processing Sequence for Regex: " + regex);
        System.out.println("Step 1: Handle Parenthesized Groups");
        System.out.println("  - Identifies and processes grouped expressions");
        System.out.println("  - Resolves alternations within groups");

        System.out.println("\nStep 2: Process Optional Characters '?'");
        System.out.println("  - Checks for characters that can be optionally included");
        System.out.println("  - Generates combinations with and without optional characters");

        System.out.println("\nStep 3: Handle Character Repetition '^'");
        System.out.println("  - Identifies characters that need specific repetitions");
        System.out.println("  - Limits repetition to max 5 times");

        System.out.println("\nStep 4: Resolve Kleene Star '*'");
        System.out.println("  - Handles zero or more repetitions of a character");
        System.out.println("  - Generates combinations with 0-5 repetitions");

        System.out.println("\nStep 5: Resolve Kleene Plus '+'");
        System.out.println("  - Handles one or more repetitions of a character");
        System.out.println("  - Generates combinations with 1-5 repetitions");

        System.out.println("\nStep 6: Process Remaining Characters");
        System.out.println("  - Recursively process any remaining regex components");
        System.out.println("  - Builds up valid string combinations");
    }
}