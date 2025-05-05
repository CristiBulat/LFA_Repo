# Topic: Regular Expression Combination Generator

### **Course:** Formal Languages & Finite Automata
### **Author:** Bulat Cristian

## Variant
![Variant 2](variant_2_task.png)

## Theory

### Regular Expressions and Dynamic String Generation

**Regular expressions** are powerful pattern-matching tools used to describe complex string search and generation criteria. They provide a concise way to define string patterns using special characters and operators that represent different matching rules.

### Key Concepts

- **Regex Operators:**
    - `?`: Optional character
    - `^n`: Exact repetition of a character
    - `*`: Zero or more repetitions
    - `+`: One or more repetitions
    - `|`: Alternation (multiple options)
    - `()`: Grouping characters

### Relation to Formal Languages

Regular expressions are directly connected to finite automata and regular languages. They provide a declarative way to define pattern recognition and generation rules that can be translated into state machines.

## Objectives

1. Implement a dynamic regular expression combination generator
2. Create a flexible system for parsing complex regex patterns
3. Demonstrate understanding of regex pattern processing
4. Develop a solution that can handle multiple regex operators
5. Implement a method to show the processing sequence of regular expressions

## Implementation Description

### Regex Processor Design

The `RegexProcessor` class implements a comprehensive approach to regex pattern processing. The design uses recursive parsing with regex matchers to handle complex patterns involving multiple operators. The implementation supports key regex features:

- Optional character insertion
- Character repetition with specified count
- Alternation within character groups
- Kleene star and plus operators

```java
private void processDynamicRegex(String regex, String currentString, List<String> combinations) {
    // Recursive regex processing logic
    // Handles different regex components step by step
}
```

### Token Generation Strategy

The generator creates valid string combinations by systematically processing regex components:
1. Identify and resolve grouped expressions
2. Handle optional characters
3. Process character repetitions
4. Resolve Kleene star and plus operators
5. Generate final string combinations

### Combination Limit Implementation

To prevent excessive computational complexity, the implementation limits repetitions:
- Optional repetitions: 0-5 times
- Kleene star: 0-5 repetitions
- Kleene plus: 1-5 repetitions

### Processing Sequence Visualization

A bonus feature provides a detailed explanation of regex processing steps, enhancing understanding of the internal mechanism.

```java
public void showProcessingSequence(String regex) {
    // Detailed step-by-step explanation of regex processing
}
```

## Results

### Regex Processing Capabilities

1. **Pattern Recognition:**
    - Support for complex regex patterns
    - Handling of multiple regex operators
    - Systematic combination generation

2. **Combination Generation:**
    - Generates valid string combinations
    - Respects regex pattern constraints
    - Limits computational complexity

### Sample Output

For regex `M?N^2(O|P)^3Q*R+`:
```
Combinations generated:
NOOQR
NOOOPQQR
MNPPPQQQR
...
```

For regex `(X|Y)^3(8|9)+`:
```
Combinations generated:
XXX8
YYY99
XXX888
...
```

## Challenges and Solutions

1. **Complex Regex Parsing:**
    - Challenge: Handling multiple regex operators
    - Solution: Recursive processing with regex matchers

2. **Computational Complexity:**
    - Challenge: Preventing infinite combination generation
    - Solution: Implementing strict repetition limits

3. **Flexible Pattern Processing:**
    - Challenge: Supporting various regex patterns
    - Solution: Modular design with step-by-step processing

## Conclusion

The laboratory work successfully demonstrated:
- Dynamic regular expression combination generation
- Comprehensive regex pattern processing
- Systematic approach to string generation
- Visualization of regex processing steps

Key achievements:
1. Implemented a flexible regex combination generator
2. Developed a modular and extensible solution
3. Provided insights into regex pattern processing mechanisms

The project bridges theoretical concepts of regular expressions with practical implementation, showcasing how complex string patterns can be systematically processed and generated.

## Theoretical Connections

The implementation directly relates to:
- Finite automata theory
- Regular language characteristics
- Pattern matching algorithms

## References

1. Hopcroft, J. E., Motwani, R., & Ullman, J. D. (2006). Introduction to Automata Theory, Languages, and Computation.
2. Aho, A. V., Lam, M. S., Sethi, R., & Ullman, J. D. (2006). Compilers: Principles, Techniques, and Tools.
3. Formal Languages & Finite Automata course materials