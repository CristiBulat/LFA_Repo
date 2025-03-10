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

### Grammar Classification Method

The classifyGrammar() method implements a hierarchical analysis to categorize the grammar according to the Chomsky hierarchy, checking from most restrictive (Type 3) to least restrictive (Type 0). The method systematically evaluates production rules to determine the grammar's structural constraints. For regular grammars (Type 3), it verifies both right-linear form (A → aB or A → a) and left-linear form (A → Ba or A → a). For context-free grammars (Type 2), it ensures all left-hand sides consist of single non-terminals. For context-sensitive grammars (Type 1), it confirms that right-hand sides are never shorter than left-hand sides, with the special exception allowing S → ε if S doesn't appear on any right-hand side. The classification proceeds through these checks sequentially, returning the most restrictive type that fits the grammar's structure.

```java

    //Check for type 3
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
    
    // Check for Type 2 (Context-Free Grammar)
    boolean isContextFree = true;
    for (String nonTerminal : productions.keySet()) {
        // In a CFG, LHS must be a single non-terminal
        if (nonTerminal.length() != 1 || !nonTerminalSymbols.contains(nonTerminal)) {
            isContextFree = false;
            break;
        }
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

```

### Checking if an FA is Deterministic

The isDeterministic() method implements a thorough analysis of the automaton's transition structure to verify deterministic behavior. For each state in the automaton, it examines all outgoing transitions for each symbol in the alphabet. The method meticulously checks if any state has multiple transitions for the same input symbol, which would violate the core deterministic property. The implementation accounts for states that might not have transitions defined for all symbols. This thorough state-by-state and symbol-by-symbol verification ensures proper classification of the automaton's deterministic nature, which is crucial for many algorithms that assume deterministic input.

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

### Converting FA to Regular Grammar

The toRegularGrammar() method implements a systematic transformation of a finite automaton into an equivalent right-linear regular grammar. This conversion leverages the fundamental relationship between Type 3 grammars and finite automata in the Chomsky hierarchy. The method maps states to non-terminal symbols and input alphabet to terminal symbols. It processes each transition to generate appropriate production rules: for transitions to non-final states, it creates rules of the form A → aB (where A is the current state, a is the input symbol, and B is the destination state); for transitions to final states, it adds both A → aB and A → a productions. Additionally, if the initial state is also a final state, an epsilon production is added for it. This comprehensive transformation ensures that the resulting grammar generates exactly the same language recognized by the original automaton.

```java

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

```

#### Converting NDFA to DFA

The toDFA() method implements the powerset construction algorithm to convert a non-deterministic finite automaton (NDFA) to its equivalent deterministic finite automaton (DFA). This critical transformation creates compound states in the DFA that correspond to sets of states in the NDFA. The implementation maintains a queue of state sets to process and a mapping between composite states and their constituent NDFA states. For each compound state, it determines if it should be marked as accepting (if any constituent state is accepting) and systematically computes transitions for each input symbol by collecting all possible next states from the original NDFA. The method handles the exponential growth of states efficiently using HashSets, ensuring proper state management during the transformation. This conversion is fundamental for many practical applications of automata theory, as many algorithms require deterministic input despite non-deterministic automata often being more intuitive to design.

```java

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