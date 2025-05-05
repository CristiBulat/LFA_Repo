# Topic: Lexer & Scanner

### **Course:** Formal Languages & Finite Automata
### **Author:** Bulat Cristian

## Theory

### Lexical Analysis

**Lexical analysis** is the process of converting a sequence of characters into a sequence of tokens. This is one of the first stages in the compilation or interpretation of programming languages. The component that performs lexical analysis is called a **lexer** (also known as a **scanner** or **tokenizer**).

### Tokens and Lexemes

- **Lexeme:** The actual string of characters from the source code that matches a pattern.
- **Token:** A categorized lexeme, consisting of a token type and optional metadata.

For example, in the statement `int count = 10;`:
- Lexemes: `int`, `count`, `=`, `10`, `;`
- Tokens: `KEYWORD(int)`, `IDENTIFIER(count)`, `ASSIGN(=)`, `INTEGER(10)`, `SEMICOLON(;)`

### Lexer Components

A lexer typically consists of:

1. **Input Buffer:** Reads and manages the source code characters.
2. **Pattern Matching:** Rules to identify tokens from character sequences.
3. **Token Generator:** Creates token objects from recognized patterns.
4. **Error Handling:** Detects and reports lexical errors.

### Relationship to Formal Languages

Lexers implement finite automata that recognize regular languages. Each token type corresponds to a regular expression, and the entire lexical structure of a language can be described using a collection of these regular expressions.

## Objectives

1. Understand the concept of lexical analysis in language processing.
2. Implement a lexer for a simple programming language.
3. Design a token structure that captures both type and value information.
4. Handle various token types including keywords, identifiers, literals, and operators.
5. Implement proper error handling and position tracking for better error reporting.

## Implementation Description

### Token Class Design

The Token class implements a comprehensive representation of lexical tokens with metadata for error reporting. The design uses an enumeration to define token types, categorizing them into logical groups (keywords, literals, operators, and delimiters). Each token instance stores not only its type and lexical value, but also precise source location information (line and column numbers). This location tracking enables detailed error messages that help pinpoint issues in the source code. The class includes standard accessors for all properties and overrides toString() to provide readable token representations for debugging and display purposes.

```java
public enum Type {
    // Keywords
    IF, ELSE, WHILE, FUNCTION, RETURN,
    
    // Literals
    INTEGER, FLOAT, STRING, IDENTIFIER,
    
    // Operators, Delimiters, Special tokens
    // ...
}
```

### Lexer Implementation

The Lexer class implements a character-by-character scanning approach with lookahead capability for multi-character token recognition. The implementation maintains position tracking through line and column counters that are carefully updated during character processing. For efficiency, the lexer employs a HashMap to quickly identify keywords from identifiers without repeated string comparisons. The design handles complex patterns like string literals with escape sequences and both single-line and multi-line comments. The scanner includes specialized methods for different token categories (numbers, identifiers, strings) that encapsulate pattern-specific logic. This modular approach makes the lexer both maintainable and extensible for future language enhancements.

```java
private Token number() {
    // Process numeric literals (integers and floats)
    boolean isFloat = false;
    // ...
    return new Token(type, value.toString(), line, startColumn);
}
```

### Whitespace and Comment Handling

The lexer implements specialized methods for processing non-token elements that affect source code structure. The skipWhitespace() method efficiently bypasses spaces, tabs, and newlines while properly updating line and column counters to maintain accurate position information. The skipComments() method handles both single-line comments (`//`) and multi-line comments (`/* */`) through different processing paths. For multi-line comments, it employs a state machine approach that scans until finding the closing delimiter. These utility methods improve code organization by separating the handling of structural elements from token recognition logic, creating a cleaner overall design while ensuring all source code is fully processed.

```java
private void skipComments() {
    // Handle single-line and multi-line comments
    // ...
}
```

### Token Stream Generation

The tokenize() method implements a comprehensive approach to transform source code into a structured token stream. It iteratively calls getNextToken() until reaching the end of input, accumulating all tokens in a list. This approach creates a complete in-memory representation of the lexical structure, enabling simpler implementation of subsequent compilation phases. The method ensures the token stream is properly terminated with an EOF token, a critical element for parser implementation. By maintaining the entire token sequence, this design allows for easier debugging and visualization of the lexical analysis results, while also facilitating integration with parsing algorithms that may need lookahead capabilities beyond what a streaming approach would provide.

```java
public List<Token> tokenize() {
    List<Token> tokens = new ArrayList<>();
    // Process all tokens
    // ...
    return tokens;
}
```

## Results

The implementation produced the following results:

1. **Token Definition:**
    - A comprehensive set of token types covering keywords, literals, operators, and delimiters
    - Support for tracking source position information (line and column)
    - Proper string representation for debugging and display

2. **Lexer Capabilities:**
    - Recognition of all language elements in SimpleScript
    - Proper handling of whitespace and comments
    - Support for string literals with escape sequences
    - Numeric literal recognition for both integers and floating-point numbers
    - Keyword identification and separation from regular identifiers

3. **Sample Output for Test Code:**

   Input:
   ```
   function gcd(a, b) {
       // Compute greatest common divisor
       if (b == 0) {
           return a;
       }
       return gcd(b, a % b);
   }
   ```

   Output:
   ```
   Token(FUNCTION, 'function', line: 1, col: 1)
   Token(IDENTIFIER, 'gcd', line: 1, col: 10)
   Token(LPAREN, '(', line: 1, col: 13)
   Token(IDENTIFIER, 'a', line: 1, col: 14)
   Token(COMMA, ',', line: 1, col: 15)
   Token(IDENTIFIER, 'b', line: 1, col: 17)
   Token(RPAREN, ')', line: 1, col: 18)
   Token(LBRACE, '{', line: 1, col: 20)
   Token(IF, 'if', line: 3, col: 5)
   Token(LPAREN, '(', line: 3, col: 8)
   Token(IDENTIFIER, 'b', line: 3, col: 9)
   Token(EQUALS, '==', line: 3, col: 11)
   Token(INTEGER, '0', line: 3, col: 14)
   Token(RPAREN, ')', line: 3, col: 15)
   Token(LBRACE, '{', line: 3, col: 17)
   Token(RETURN, 'return', line: 4, col: 9)
   Token(IDENTIFIER, 'a', line: 4, col: 16)
   Token(SEMICOLON, ';', line: 4, col: 17)
   Token(RBRACE, '}', line: 5, col: 5)
   Token(RETURN, 'return', line: 6, col: 5)
   Token(IDENTIFIER, 'gcd', line: 6, col: 12)
   Token(LPAREN, '(', line: 6, col: 15)
   Token(IDENTIFIER, 'b', line: 6, col: 16)
   Token(COMMA, ',', line: 6, col: 17)
   Token(IDENTIFIER, 'a', line: 6, col: 19)
   Token(MODULO, '%', line: 6, col: 21)
   Token(IDENTIFIER, 'b', line: 6, col: 23)
   Token(RPAREN, ')', line: 6, col: 24)
   Token(SEMICOLON, ';', line: 6, col: 25)
   Token(RBRACE, '}', line: 7, col: 1)
   Token(EOF, '', line: 7, col: 2)
   ```

## Conclusion

In this laboratory work, I successfully implemented a lexer for the SimpleScript language. The key achievements were:

1. Designing a robust token structure that captures type, value, and position information.
2. Implementing a lexer that recognizes various token categories including keywords, identifiers, operators, and literals.
3. Adding support for comments, both single-line and multi-line.
4. Managing string literals with escape sequences.
5. Implementing proper error handling and position tracking.

The results confirmed that:
- The lexer accurately tokenizes source code according to the language specification.
- The position tracking provides valuable information for error reporting.
- The modular design enables clean separation of concerns for different token types.

This implementation demonstrates the fundamental concepts of lexical analysis in language processing. The lexer serves as the foundation for subsequent compilation or interpretation stages, providing a structured token stream that can be processed by a parser to build an abstract syntax tree.

The project also highlights the connection between theoretical concepts (regular expressions and finite automata) and practical language implementation aspects. Each token pattern effectively represents a small finite automaton, and the lexer as a whole implements a collection of these automata to recognize the lexical structure of the language.

## References

1. Aho, A. V., Lam, M. S., Sethi, R., & Ullman, J. D. (2006). Compilers: Principles, Techniques, and Tools (2nd Edition).
2. Cooper, K., & Torczon, L. (2011). Engineering a Compiler (2nd Edition).
3. Formal Languages & Finite Automata course materials.