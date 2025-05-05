package org.lab5;

import java.util.*;

/**
 * Class to represent a formal grammar with Chomsky Normal Form conversion functionality
 */
public class Grammar {
    private Set<String> nonTerminals;
    private Set<String> terminals;
    private Map<String, List<String>> productions;
    private String startSymbol;

    public Grammar(Set<String> nonTerminals, Set<String> terminals, Map<String, List<String>> productions, String startSymbol) {
        this.nonTerminals = nonTerminals;
        this.terminals = terminals;
        this.productions = productions;
        this.startSymbol = startSymbol;
    }

    /**
     * Deep copy constructor to create a new Grammar instance from an existing one
     * @param other The grammar to copy
     */
    public Grammar(Grammar other) {
        this.nonTerminals = new HashSet<>(other.nonTerminals);
        this.terminals = new HashSet<>(other.terminals);
        this.startSymbol = other.startSymbol;

        // Deep copy of productions map
        this.productions = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : other.productions.entrySet()) {
            this.productions.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
    }

    /**
     * Step 1: Eliminate epsilon productions from the grammar
     */
    public void eliminateEpsilonProductions() {
        System.out.println("Step 1: Eliminating ε-productions");

        // Find all nullable symbols (symbols that can derive ε)
        Set<String> nullableSymbols = findNullableSymbols();
        System.out.println("Nullable symbols: " + nullableSymbols);

        // Generate new productions without epsilon
        Map<String, List<String>> newProductions = new HashMap<>();

        // First, copy all non-epsilon productions
        for (String nonTerminal : productions.keySet()) {
            List<String> rules = new ArrayList<>();

            for (String rule : productions.get(nonTerminal)) {
                if (!rule.isEmpty()) {  // Skip epsilon productions
                    rules.add(rule);
                }
            }

            newProductions.put(nonTerminal, rules);
        }

        // Add new productions considering nullable symbols
        for (String nonTerminal : productions.keySet()) {
            List<String> currentRules = newProductions.get(nonTerminal);
            Set<String> newRules = new HashSet<>();

            for (String rule : productions.get(nonTerminal)) {
                if (!rule.isEmpty()) {  // Skip epsilon rules
                    // Generate all possible combinations by removing nullable symbols
                    List<String> combinations = generateCombinations(rule, nullableSymbols);
                    for (String combination : combinations) {
                        if (!combination.isEmpty() && !rule.equals(combination)) {
                            newRules.add(combination);
                        }
                    }
                }
            }

            // Add new rules to the current rules
            currentRules.addAll(newRules);
            newProductions.put(nonTerminal, currentRules);
        }

        // Update the grammar's productions
        this.productions = newProductions;
        System.out.println("Grammar after eliminating ε-productions:");
        printGrammar();
    }

    /**
     * Find all nullable symbols in the grammar
     * @return Set of nullable non-terminal symbols
     */
    private Set<String> findNullableSymbols() {
        Set<String> nullableSymbols = new HashSet<>();
        boolean changed;

        // First pass: find direct nullable symbols (those with epsilon productions)
        for (String nonTerminal : productions.keySet()) {
            for (String rule : productions.get(nonTerminal)) {
                if (rule.isEmpty()) {
                    nullableSymbols.add(nonTerminal);
                    break;
                }
            }
        }

        // Iteratively find indirect nullable symbols
        do {
            changed = false;

            for (String nonTerminal : productions.keySet()) {
                if (nullableSymbols.contains(nonTerminal)) {
                    continue;  // Already marked as nullable
                }

                for (String rule : productions.get(nonTerminal)) {
                    boolean allNullable = true;

                    // Check if all symbols in the rule are nullable
                    for (int i = 0; i < rule.length(); i++) {
                        String symbol = rule.substring(i, i + 1);
                        if (!nullableSymbols.contains(symbol)) {
                            allNullable = false;
                            break;
                        }
                    }

                    if (allNullable && !rule.isEmpty()) {
                        nullableSymbols.add(nonTerminal);
                        changed = true;
                        break;
                    }
                }
            }
        } while (changed);

        return nullableSymbols;
    }

    /**
     * Generate all possible combinations of a rule by removing nullable symbols
     * @param rule The production rule
     * @param nullableSymbols Set of nullable symbols
     * @return List of all possible combinations
     */
    private List<String> generateCombinations(String rule, Set<String> nullableSymbols) {
        List<String> result = new ArrayList<>();
        generateCombinationsHelper(rule, 0, new StringBuilder(), nullableSymbols, result);
        return result;
    }

    private void generateCombinationsHelper(String rule, int index, StringBuilder current,
                                            Set<String> nullableSymbols, List<String> result) {
        if (index == rule.length()) {
            if (current.length() > 0) {
                result.add(current.toString());
            }
            return;
        }

        String symbol = rule.substring(index, index + 1);

        // Include the current symbol
        current.append(symbol);
        generateCombinationsHelper(rule, index + 1, current, nullableSymbols, result);
        current.deleteCharAt(current.length() - 1);

        // Skip the current symbol if it's nullable
        if (nullableSymbols.contains(symbol)) {
            generateCombinationsHelper(rule, index + 1, current, nullableSymbols, result);
        }
    }

    /**
     * Step 2: Eliminate renaming productions (unit productions) from the grammar
     * A unit production is of the form A -> B where B is a non-terminal
     */
    public void eliminateUnitProductions() {
        System.out.println("Step 2: Eliminating unit productions (renaming)");

        // Map to store non-unit productions for each non-terminal
        Map<String, List<String>> newProductions = new HashMap<>();
        for (String nonTerminal : nonTerminals) {
            newProductions.put(nonTerminal, new ArrayList<>());
        }

        // Find unit pairs (A,B) such that A ->* B
        Map<String, Set<String>> unitPairs = findUnitPairs();

        // For each non-terminal, add non-unit productions based on unit pairs
        for (String A : nonTerminals) {
            Set<String> unitReachable = unitPairs.getOrDefault(A, new HashSet<>());

            for (String B : unitReachable) {
                // For each non-unit production of B, add it to A
                for (String production : productions.get(B)) {
                    // Skip unit productions
                    if (production.length() != 1 || !nonTerminals.contains(production)) {
                        newProductions.get(A).add(production);
                    }
                }
            }
        }

        // Update the grammar's productions
        this.productions = newProductions;
        System.out.println("Grammar after eliminating unit productions:");
        printGrammar();
    }

    /**
     * Find all unit pairs (A,B) such that A ->* B where A,B are non-terminals
     * @return Map of non-terminals to sets of reachable non-terminals
     */
    private Map<String, Set<String>> findUnitPairs() {
        Map<String, Set<String>> unitPairs = new HashMap<>();

        // Initialize with identity pairs (A,A)
        for (String nonTerminal : nonTerminals) {
            Set<String> pairs = new HashSet<>();
            pairs.add(nonTerminal);  // A ->* A
            unitPairs.put(nonTerminal, pairs);
        }

        // Find unit pairs iteratively
        boolean changed;
        do {
            changed = false;

            for (String A : nonTerminals) {
                Set<String> reachable = unitPairs.get(A);
                Set<String> newReachable = new HashSet<>(reachable);

                for (String B : reachable) {
                    for (String production : productions.get(B)) {
                        // If B -> C is a unit production, then A ->* C
                        if (production.length() == 1 && nonTerminals.contains(production)) {
                            if (newReachable.add(production)) {
                                changed = true;
                            }
                        }
                    }
                }

                unitPairs.put(A, newReachable);
            }
        } while (changed);

        return unitPairs;
    }

    /**
     * Step 3: Eliminate inaccessible symbols from the grammar
     * A symbol is accessible if it can be reached from the start symbol
     */
    public void eliminateInaccessibleSymbols() {
        System.out.println("Step 3: Eliminating inaccessible symbols");

        // Find all accessible symbols
        Set<String> accessibleSymbols = findAccessibleSymbols();
        System.out.println("Accessible symbols: " + accessibleSymbols);

        // Remove inaccessible non-terminals and their productions
        Set<String> accessibleNonTerminals = new HashSet<>(accessibleSymbols);
        accessibleNonTerminals.retainAll(nonTerminals);

        // Update non-terminals and productions
        nonTerminals = accessibleNonTerminals;

        Map<String, List<String>> newProductions = new HashMap<>();
        for (String nonTerminal : nonTerminals) {
            newProductions.put(nonTerminal, productions.get(nonTerminal));
        }
        productions = newProductions;

        // Remove inaccessible terminals
        Set<String> accessibleTerminals = new HashSet<>(accessibleSymbols);
        accessibleTerminals.retainAll(terminals);
        terminals = accessibleTerminals;

        System.out.println("Grammar after eliminating inaccessible symbols:");
        printGrammar();
    }

    /**
     * Find all symbols (terminals and non-terminals) that are accessible from the start symbol
     * @return Set of accessible symbols
     */
    private Set<String> findAccessibleSymbols() {
        Set<String> accessibleSymbols = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        // Start with the start symbol
        queue.add(startSymbol);
        accessibleSymbols.add(startSymbol);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            // If current is a non-terminal, process its productions
            if (nonTerminals.contains(current) && productions.containsKey(current)) {
                for (String production : productions.get(current)) {
                    for (int i = 0; i < production.length(); i++) {
                        String symbol = production.substring(i, i + 1);
                        if (!accessibleSymbols.contains(symbol)) {
                            accessibleSymbols.add(symbol);
                            if (nonTerminals.contains(symbol)) {
                                queue.add(symbol);
                            }
                        }
                    }
                }
            }
        }

        return accessibleSymbols;
    }

    /**
     * Step 4: Eliminate non-productive symbols from the grammar
     * A non-terminal is productive if it can derive a string of terminals
     */
    public void eliminateNonProductiveSymbols() {
        System.out.println("Step 4: Eliminating non-productive symbols");

        // Find all productive non-terminals
        Set<String> productiveSymbols = findProductiveSymbols();
        System.out.println("Productive symbols: " + productiveSymbols);

        // Remove non-productive non-terminals and their productions
        Set<String> productiveNonTerminals = new HashSet<>(productiveSymbols);
        nonTerminals.retainAll(productiveNonTerminals);

        // Update productions
        Map<String, List<String>> newProductions = new HashMap<>();
        for (String nonTerminal : nonTerminals) {
            List<String> validProductions = new ArrayList<>();

            for (String production : productions.get(nonTerminal)) {
                boolean isValid = true;

                // Check if all symbols in the production are productive
                for (int i = 0; i < production.length(); i++) {
                    String symbol = production.substring(i, i + 1);
                    if (nonTerminals.contains(symbol) && !productiveSymbols.contains(symbol)) {
                        isValid = false;
                        break;
                    }
                }

                if (isValid) {
                    validProductions.add(production);
                }
            }

            newProductions.put(nonTerminal, validProductions);
        }

        productions = newProductions;

        System.out.println("Grammar after eliminating non-productive symbols:");
        printGrammar();
    }

    /**
     * Find all productive non-terminals in the grammar
     * @return Set of productive non-terminal symbols
     */
    private Set<String> findProductiveSymbols() {
        Set<String> productiveSymbols = new HashSet<>();
        boolean changed;

        // First pass: non-terminals with productions containing only terminals are productive
        for (String nonTerminal : nonTerminals) {
            for (String production : productions.get(nonTerminal)) {
                boolean containsOnlyTerminals = true;

                for (int i = 0; i < production.length(); i++) {
                    String symbol = production.substring(i, i + 1);
                    if (!terminals.contains(symbol)) {
                        containsOnlyTerminals = false;
                        break;
                    }
                }

                if (containsOnlyTerminals) {
                    productiveSymbols.add(nonTerminal);
                    break;
                }
            }
        }

        // Iteratively find more productive non-terminals
        do {
            changed = false;

            for (String nonTerminal : nonTerminals) {
                if (productiveSymbols.contains(nonTerminal)) {
                    continue;  // Already marked as productive
                }

                for (String production : productions.get(nonTerminal)) {
                    boolean allProductive = true;

                    // Check if all non-terminals in the production are productive
                    for (int i = 0; i < production.length(); i++) {
                        String symbol = production.substring(i, i + 1);
                        if (nonTerminals.contains(symbol) && !productiveSymbols.contains(symbol)) {
                            allProductive = false;
                            break;
                        }
                    }

                    if (allProductive) {
                        productiveSymbols.add(nonTerminal);
                        changed = true;
                        break;
                    }
                }
            }
        } while (changed);

        return productiveSymbols;
    }

    /**
     * Step 5: Convert the grammar to Chomsky Normal Form
     * In CNF, all productions are of the form:
     * A -> BC (where B and C are non-terminals)
     * A -> a (where a is a terminal)
     */
    public void convertToChomskyNormalForm() {
        System.out.println("Step 5: Converting to Chomsky Normal Form");

        // Step 5.1: Replace terminals in mixed productions
        System.out.println("Step 5.1: Replacing terminals in mixed productions...");
        replaceTerminalsInMixedProductions();

        // Step 5.2: Break long productions
        System.out.println("Step 5.2: Breaking long productions...");
        breakLongProductionsEfficient();

        System.out.println("Grammar in Chomsky Normal Form:");
        printGrammar();
    }

    /**
     * Replace terminals in mixed productions with new non-terminals
     * e.g., A -> aBC becomes A -> XBC and X -> a
     */
    private void replaceTerminalsInMixedProductions() {
        Map<String, String> terminalToNonTerminal = new HashMap<>();
        Map<String, List<String>> newProductions = new HashMap<>();

        // Copy existing productions first
        for (String nonTerminal : productions.keySet()) {
            newProductions.put(nonTerminal, new ArrayList<>(productions.get(nonTerminal)));
        }

        // For each terminal, create a new non-terminal
        for (String terminal : terminals) {
            String newNonTerminal = "X_" + terminal;
            terminalToNonTerminal.put(terminal, newNonTerminal);

            // Add new production: X_a -> a
            List<String> newRule = new ArrayList<>();
            newRule.add(terminal);
            newProductions.put(newNonTerminal, newRule);
            nonTerminals.add(newNonTerminal);
        }

        // Replace terminals in mixed productions
        for (String nonTerminal : productions.keySet()) {
            List<String> originalRules = productions.get(nonTerminal);
            List<String> updatedRules = new ArrayList<>();

            for (String rule : originalRules) {
                if (rule.length() > 1) {
                    // Mixed production - replace terminals
                    StringBuilder newRule = new StringBuilder();
                    boolean replacedTerminal = false;

                    for (int i = 0; i < rule.length(); i++) {
                        String symbol = rule.substring(i, i + 1);
                        if (terminals.contains(symbol)) {
                            newRule.append(terminalToNonTerminal.get(symbol));
                            replacedTerminal = true;
                        } else {
                            newRule.append(symbol);
                        }
                    }

                    if (replacedTerminal) {
                        updatedRules.add(newRule.toString());
                    } else {
                        updatedRules.add(rule);
                    }
                } else {
                    // Terminal-only or non-terminal-only production - keep as is
                    updatedRules.add(rule);
                }
            }

            newProductions.put(nonTerminal, updatedRules);
        }

        productions = newProductions;
    }

    /**
     * Efficient method for breaking long productions into binary form
     */
    private void breakLongProductionsEfficient() {
        // Track changes and new productions in a single pass
        Map<String, List<String>> newProductions = new HashMap<>(productions);
        int newVarCounter = 1;

        // First pass: identify all rules that need to be broken
        List<RuleToBreak> rulesToBreak = new ArrayList<>();

        for (String nonTerminal : productions.keySet()) {
            List<String> rules = productions.get(nonTerminal);

            for (int i = 0; i < rules.size(); i++) {
                String rule = rules.get(i);
                if (rule.length() > 2) {
                    rulesToBreak.add(new RuleToBreak(nonTerminal, rule, i));
                }
            }
        }

        // Process all rules that need to be broken
        if (!rulesToBreak.isEmpty()) {
            // Create the chain of new non-terminals for each long rule
            for (RuleToBreak ruleInfo : rulesToBreak) {
                String nonTerminal = ruleInfo.nonTerminal;
                String rule = ruleInfo.rule;

                // Remove the original rule
                newProductions.get(nonTerminal).remove(ruleInfo.ruleIndex);

                // Break the rule into a binary chain
                // For example, A -> BCDE becomes:
                // A -> BZ1, Z1 -> CZ2, Z2 -> DE

                String[] symbols = new String[rule.length()];
                for (int i = 0; i < rule.length(); i++) {
                    symbols[i] = rule.substring(i, i + 1);
                }

                if (symbols.length == 3) {
                    // Simple case: A -> BCD becomes A -> BY1, Y1 -> CD
                    String newNonTerminal = "Y" + newVarCounter++;
                    nonTerminals.add(newNonTerminal);

                    // Add A -> BY1
                    newProductions.get(nonTerminal).add(symbols[0] + newNonTerminal);

                    // Add Y1 -> CD
                    List<String> newRules = new ArrayList<>();
                    newRules.add(symbols[1] + symbols[2]);
                    newProductions.put(newNonTerminal, newRules);

                } else {
                    // For longer rules, create a chain of non-terminals
                    String currentNonTerminal = nonTerminal;
                    int currentPos = 0;

                    while (currentPos < symbols.length - 2) {
                        String newNonTerminal = "Y" + newVarCounter++;
                        nonTerminals.add(newNonTerminal);

                        // Add current -> FirstY
                        newProductions.get(currentNonTerminal).add(symbols[currentPos] + newNonTerminal);

                        // Set up for next iteration
                        currentNonTerminal = newNonTerminal;
                        currentPos++;

                        // Initialize production list for the new non-terminal
                        if (!newProductions.containsKey(currentNonTerminal)) {
                            newProductions.put(currentNonTerminal, new ArrayList<>());
                        }
                    }

                    // Add the last step: Y_n -> LastSecondLast
                    newProductions.get(currentNonTerminal).add(symbols[symbols.length - 2] + symbols[symbols.length - 1]);
                }
            }
        }

        // Update productions
        productions = newProductions;
    }

    /**
     * Helper class to track rules that need to be broken
     */
    private static class RuleToBreak {
        final String nonTerminal;
        final String rule;
        final int ruleIndex;

        RuleToBreak(String nonTerminal, String rule, int ruleIndex) {
            this.nonTerminal = nonTerminal;
            this.rule = rule;
            this.ruleIndex = ruleIndex;
        }
    }

    /**
     * Print the current state of the grammar
     */
    public void printGrammar() {
        System.out.println("G = (VN, VT, P, S)");
        System.out.println("VN = " + nonTerminals);
        System.out.println("VT = " + terminals);
        System.out.println("S = " + startSymbol);
        System.out.println("P = {");

        int ruleNumber = 1;
        List<String> sortedNonTerminals = new ArrayList<>(nonTerminals);
        Collections.sort(sortedNonTerminals); // Sort for consistent output

        for (String nonTerminal : sortedNonTerminals) {
            if (productions.containsKey(nonTerminal)) {
                List<String> rules = productions.get(nonTerminal);
                // Sort rules for consistent output
                Collections.sort(rules);

                for (String production : rules) {
                    System.out.println("  " + ruleNumber++ + ". " + nonTerminal + " → " +
                            (production.isEmpty() ? "ε" : production));
                }
            }
        }

        System.out.println("}");
        System.out.println();
    }

    /**
     * Apply all steps to convert the grammar to Chomsky Normal Form
     */
    public void convertToCNF() {
        System.out.println("Original Grammar:");
        printGrammar();

        // Step 1: Eliminate ε-productions
        eliminateEpsilonProductions();

        // Step 2: Eliminate unit productions
        eliminateUnitProductions();

        // Step 3: Eliminate inaccessible symbols
        eliminateInaccessibleSymbols();

        // Step 4: Eliminate non-productive symbols
        eliminateNonProductiveSymbols();

        // Step 5: Convert to Chomsky Normal Form
        convertToChomskyNormalForm();

        System.out.println("Final Grammar in Chomsky Normal Form:");
        printGrammar();
    }

    // Getters and setters
    public Set<String> getNonTerminals() {
        return nonTerminals;
    }

    public Set<String> getTerminals() {
        return terminals;
    }

    public Map<String, List<String>> getProductions() {
        return productions;
    }

    public String getStartSymbol() {
        return startSymbol;
    }
}