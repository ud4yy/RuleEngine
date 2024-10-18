package com.zeotap.backend.datastructure;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleTokenizer {

    public static List<String> tokenizeRule(String rule) {
        List<String> tokens = new ArrayList<>();
        
        String[] patterns = {
            "\\(|\\)",  // Brackets
            "AND|OR",   // Logical operators
            "[\\w\\.]+ *[<>=!]+ *('[^']*'|[\\w\\.]+)",  // Conditions (e.g., "age > 30", "department = 'Sales'", "status != 'Inactive'")
            "\\S+"      // Any non-space character sequence (catch-all)
        };
        
        // Combine all patterns
        String combinedPattern = String.join("|", patterns);
        Pattern pattern = Pattern.compile(combinedPattern);
        Matcher matcher = pattern.matcher(rule);
        
        // Find all tokens
        while (matcher.find()) {
            String token = matcher.group().trim();
            // Remove single quotes from strings within conditions
            token = token.replaceAll("'([^']*)'", "$1");
            tokens.add(token);
        }
        
        return tokens;
    }

    public static boolean validateTokens(List<String> tokens) {
        Stack<String> parenthesesStack = new Stack<>();
        String previousToken = "";
        
        for (String token : tokens) {
            // Check for balanced parentheses
            if (token.equals("(")) {
                parenthesesStack.push(token);
            } else if (token.equals(")")) {
                if (parenthesesStack.isEmpty()) {
                    return false; 
                }
                parenthesesStack.pop();
            }

            if (isCondition(token)) {
                if (!previousToken.isEmpty() && !isOperator(previousToken) && !previousToken.equals("(")) return false;
            }
            else if (isOperator(token)) {
                if (!previousToken.isEmpty() && (isOperator(previousToken) || previousToken.equals("("))) return false;
            }
            previousToken = token;
        }

        // If parentheses are balanced, the stack should be empty
        return parenthesesStack.isEmpty();
    }

    // Helper function to check if a token is an operator (AND, OR)
    public static boolean isOperator(String token) {
        return token.equals("AND") || token.equals("OR");
    }

    // Helper function to check if a token is a condition
    public static boolean isCondition(String token) {
        return token.matches("[\\w\\.]+ *[<>=!]+ *[\\w\\.]+");
    }
}