package org.lab6;

import org.lab6.Lexer;
import org.lab6.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("SimpleScript Parser - AST Builder");
        System.out.println("-----------------------------");
        System.out.println("1. Enter code directly");
        System.out.println("2. Read code from file");
        System.out.print("Choose an option: ");

        int option = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String sourceCode = "";

        if (option == 1) {
            System.out.println("Enter code (type 'END' on a new line to finish):");
            StringBuilder codeBuilder = new StringBuilder();
            String line;

            while (!(line = scanner.nextLine()).equals("END")) {
                codeBuilder.append(line).append("\n");
            }

            sourceCode = codeBuilder.toString();
        } else if (option == 2) {
            System.out.print("Enter file path: ");
            String filePath = scanner.nextLine();

            try {
                sourceCode = new String(Files.readAllBytes(Paths.get(filePath)));
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
                return;
            }
        } else {
            System.out.println("Invalid option.");
            return;
        }

        // Create lexer and tokenize the input
        Lexer lexer = new Lexer(sourceCode);
        List<Token> tokens = lexer.tokenize();

        // Print the tokens
        System.out.println("\nTokens:");
        System.out.println("-------");
        for (Token token : tokens) {
            System.out.println(token);
        }

        // Parse tokens and build AST
        Parser parser = new Parser(tokens);
        try {
            ProgramNode ast = parser.parse();

            // Print the AST
            System.out.println("\nAbstract Syntax Tree:");
            System.out.println("--------------------");
            ASTPrinter printer = new ASTPrinter();
            System.out.println(printer.print(ast));

            System.out.println("\nParsing completed successfully!");
        } catch (Exception e) {
            System.err.println("Error during parsing: " + e.getMessage());
            e.printStackTrace();
        }

        scanner.close();
    }
}