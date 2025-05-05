# Topic: Parser & Building an Abstract Syntax Tree

### **Course:** Formal Languages & Finite Automata
### **Author:** Bulat Cristian

## Theory

### Parsing

**Parsing** is the process of analyzing a string of symbols according to a formal grammar. In programming language processing, parsing is typically the second phase that follows lexical analysis (tokenization). The parser takes the stream of tokens produced by the lexer and determines if they conform to the syntactic rules defined by the language grammar.

The main goals of parsing include:
- Validating that the input adheres to the language's grammar rules
- Creating a structured representation of the input (often as a tree)
- Detecting and reporting syntax errors
- Providing a foundation for subsequent compilation or interpretation phases

Parsing techniques generally fall into two categories:
- **Top-down parsing**: Starts with the grammar's start symbol and attempts to derive the input (e.g., recursive descent, LL parsing)
- **Bottom-up parsing**: Starts with the input and attempts to reduce it to the start symbol (e.g., LR parsing, shift-reduce parsing)

### Abstract Syntax Tree (AST)

An **Abstract Syntax Tree (AST)** is a tree representation of the abstract syntactic structure of source code. Unlike a parse tree (or concrete syntax tree) which captures every detail of the input according to the grammar, an AST abstracts away certain syntactic details to focus on the semantically important aspects of the code.

Key characteristics of ASTs include:

1. **Hierarchical Structure**: ASTs organize code into a tree where nodes represent constructs and operations.
2. **Semantic Representation**: ASTs capture the meaning of the code rather than just its syntactic structure.
3. **Abstraction**: ASTs omit certain syntactic details like parentheses, semicolons, and other delimiters.
4. **Language Independence**: While the specific node types may vary, the concept of ASTs is universal across programming languages.

ASTs serve as an intermediate representation in compilers and interpreters, forming a bridge between the syntactic and semantic analysis phases. They provide a convenient structure for code analysis, transformation, and optimization.

## Objectives

1. Get familiar with parsing concepts and implementation techniques.
2. Understand the structure and purpose of Abstract Syntax Trees.
3. Implement a parser for the SimpleScript programming language that:
    - Uses the token stream from the lexical analyzer (Lab 3)
    - Follows a formal grammar for SimpleScript
    - Constructs an AST representing the input program
4. Create appropriate AST node classes to represent various language constructs.
5. Implement a system to visualize and debug the AST.

## Implementation Description

### Token Types and Lexical Analysis

For this laboratory work, we built upon the lexical analyzer from Lab 3, which already defined a comprehensive set of token types categorized into:

- **Keywords**: IF, ELSE, WHILE, FOR, FUNCTION, RETURN
- **Literals**: INTEGER, FLOAT, STRING, IDENTIFIER
- **Operators**: PLUS, MINUS, MULTIPLY, DIVIDE, MODULO, etc.
- **Delimiters**: LPAREN, RPAREN, LBRACE, RBRACE, SEMICOLON, COMMA
- **Special tokens**: EOF, UNKNOWN

This categorization provides the necessary foundation for syntactic analysis, as the parser uses token types to determine the grammatical structure of the input.

### AST Node Structure

The AST implementation follows an object-oriented approach with a class hierarchy representing different language constructs. At the core is the `ASTNode` interface:

```java
public interface ASTNode {
    void accept(ASTVisitor visitor);
}
```

This interface utilizes the Visitor design pattern, which allows operations on the tree structure without modifying the node classes themselves. The visitor interface defines methods for handling each node type:

```java
interface ASTVisitor {
    void visit(ProgramNode node);
    void visit(FunctionDeclarationNode node);
    void visit(BlockNode node);
    // Methods for other node types...
}
```

The AST node classes can be organized into several categories:

#### 1. Program Structure Nodes

- **ProgramNode**: Represents the entire program, containing a list of top-level statements and function declarations.
- **FunctionDeclarationNode**: Represents a function definition with name, parameters, and body.
- **BlockNode**: Represents a sequence of statements enclosed in braces.

#### 2. Statement Nodes

- **VariableDeclarationNode**: Represents a variable declaration with initialization.
- **AssignmentNode**: Represents an assignment operation.
- **IfStatementNode**: Represents a conditional statement with condition, then-branch, and optional else-branch.
- **WhileStatementNode**: Represents a while loop with condition and body.
- **ForStatementNode**: Represents a for loop with initializer, condition, update, and body.
- **ReturnStatementNode**: Represents a return statement with optional value.
- **ExpressionStatementNode**: Wraps an expression used as a statement.

#### 3. Expression Nodes

- **BinaryExpressionNode**: Represents operations with two operands (e.g., addition, multiplication).
- **UnaryExpressionNode**: Represents operations with one operand (e.g., negation, logical not).
- **CallExpressionNode**: Represents function calls with arguments.
- **IdentifierNode**: Represents variable references.
- **Literal Nodes**: Represent constant values (IntegerLiteralNode, FloatLiteralNode, StringLiteralNode).

Each node class contains attributes specific to its language construct and implements the `accept` method to support the visitor pattern.

### Recursive Descent Parser

The parser implements a **recursive descent** approach, which is a top-down parsing technique where the parser uses a set of recursive methods to process the input according to the grammar rules.

```java
public class Parser {
    private List<Token> tokens;
    private int current = 0;
    
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
    
    public ProgramNode parse() {
        ProgramNode program = new ProgramNode();
        
        while (!isAtEnd()) {
            program.addStatement(declaration());
        }
        
        return program;
    }
    
    // Methods for parsing different constructs...
}
```

The parser implements a hierarchy of parsing methods that mirror the grammar's structure:

```java
// Top-level declarations
private ASTNode declaration() {
    if (match(Token.Type.FUNCTION)) {
        return functionDeclaration();
    }
    
    return statement();
}

// Statement types
private ASTNode statement() {
    if (match(Token.Type.IF)) return ifStatement();
    if (match(Token.Type.WHILE)) return whileStatement();
    if (match(Token.Type.FOR)) return forStatement();
    if (match(Token.Type.RETURN)) return returnStatement();
    if (match(Token.Type.LBRACE)) return block();
    
    return expressionStatement();
}
```

#### Expression Parsing and Operator Precedence

A critical aspect of parsing is handling expressions with correct operator precedence. The parser implements this using a series of mutually recursive methods, each responsible for a specific precedence level:

```java
private ASTNode expression() {
    return assignment();
}

private ASTNode assignment() {
    ASTNode expr = or();
    
    if (match(Token.Type.ASSIGN)) {
        ASTNode value = assignment();
        
        if (expr instanceof IdentifierNode) {
            return new AssignmentNode((IdentifierNode)expr, value);
        }
        
        error(previous(), "Invalid assignment target.");
    }
    
    return expr;
}

// Methods for other precedence levels: or(), and(), equality(), comparison(), term(), factor(), unary(), call(), primary()
```

This approach, known as the **Pratt parsing** technique, ensures that expressions are parsed according to their correct precedence and associativity rules.

### Error Handling

The parser includes robust error handling capabilities to detect and report syntax errors:

```java
private Token consume(Token.Type type, String message) {
    if (check(type)) return advance();
    throw error(peek(), message);
}

private ParseError error(Token token, String message) {
    String errorMsg = String.format("[line %d, col %d] Error at '%s': %s", 
            token.getLine(), token.getColumn(), token.getValue(), message);
    System.err.println(errorMsg);
    return new ParseError();
}
```

The parser also implements **panic mode recovery**, which allows it to recover from errors and continue parsing:

```java
private void synchronize() {
    advance();
    
    while (!isAtEnd()) {
        if (previous().getType() == Token.Type.SEMICOLON) return;
        
        switch (peek().getType()) {
            case FUNCTION:
            case IF:
            case WHILE:
            case FOR:
            case RETURN:
                return;
        }
        
        advance();
    }
}
```

This recovery mechanism helps the parser report multiple errors in a single pass rather than stopping at the first error encountered.

### AST Visualization

To aid in debugging and understanding the parsed structure, an `ASTPrinter` class was implemented using the Visitor pattern:

```java
public class ASTPrinter implements ASTVisitor {
    private StringBuilder builder = new StringBuilder();
    private int indentLevel = 0;
    
    public String print(ASTNode node) {
        builder = new StringBuilder();
        indentLevel = 0;
        node.accept(this);
        return builder.toString();
    }
    
    // Implementation of visit methods for each node type...
}
```

The AST printer traverses the syntax tree and produces a readable text representation with proper indentation, clearly showing the hierarchical structure of the program.

## Results

The implementation was tested with various SimpleScript programs to verify its correctness. Here's an example of the parsing process using a simple program:

### Sample Input

```
function factorial(n) {
    if (n <= 1) {
        return 1;
    }
    return n * factorial(n - 1);
}

result = factorial(5);
```

### Lexical Analysis Output

```
Token(FUNCTION, 'function', line: 1, col: 1)
Token(IDENTIFIER, 'factorial', line: 1, col: 10)
Token(LPAREN, '(', line: 1, col: 19)
Token(IDENTIFIER, 'n', line: 1, col: 20)
Token(RPAREN, ')', line: 1, col: 21)
Token(LBRACE, '{', line: 1, col: 23)
Token(IF, 'if', line: 2, col: 5)
Token(LPAREN, '(', line: 2, col: 8)
Token(IDENTIFIER, 'n', line: 2, col: 9)
Token(LESS_EQUALS, '<=', line: 2, col: 11)
Token(INTEGER, '1', line: 2, col: 14)
Token(RPAREN, ')', line: 2, col: 15)
Token(LBRACE, '{', line: 2, col: 17)
Token(RETURN, 'return', line: 3, col: 9)
Token(INTEGER, '1', line: 3, col: 16)
Token(SEMICOLON, ';', line: 3, col: 17)
Token(RBRACE, '}', line: 4, col: 5)
Token(RETURN, 'return', line: 5, col: 5)
Token(IDENTIFIER, 'n', line: 5, col: 12)
Token(MULTIPLY, '*', line: 5, col: 14)
Token(IDENTIFIER, 'factorial', line: 5, col: 16)
Token(LPAREN, '(', line: 5, col: 25)
Token(IDENTIFIER, 'n', line: 5, col: 26)
Token(MINUS, '-', line: 5, col: 28)
Token(INTEGER, '1', line: 5, col: 30)
Token(RPAREN, ')', line: 5, col: 31)
Token(SEMICOLON, ';', line: 5, col: 32)
Token(RBRACE, '}', line: 6, col: 1)
Token(IDENTIFIER, 'result', line: 8, col: 1)
Token(ASSIGN, '=', line: 8, col: 8)
Token(IDENTIFIER, 'factorial', line: 8, col: 10)
Token(LPAREN, '(', line: 8, col: 19)
Token(INTEGER, '5', line: 8, col: 20)
Token(RPAREN, ')', line: 8, col: 21)
Token(SEMICOLON, ';', line: 8, col: 22)
Token(EOF, '', line: 9, col: 1)
```

### AST Output

```
Program
  Function Declaration: factorial
    Parameters: n 
    Body:
      Block
        If Statement
          Condition: (n <= 1)
          Then Branch:
            Block
              Return: 1
        Return Statement: (n * Call: factorial(n - 1))
  Expression Statement: Assignment: result = Call: factorial(5)
```

This output demonstrates how the parser correctly builds a hierarchical representation of the program structure, capturing the function declaration, the if statement with its condition and body, the return statements, and the function call assignment.

## Conclusion

In this laboratory work, I successfully implemented a parser and abstract syntax tree for the SimpleScript language. This implementation completes the front-end phase of language processing, building upon the lexical analyzer from Lab 3.

Key achievements include:

1. **Comprehensive Grammar Implementation**: Developed a parser that handles a wide range of language constructs, including functions, control flow statements, and expressions with proper precedence.

2. **Robust AST Design**: Created a flexible and extensible AST structure using the Visitor pattern, which can represent all SimpleScript language features while abstracting away syntactic details.

3. **Error Handling and Recovery**: Implemented a robust error reporting and recovery system that provides helpful error messages and continues parsing after encountering errors.

4. **AST Visualization**: Developed a visualization tool that presents the AST in a readable format, aiding in debugging and understanding the parsing process.

The implementation demonstrates several important concepts in language processing:

- The relationship between formal grammars and parser implementation
- The distinction between concrete syntax (tokens) and abstract syntax (AST)
- The importance of proper operator precedence handling
- The value of design patterns (like Visitor) in language processing applications

This parser and AST system provide a solid foundation for further language processing tasks, such as semantic analysis, optimization, and code generation or interpretation. The modular design allows for easy extension and modification to support additional language features or alternative processing approaches.

## References

1. Aho, A. V., Lam, M. S., Sethi, R., & Ullman, J. D. (2006). Compilers: Principles, Techniques, and Tools (2nd Edition). Addison-Wesley.
2. Parr, T. (2010). Language Implementation Patterns: Create Your Own Domain-Specific and General Programming Languages. Pragmatic Bookshelf.
3. Appel, A. W. (2004). Modern Compiler Implementation in Java (2nd Edition). Cambridge University Press.
4. Grune, D., van Reeuwijk, K., Bal, H. E., Jacobs, C. J. H., & Langendoen, K. (2012). Modern Compiler Design (2nd Edition). Springer.
5. Formal Languages & Finite Automata course materials.