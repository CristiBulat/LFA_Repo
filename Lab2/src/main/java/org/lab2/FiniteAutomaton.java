package org.lab2;
import java.util.*;

/**
 * Class to represent a Finite Automaton
 */
public class FiniteAutomaton {
    private Set<String> states;
    private Set<Character> alphabet;
    private Map<String, Map<Character, Set<String>>> transitions;
    private String initialState;
    private Set<String> finalStates;

    public FiniteAutomaton(Set<String> states, Set<Character> alphabet,
                           Map<String, Map<Character, Set<String>>> transitions,
                           String initialState, Set<String> finalStates) {
        this.states = states;
        this.alphabet = alphabet;
        this.transitions = transitions;
        this.initialState = initialState;
        this.finalStates = finalStates;
    }

    /**
     * Check if the automaton is deterministic
     * @return true if deterministic, false otherwise
     */
    public boolean isDeterministic() {
        for (String state : states) {
            if (!transitions.containsKey(state)) {
                continue;  // No transitions from this state
            }

            Map<Character, Set<String>> stateTransitions = transitions.get(state);
            for (Character symbol : alphabet) {
                if (stateTransitions.containsKey(symbol)) {
                    Set<String> nextStates = stateTransitions.get(symbol);
                    if (nextStates.size() > 1) {
                        return false;  // Multiple next states for same symbol
                    }
                }
            }
        }
        return true;
    }

    /**
     * Convert FA to regular grammar
     * @return Grammar equivalent to this FA
     */
    public Grammar toRegularGrammar() {
        Set<String> nonTerminals = new HashSet<>();
        Set<String> terminals = new HashSet<>();
        Map<String, List<String>> productions = new HashMap<>();

        // States become non-terminals
        for (String state : states) {
            nonTerminals.add(state);
            productions.put(state, new ArrayList<>());
        }

        // Alphabet becomes terminals
        for (Character symbol : alphabet) {
            terminals.add(symbol.toString());
        }

        // Process transitions to create productions
        for (String fromState : transitions.keySet()) {
            Map<Character, Set<String>> stateTransitions = transitions.get(fromState);

            for (Character symbol : stateTransitions.keySet()) {
                Set<String> toStates = stateTransitions.get(symbol);

                for (String toState : toStates) {
                    // If to-state is final, add production A -> a
                    if (finalStates.contains(toState)) {
                        productions.get(fromState).add(symbol.toString());
                    }

                    // Add production A -> aB
                    productions.get(fromState).add(symbol.toString() + toState);
                }
            }
        }

        // Add epsilon production for initial state if it's final
        if (finalStates.contains(initialState)) {
            productions.get(initialState).add("");  // Epsilon
        }

        return new Grammar(nonTerminals, terminals, initialState, productions);
    }

    /**
     * Convert NDFA to DFA
     * @return DFA equivalent to this NDFA
     */
    public FiniteAutomaton toDFA() {
        // New DFA components
        Set<String> newStates = new HashSet<>();
        Map<String, Map<Character, Set<String>>> newTransitions = new HashMap<>();
        Set<String> newFinalStates = new HashSet<>();

        // Queue for processing new compound states
        Queue<Set<String>> stateQueue = new LinkedList<>();
        // Map to track the compound states we've already processed
        Map<Set<String>, String> stateNameMap = new HashMap<>();

        // Start with the initial state
        Set<String> initialStateSet = new HashSet<>();
        initialStateSet.add(initialState);
        stateQueue.add(initialStateSet);

        String newInitialState = setToStateName(initialStateSet);
        newStates.add(newInitialState);
        stateNameMap.put(initialStateSet, newInitialState);

        // Process all compound states in the queue
        while (!stateQueue.isEmpty()) {
            Set<String> currentStateSet = stateQueue.poll();
            String currentCompoundState = stateNameMap.get(currentStateSet);

            // Check if this compound state contains any final states
            for (String state : currentStateSet) {
                if (finalStates.contains(state)) {
                    newFinalStates.add(currentCompoundState);
                    break;
                }
            }

            // Initialize transitions for this compound state
            newTransitions.put(currentCompoundState, new HashMap<>());

            // Process each symbol in the alphabet
            for (Character symbol : alphabet) {
                Set<String> nextStates = new HashSet<>();

                // Collect all possible next states for this symbol
                for (String state : currentStateSet) {
                    if (transitions.containsKey(state) &&
                            transitions.get(state).containsKey(symbol)) {
                        nextStates.addAll(transitions.get(state).get(symbol));
                    }
                }

                // Skip if no transitions
                if (nextStates.isEmpty()) {
                    continue;
                }

                // Create a name for this new compound state
                String nextCompoundState;
                if (!stateNameMap.containsKey(nextStates)) {
                    nextCompoundState = setToStateName(nextStates);
                    stateNameMap.put(nextStates, nextCompoundState);
                    newStates.add(nextCompoundState);
                    stateQueue.add(nextStates);  // Process this new state later
                } else {
                    nextCompoundState = stateNameMap.get(nextStates);
                }

                // Add the transition
                if (!newTransitions.get(currentCompoundState).containsKey(symbol)) {
                    newTransitions.get(currentCompoundState).put(symbol, new HashSet<>());
                }
                newTransitions.get(currentCompoundState).get(symbol).add(nextCompoundState);
            }
        }

        return new FiniteAutomaton(newStates, alphabet, newTransitions, newInitialState, newFinalStates);
    }

    private String setToStateName(Set<String> stateSet) {
        if (stateSet.isEmpty()) return "∅";
        List<String> sortedStates = new ArrayList<>(stateSet);
        Collections.sort(sortedStates);
        return "{" + String.join(",", sortedStates) + "}";
    }

    // Helper method to add a transition (static utility method)
    public static void addTransition(Map<String, Map<Character, Set<String>>> transitions,
                                     String fromState, char symbol, String toState) {
        if (!transitions.get(fromState).containsKey(symbol)) {
            transitions.get(fromState).put(symbol, new HashSet<>());
        }
        transitions.get(fromState).get(symbol).add(toState);
    }

    // Getters
    public Set<String> getStates() {
        return states;
    }

    public Set<Character> getAlphabet() {
        return alphabet;
    }

    public Map<String, Map<Character, Set<String>>> getTransitions() {
        return transitions;
    }

    public String getInitialState() {
        return initialState;
    }

    public Set<String> getFinalStates() {
        return finalStates;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Finite Automaton:\n");
        sb.append("States: ").append(states).append("\n");
        sb.append("Alphabet: ").append(alphabet).append("\n");
        sb.append("Initial State: ").append(initialState).append("\n");
        sb.append("Final States: ").append(finalStates).append("\n");
        sb.append("Transitions:\n");

        List<String> sortedStates = new ArrayList<>(states);
        Collections.sort(sortedStates);

        for (String from : sortedStates) {
            if (!transitions.containsKey(from)) continue;

            Map<Character, Set<String>> stateTransitions = transitions.get(from);

            for (Character symbol : stateTransitions.keySet()) {
                Set<String> toStates = stateTransitions.get(symbol);
                for (String to : toStates) {
                    sb.append("  δ(").append(from).append(", ").append(symbol)
                            .append(") = ").append(to).append("\n");
                }
            }
        }

        return sb.toString();
    }
}