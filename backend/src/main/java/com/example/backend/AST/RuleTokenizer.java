package com.example.backend.AST;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleTokenizer {
        public static List<String> tokenizeRule(String rule) {
            List<String> tokens = new ArrayList<>();
            
            // Define regex patterns
            String[] patterns = {
                "\\(|\\)",  // Brackets
                "AND|OR",   // Logical operators
                "[\\w\\.]+ *[<>=!]+ *('[^']*'|[\\w\\.]+)",  // Conditions
                "\\S+"      // Any non-space character sequence (catch-all)
            };
            
            // Combine all patterns
            String combinedPattern = String.join("|", patterns);
            Pattern pattern = Pattern.compile(combinedPattern);
            Matcher matcher = pattern.matcher(rule);
            
            // Find all tokens
            while (matcher.find()) {
                String token = matcher.group().trim();
                
                // Handle conditions with quoted strings
                if (token.contains("'")) {
                    // Extract the quoted part
                    Pattern quotePattern = Pattern.compile("'([^']*)'");
                    Matcher quoteMatcher = quotePattern.matcher(token);
                    if (quoteMatcher.find()) {
                        String quotedPart = quoteMatcher.group(1);
                        // Remove spaces from the quoted part
                        String noSpaceQuotedPart = quotedPart.replaceAll("\\s+", "");
                        // Replace the original quoted part with the no-space version
                        token = token.replace("'" + quotedPart + "'", "'" + noSpaceQuotedPart + "'");
                    }
                }
                
                tokens.add(token);
            }
            
            return tokens;
        }
    
    
    private static List<String> splitCondition(String condition) {
        List<String> parts = new ArrayList<>();
        
        // Pattern to match: attribute, operator, and value (quoted or unquoted)
        Pattern pattern = Pattern.compile("([\\w\\.]+)\\s*([<>=!]+)\\s*(?:'([^']*)'|([\\w\\.]+))");
        Matcher matcher = pattern.matcher(condition);
        
        if (matcher.find()) {
            // Add attribute
            parts.add(matcher.group(1));
            
            // Add operator
            parts.add(matcher.group(2));
            
            // Add value (either quoted or unquoted)
            String value = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
            parts.add(value);
        }
        
        return parts;
    }

    public static boolean validateTokens(List<String> tokens) {
        if (tokens.isEmpty()) {
            return false;
        }

        Stack<String> parenthesesStack = new Stack<>();
        String previousToken = null;
        
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            
            // Check parentheses
            if (token.equals("(")) {
                parenthesesStack.push(token);
            } else if (token.equals(")")) {
                if (parenthesesStack.isEmpty()) {
                    return false; // Unmatched closing parenthesis
                }
                parenthesesStack.pop();
            }
            
            // Validate token sequence
            if (previousToken != null) {
                if (!isValidTokenSequence(previousToken, token)) {
                    return false;
                }
            }
            
            previousToken = token;
        }
        
        // Check final state
        if (!parenthesesStack.isEmpty()) {
            return false; // Unmatched opening parenthesis
        }
        
        // Check first and last tokens
        if (!isValidFirstToken(tokens.get(0)) || !isValidLastToken(tokens.get(tokens.size() - 1))) {
            return false;
        }
        
        return true;
    }

    private static boolean isValidTokenSequence(String prev, String current) {
        if (isOperator(prev) && isOperator(current)) return false;
        if (isCondition(prev) && isCondition(current)) return false;
        if (prev.equals("(") && (isOperator(current) || current.equals(")"))) return false;
        if (isOperator(prev) && current.equals(")")) return false;
        if (prev.equals(")") && isCondition(current)) return false;
        return true;
    }

    private static boolean isValidFirstToken(String token) {
        return token.equals("(") || isCondition(token);
    }

    private static boolean isValidLastToken(String token) {
        return token.equals(")") || isCondition(token);
    }

    public static boolean isOperator(String token) {
        return token.equals("AND") || token.equals("OR");
    }

    public static boolean isCondition(String token) {
        // Check if token contains an operator and either a quoted string or a non-quoted value
        return token.matches("[\\w\\.]+ *[<>=!]+ *(?:'[^']*'|[\\w\\.]+)") ||
               token.matches("[\\w\\.]+ +(?:'[^']*'|[\\w\\.]+)");
    }

    // Helper method to test the tokenizer
    public static void testTokenizer(String rule) {
        System.out.println("Testing rule: " + rule);
        List<String> tokens = tokenizeRule(rule);
        System.out.println("Tokens: " + tokens);
        System.out.println("Valid: " + validateTokens(tokens));
        System.out.println();
    }

    public static void main(String[] args) {
        // Test cases
        String[] testRules = {
            "(age > 30 AND department = 'Sales Abc')",
            "(role = 'Senior Developer' OR department = 'R&D Team')",
            "(location = 'New York City' AND experience > 5)",
            "(name = 'John Doe' AND age != 25)",
            "(department = 'Sales & Marketing' OR salary > 50000)"
        };

        for (String rule : testRules) {
            testTokenizer(rule);
        }
    }
}