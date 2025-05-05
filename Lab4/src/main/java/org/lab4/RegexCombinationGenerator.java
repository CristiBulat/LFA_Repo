package org.lab4;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class RegexCombinationGenerator {
    public static void main(String[] args) {
        String regex1 = "M?N^2(O|P)^3Q*R^+";
        String regex2 = "(X|Y)^3(8|9)^+";
        String regex3 = "(H|I)(J)L^+N?";

        RegexProcessor processor = new RegexProcessor();

        processor.showProcessingSequence(regex1);
        System.out.println("\nGenerating combinations for Regex 1: ");
        List<String> combinations1 = processor.generateCombinations(regex1);
        combinations1.forEach(System.out::println);

        processor.showProcessingSequence(regex2);
        System.out.println("\nGenerating combinations for Regex 2:");
        List<String> combinations2 = processor.generateCombinations(regex2);
        combinations2.forEach(System.out::println);

        processor.showProcessingSequence(regex3);
        System.out.println("\nGenerating combinations for Regex 3:");
        List<String> combinations3 = processor.generateCombinations(regex3);
        combinations3.forEach(System.out::println);
    }
}