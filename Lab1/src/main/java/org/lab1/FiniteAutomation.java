package org.lab1;
import java.util.*;

class FiniteAutomaton {
    private Set<String> states;
    private Set<Character> alphabet;
    private Map<String, Map<Character, String>> transitions;
    private String initialState;
    private Set<String> finalStates;

    public FiniteAutomaton(Grammar grammar) {
        this.states = new HashSet<>(grammar.getNonTerminals());
        this.alphabet = new HashSet<>();
        this.transitions = new HashMap<>();
        this.finalStates = new HashSet<>();

        for (String nonTerminal : grammar.getNonTerminals()) {
            transitions.put(nonTerminal, new HashMap<>());
            for (String production : grammar.getProductionRules().get(nonTerminal)) {
                if (production.length() == 1 && grammar.getTerminals().contains(production)) {
                    finalStates.add(production);
                } else if (production.length() >= 1) {
                    char terminal = production.charAt(0);
                    String nextState = production.length() > 1 ? production.substring(1) : "";
                    alphabet.add(terminal);
                    transitions.get(nonTerminal).put(terminal, nextState);
                }
            }
        }
        this.initialState = grammar.getStartSymbol();
    }

    public boolean stringBelongToLanguage(String input) {
        String currentState = initialState;
        for (char symbol : input.toCharArray()) {
            if (!transitions.containsKey(currentState) || !transitions.get(currentState).containsKey(symbol)) {
                return false;
            }
            currentState = transitions.get(currentState).get(symbol);
        }
        return finalStates.contains(currentState);
    }
}