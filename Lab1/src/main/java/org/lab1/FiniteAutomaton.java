package org.lab1;
import java.util.*;
import java.util.stream.Collectors;

class FiniteAutomaton {
    private Set<String> states;
    private Set<Character> alphabet;
    private Map<String, Map<Character, Set<String>>> transitions;
    private String initialState;
    private Set<String> finalStates;

    public FiniteAutomaton(Grammar grammar) {
        this.states = new HashSet<>(grammar.getNonTerminals());
        this.alphabet = new HashSet<>(grammar.getTerminals().stream()
                .filter(s -> s.length() == 1)
                .map(s -> s.charAt(0))
                .collect(Collectors.toSet()));
        this.transitions = new HashMap<>();
        this.finalStates = new HashSet<>();

        // Initialize transitions
        for (String state : states) {
            transitions.put(state, new HashMap<>());
        }

        // Process production rules to build transitions
        for (String nonTerminal : grammar.getNonTerminals()) {
            for (String production : grammar.getProductionRules().get(nonTerminal)) {
                // If production is epsilon or empty string, make this a final state
                if (production.isEmpty() || production.equals("ε")) {
                    finalStates.add(nonTerminal);
                    continue;
                }

                // Check if production starts with a terminal
                if (production.length() >= 1) {
                    char firstChar = production.charAt(0);

                    // Make sure the character is in the alphabet (is a terminal)
                    if (grammar.getTerminals().contains(String.valueOf(firstChar))) {
                        String remainingState = production.length() > 1 ? production.substring(1) : "";

                        // If there's nothing after the terminal, it's an accepting state
                        if (remainingState.isEmpty()) {
                            // Create a special accepting state if needed
                            String acceptState = nonTerminal + "_accept";
                            states.add(acceptState);
                            finalStates.add(acceptState);

                            // Add transition
                            transitions.get(nonTerminal)
                                    .computeIfAbsent(firstChar, k -> new HashSet<>())
                                    .add(acceptState);
                        } else {
                            // Add transition to the remaining state
                            transitions.get(nonTerminal)
                                    .computeIfAbsent(firstChar, k -> new HashSet<>())
                                    .add(remainingState);
                        }
                    }
                }
            }
        }

        this.initialState = grammar.getStartSymbol();
    }

    public boolean stringBelongToLanguage(String input) {
        Set<String> currentStates = new HashSet<>();
        currentStates.add(initialState);

        // Process each symbol in the input
        for (char symbol : input.toCharArray()) {
            Set<String> nextStates = new HashSet<>();

            // For each current state, find all possible next states
            for (String state : currentStates) {
                if (transitions.containsKey(state) &&
                        transitions.get(state).containsKey(symbol)) {
                    nextStates.addAll(transitions.get(state).get(symbol));
                }
            }

            if (nextStates.isEmpty()) {
                return false; // No valid transitions
            }
            currentStates = nextStates;
        }

        // Check if we ended in any final state
        for (String state : currentStates) {
            if (finalStates.contains(state)) {
                return true;
            }
        }
        return false;
    }
}