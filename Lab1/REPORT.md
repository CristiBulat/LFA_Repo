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

#### Terminals and Non-Terminals:
The sets of terminals (`V_t`) and non-terminals (`V_n`) are stored as `Set<String>`. This ensures that each symbol appears only once, which is useful for checking and processing unique symbols.
```java
Set<String> nonTerminals = new HashSet<>();
Set<String> terminals = new HashSet<>();
```

#### HashMap Usage (productionRules):
In the `Grammar` class, the `productionRules` `HashMap` is used to store the production rules of the grammar.
The key in this map is a non-terminal symbol (e.g., "S", "I"), and the value is a list of possible production strings (e.g., ["cI", "aS"]).
```java
HashMap<String, List<String>> productionRules = new HashMap<>();
productionRules.put("S", Arrays.asList("cI", "aS"));
```


#### Random String Generation:
The `generateString()` method uses the `productionRules` `HashMap` to replace non-terminal symbols in the `startSymbol` with their corresponding production rules.
This process continues until no more non-terminals are left to replace, ensuring that a valid string is generated according to the grammar.
```java
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
```


### FiniteAutomaton Class

The `FiniteAutomaton` class represents the equivalent finite automaton with:

- **States (Q)**
- **Alphabet (Sigma)**
- **Transition function (delta)**
- **Initial state (q₀)**
- **Final states (F)**

#### States (Q):
The `states` set represents the states in the finite automaton. Each state corresponds to a non-terminal symbol in the grammar.
The initial state is the `startSymbol` of the grammar, and transitions are made based on production rules.
```java
Set<String> states = new HashSet<>(grammar.getNonTerminals());
```

#### Alphabet (Σ):
The `alphabet` set contains the terminal symbols of the grammar, which are the input symbols for the finite automaton.
This set is constructed by extracting the single-character terminal symbols from the grammar.
```java
Set<Character> alphabet = grammar.getTerminals().stream()
        .filter(s -> s.length() == 1)
        .map(s -> s.charAt(0))
        .collect(Collectors.toSet());
```

#### Transition Function (δ):
The `transitions` `HashMap` stores the transition function, where the key is a state (non-terminal) and the value is another `HashMap` that maps input symbols (characters from the alphabet) to the resulting set of next states.
```java
HashMap<String, HashMap<Character, Set<String>>> transitions = new HashMap<>();
```

---

## Screenshots

![image alt](https://github.com/CristiBulat/LFA_Repo/blob/laboratory1/Screenshot.png?raw=true)

## Results

1. Successfully implemented the Grammar class.
2. Generated valid strings based on the production rules.
3. Converted the grammar into a finite automaton.
4. Verified string acceptance using FA state transitions.

## Conclusion

In this laboratory, we explored the fundamental concepts of formal languages, regular grammars, and finite automata, focusing on their practical implementation. We successfully implemented a Grammar class to represent a formal grammar, enabling the generation of valid strings according to specified production rules. The conversion of this grammar into a finite automaton (FA) was achieved by treating non-terminal symbols as states, defining transitions based on production rules, and incorporating terminal symbols into the FA's alphabet.
Through this process, we demonstrated the ability to generate strings that belong to a language defined by the grammar and verified their validity by simulating FA state transitions. The implementation highlighted the relationship between formal grammars and finite automata, reinforcing the theoretical principles covered in the course. Ultimately, this lab provided a hands-on experience with constructing and manipulating formal languages and finite automata, which are essential topics in computational theory.

## References

1. Hopcroft E. and others. Introduction to Automata Theory, Languages and Computation
2. LFPC Guide (ELSE)
3. Introduction to formal languages (ELSE)