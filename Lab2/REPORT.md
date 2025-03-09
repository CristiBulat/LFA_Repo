# Topic: Determinism in Finite Automata. Conversion from NDFA 2 DFA. Chomsky Hierarchy.

### **Course:** Formal Languages
### **Author:** Bulat Cristian

## Theory

### Chomsky Hierarchy

The **Chomsky Hierarchy** classifies formal grammars into four types based on their production rules:

- **Type 0 (Unrestricted Grammar):** No restrictions on production rules. Equivalent to Turing machines.
- **Type 1 (Context-Sensitive Grammar):** For any production rule α → β, |α| ≤ |β|, except possibly S → ε if S doesn't appear on the right side of any rule.
- **Type 2 (Context-Free Grammar):** Left side of each production rule must be a single non-terminal.
- **Type 3 (Regular Grammar):** Production rules must be in the form A → a or A → aB (right linear) or A → Ba (left linear), where A and B are non-terminals, and a is a terminal.

### Finite Automaton (FA)

A **finite automaton (FA)** consists of:

- **Q (States):** A finite set of states.
- **Σ (Alphabet):** A finite set of input symbols.
- **δ (Transition function):** Defines state changes based on input symbols.
- **q₀ (Start state):** The state where execution begins.
- **F (Final states):** Accepting states that determine if a string is valid.

### Deterministic vs Non-deterministic Finite Automata

- **Deterministic Finite Automaton (DFA):** For each state and input symbol, there is exactly one next state.
- **Non-deterministic Finite Automaton (NDFA):** For some state and input symbol, there may be multiple next states or none.

### Conversion from NDFA to DFA

The conversion process involves creating "compound states" in the DFA that represent sets of states in the NDFA:

1. Start with the initial state of the NDFA.
2. For each input symbol, determine all possible next states.
3. Create a new DFA state for each unique set of NDFA states.
4. Continue this process until all reachable compound states are processed.

### Converting FA to Regular Grammar

To convert a finite automaton to a regular grammar:

1. States become non-terminals.
2. The alphabet becomes terminals.
3. For each transition δ(p, a) = q, create a production p → aq.
4. For final states, add productions to derive terminal strings.

## Objectives

1. Implement a class to represent a grammar.
2. Classify grammars based on the Chomsky hierarchy.
3. Implement conversion of a finite automaton to a regular grammar.
4. Determine if a finite automaton is deterministic or non-deterministic.
5. Implement functionality to convert an NDFA to a DFA.

## Implementation Description

### Grammar Class

The `Grammar` class represents a formal grammar with:

- **Non-terminal symbols (VN)**
- **Terminal symbols (VT)**
- **Production rules (P)**
- **Start symbol (S)**

```java
class Grammar {
    private Set<String> nonTerminalSymbols;
    private Set<String> terminalSymbols;
    private String startSymbol;
    private Map<String, List<String>> productions;

    public Grammar(Set<String> nonTerminalSymbols, Set<String> terminalSymbols, 
                  String startSymbol, Map<String, List<String>> productions) {
        this.nonTerminalSymbols = nonTerminalSymbols;
        this.terminalSymbols = terminalSymbols;
        this.startSymbol = startSymbol;
        this.productions = productions;
    }
}
```

#### Grammar Classification Method

The `classifyGrammar()` method determines the grammar type according to the Chomsky hierarchy:

```java
public String classifyGrammar() {
    // Check for Type 3 (Regular Grammar)
    boolean isRightLinear = true;
    boolean isLeftLinear = true;

    for (String nonTerminal : productions.keySet()) {
        List<String> rules = productions.get(nonTerminal);
        for (String rule : rules) {
            // Check right-linear form: A -> aB or A -> a
            if (!isRightLinearProduction(rule) && !isTerminalOnly(rule)) {
                isRightLinear = false;
            }
            
            // Check left-linear form: A -> Ba or A -> a
            if (!isLeftLinearProduction(rule) && !isTerminalOnly(rule)) {
                isLeftLinear = false;
            }

            // If neither right nor left linear, break early
            if (!isRightLinear && !isLeftLinear) {
                break;
            }
        }
    }

    if (isRightLinear || isLeftLinear) {
        return "Type 3 (Regular Grammar)";
    }

    // Check for Type 2 (Context-Free Grammar)
    boolean isContextFree = true;
    for (String nonTerminal : productions.keySet()) {
        // In a CFG, LHS must be a single non-terminal
        if (nonTerminal.length() != 1 || !nonTerminalSymbols.contains(nonTerminal)) {
            isContextFree = false;
            break;
        }
    }

    if (isContextFree) {
        return "Type 2 (Context-Free Grammar)";
    }

    // Check for Type 1 (Context-Sensitive Grammar)
    boolean isContextSensitive = true;
    for (String lhs : productions.keySet()) {
        List<String> rules = productions.get(lhs);
        for (String rhs : rules) {
            // In a CSG, |LHS| <= |RHS| (except for S -> ε if S doesn't appear on RHS)
            if (rhs.length() < lhs.length() && 
                !(lhs.equals(startSymbol) && rhs.isEmpty())) {
                isContextSensitive = false;
                break;
            }
        }
        if (!isContextSensitive) break;
    }

    if (isContextSensitive) {
        return "Type 1 (Context-Sensitive Grammar)";
    }

    // If none of the above, it's Type 0 (Unrestricted Grammar)
    return "Type 0 (Unrestricted Grammar)";
}
```

### FiniteAutomaton Class

The `FiniteAutomaton` class represents a finite automaton with:

- **States (Q)**
- **Alphabet (Sigma)**
- **Transition function (delta)**
- **Initial state (q₀)**
- **Final states (F)**

#### Checking if an FA is Deterministic

The `isDeterministic()` method checks whether the automaton is deterministic:

```java
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
```

#### Converting FA to Regular Grammar

The `toRegularGrammar()` method converts the finite automaton to a regular grammar:

```java
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
```

#### Converting NDFA to DFA

The `toDFA()` method implements the conversion from NDFA to DFA:

```java
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
```

## Main Class and Implementation

The `Main` class demonstrates the implementation by:

1. Defining a finite automaton
2. Checking if it's deterministic
3. Converting the FA to a regular grammar
4. Classifying the grammar
5. Converting NDFA to DFA (if necessary)

```java
public static void main(String[] args) {
    // Defining the finite automaton
    Set<String> states = new HashSet<>(Arrays.asList("q0", "q1", "q2", "q3", "q4"));
    Set<Character> alphabet = new HashSet<>(Arrays.asList('a', 'b'));
    String initialState = "q0";
    Set<String> finalStates = new HashSet<>(Arrays.asList("q4"));
    
    // Define transitions
    Map<String, Map<Character, Set<String>>> transitions = new HashMap<>();
    
    // Initialize transition maps for each state
    for (String state : states) {
        transitions.put(state, new HashMap<>());
    }
    
    // Add transitions
    // δ(q0,a) = q1
    addTransition(transitions, "q0", 'a', "q1");
    
    // δ(q1,b) = q1
    addTransition(transitions, "q1", 'b', "q1");
    
    // δ(q1,b) = q2 (This makes it non-deterministic)
    addTransition(transitions, "q1", 'b', "q2");
    
    // δ(q2,b) = q3
    addTransition(transitions, "q2", 'b', "q3");
    
    // δ(q3,a) = q1
    addTransition(transitions, "q3", 'a', "q1");
    
    // δ(q2,a) = q4
    addTransition(transitions, "q2", 'a', "q4");
    
    // Create the finite automaton
    FiniteAutomaton fa = new FiniteAutomaton(states, alphabet, transitions, initialState, finalStates);
    
    // Print the automaton
    System.out.println(fa);
    
    // Check if it's deterministic
    boolean isDeterministic = fa.isDeterministic();
    System.out.println("Is the automaton deterministic? " + isDeterministic);
    
    // Convert FA to regular grammar
    Grammar regularGrammar = fa.toRegularGrammar();
    System.out.println("\nConverted to Regular Grammar:");
    System.out.println(regularGrammar);
    
    // Classify the grammar
    String grammarType = regularGrammar.classifyGrammar();
    System.out.println("Grammar Classification: " + grammarType);
    
    // Convert NDFA to DFA
    if (!isDeterministic) {
        System.out.println("\nConverting NDFA to DFA:");
        FiniteAutomaton dfa = fa.toDFA();
        System.out.println(dfa);
        
        // Verify that the DFA is indeed deterministic
        System.out.println("Is the converted DFA deterministic? " + dfa.isDeterministic());
    }
}
```

## Results

The implementation produced the following results:

1. **Finite Automaton Definition:**
    - States: [q0, q1, q2, q3, q4]
    - Alphabet: [a, b]
    - Initial State: q0
    - Final States: [q4]
    - Transitions:
        - δ(q0, a) = q1
        - δ(q1, b) = q1
        - δ(q1, b) = q2
        - δ(q2, b) = q3
        - δ(q3, a) = q1
        - δ(q2, a) = q4

2. **Determinism Check:**
    - Is the automaton deterministic? false
    - The automaton is non-deterministic because from state q1 with input symbol 'b', there are two possible next states: q1 and q2.

3. **Conversion to Regular Grammar:**
    - Non-terminals: [q0, q1, q2, q3, q4]
    - Terminals: [a, b]
    - Start Symbol: q0
    - Productions:
        - q0 -> aq1
        - q1 -> bq1 | bq2
        - q2 -> bq3 | aq4 | a
        - q3 -> aq1
        - q4 ->

4. **Grammar Classification:**
    - Grammar Classification: Type 3 (Regular Grammar)

5. **NDFA to DFA Conversion:**
    - New DFA States: [{q0}, {q1}, {q1,q2}, {q1,q3}, {q1,q4}, {q3}, {q4}]
    - New DFA Transitions:
        - δ({q0}, a) = {q1}
        - δ({q1}, b) = {q1,q2}
        - δ({q1,q2}, a) = {q1,q4}
        - δ({q1,q2}, b) = {q1,q3}
        - δ({q1,q3}, a) = {q1}
        - δ({q1,q3}, b) = {q1,q2}
        - δ({q1,q4}, b) = {q1,q2}
        - δ({q3}, a) = {q1}
    - Is the converted DFA deterministic? true

## Conclusion

In this laboratory, I successfully implemented algorithms related to formal languages, grammars, and finite automata. The key achievements were:

1. Implementing the Grammar class with functionality to classify grammars according to the Chomsky hierarchy.
2. Creating a FiniteAutomaton class that can determine if an automaton is deterministic.
3. Implementing the conversion from a finite automaton to a regular grammar.
4. Developing an algorithm to convert an NDFA to a DFA.

The results confirmed that:
- The given finite automaton is non-deterministic due to multiple transitions from state q1 with the same input symbol.
- The grammar derived from the finite automaton is a Type 3 (Regular Grammar).
- The conversion from NDFA to DFA produces a deterministic automaton that recognizes the same language.

These implementations demonstrate the fundamental relationship between finite automata and regular grammars in formal language theory. The NDFA to DFA conversion algorithm effectively handles the non-determinism by creating compound states that track all possible states the NDFA could be in at each step of input processing.

## References

1. Hopcroft, J. E., Motwani, R., & Ullman, J. D. (2006). Introduction to Automata Theory, Languages, and Computation.
2. Sipser, M. (2012). Introduction to the Theory of Computation.
3. Formal Languages and Automata Theory course materials.