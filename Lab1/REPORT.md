# Introduction to Formal Languages, Regular Grammars, and Finite Automata

### **Course:** Formal Languages & Finite Automata  
### **Author:** Bulat Cristian

## Theory

### Formal Grammar

A **formal grammar** is a set of rules used to generate strings in a language. It consists of:

- **VN (Non-terminals):** Symbols that can be replaced (e.g., S, A, B).
- **VT (Terminals):** Symbols that appear in the final string (e.g., a, b, c).
- **P (Production rules):** Transform non-terminals into sequences of terminals and/or non-terminals.
- **Start Symbol:** The initial non-terminal from which the derivation begins.

### Finite Automaton (FA)

A **finite automaton (FA)** is a computational model used to recognize patterns in strings. It consists of:

- **Q (States):** A finite set of states.
- **Σ (Alphabet):** A finite set of input symbols.
- **δ (Transition function):** Defines state changes based on input symbols.
- **q₀ (Start state):** The state where execution begins.
- **F (Final states):** Accepting states that determine if a string is valid.

### Grammar to Finite Automaton Conversion

- Each non-terminal is treated as a state.
- Production rules define transitions.
- Terminal-only transitions lead to final states.

A finite automaton can verify if a string belongs to a language by following state transitions based on input symbols. If the automaton reaches a final state, the string is accepted.

---

## Objectives

- Implement a class to represent a grammar.
- Generate valid strings from the given grammar.
- Implement functionality to convert the grammar into a finite automaton.
- Check if a given string belongs to the language of the finite automaton.

---

## Implementation Description

### Grammar Class

The `Grammar` class represents the given grammar with:

- **Non-terminal symbols (VN)**
- **Terminal symbols (VT)**
- **Production rules (P)**
- **Start symbol (S)**

A method generates five valid strings based on the production rules.

### FiniteAutomaton Class

The `FiniteAutomaton` class represents the equivalent finite automaton with:

- **States (Q)**
- **Alphabet (Sigma)**
- **Transition function (delta)**
- **Initial state (q₀)**
- **Final states (F)**

A method verifies whether a given string is valid according to the automaton.

---

## Code Snippets

### Grammar Class

```java
class Grammar {
    private Set<String> nonTerminals;
    private Set<String> terminals;
    private Map<String, List<String>> productionRules;
    private String startSymbol;

    public Grammar(Set<String> V_n, Set<String> V_t, Map<String, List<String>> P, String S) {
        this.nonTerminals = V_n;
        this.terminals = V_t;
        this.productionRules = P;
        this.startSymbol = S;
    }

    public String generateString() {
        Random random = new Random();
        StringBuilder result = new StringBuilder(startSymbol);

        while (true) {
            boolean replaced = false;
            for (String nonTerminal : nonTerminals) {
                if (result.toString().contains(nonTerminal)) {
                    List<String> productions = productionRules.get(nonTerminal);
                    String chosenProduction = productions.get(random.nextInt(productions.size()));
                    int index = result.indexOf(nonTerminal);
                    result.replace(index, index + nonTerminal.length(), chosenProduction);
                    replaced = true;
                    break;
                }
            }
            if (!replaced) break;
        }
        return result.toString();
    }

    public FiniteAutomaton toFiniteAutomaton() {
        return new FiniteAutomaton(this);
    }

    public Set<String> getNonTerminals() { return nonTerminals; }
    public Set<String> getTerminals() { return terminals; }
    public Map<String, List<String>> getProductionRules() { return productionRules; }
    public String getStartSymbol() { return startSymbol; }
}
```

### FiniteAutomaton Class

```java
class FiniteAutomaton {
    private Set<String> states;
    private Set<Character> alphabet;
    private Map<String, Map<Character, Set<String>>> transitions;  // One input can lead to multiple states
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
```

### Main Execution

```java
public class Main {
    public static void main(String[] args) {
        Set<String> V_n = Set.of("S", "I", "J", "K");
        Set<String> V_t = Set.of("a", "b", "c", "e", "n", "f", "m");
        Map<String, List<String>> P = new HashMap<>();
        P.put("S", List.of("cI"));
        P.put("I", List.of("bJ", "fI", "eK", "e"));
        P.put("J", List.of("nJ", "cS"));
        P.put("K", List.of("nK", "m"));

        String S = "S";
        Grammar grammar = new Grammar(V_n, V_t, P, S);

        System.out.println("Generated strings:");
        for (int i = 0; i < 5; i++) {
            System.out.println(grammar.generateString());
        }

        FiniteAutomaton automaton = grammar.toFiniteAutomaton();

        System.out.println();
        String testString1 = "cIbJ";
        System.out.println("Does \"" + testString1 + "\" belong to the language? " +
                automaton.stringBelongToLanguage(testString1));

        String testString2 = "cem";
        System.out.println("Does \"" + testString2 + "\" belong to the language? " +
                automaton.stringBelongToLanguage(testString2));
    }
}
```

## Screenshots

![Automaton visualization](https://via.placeholder.com/800x400?text=Automaton+Visualization)

## Results

1. Successfully implemented the Grammar class.
2. Generated valid strings based on the production rules.
3. Converted the grammar into a finite automaton.
4. Verified string acceptance using FA state transitions.

## References

1. Hopcroft E. and others. Introduction to Automata Theory, Languages and Computation
2. LFPC Guide (ELSE)
3. Introduction to formal languages (ELSE)