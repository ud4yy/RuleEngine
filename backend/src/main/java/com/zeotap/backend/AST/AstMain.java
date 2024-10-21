package com.zeotap.backend.AST;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AstMain {

    public static Node create_rule(String rule_string) {
        List<String> tokens = RuleTokenizer.tokenizeRule(rule_string);
        
        // Validate tokens before building the AST
        if (!validateTokens(tokens)) {
            System.err.println("Invalid rule: " + rule_string);
            return null; // Return null for invalid rules
        }

        return buildAST(tokens);
    }

    private static boolean validateTokens(List<String> tokens) {
        // Basic validation to check if parentheses are balanced
        int balance = 0;
        for (String token : tokens) {
            if (token.equals("(")) {
                balance++;
            } else if (token.equals(")")) {
                balance--;
            }
            if (balance < 0) return false; // More closing than opening
        }
        return balance == 0; // Check if all opened parentheses are closed
    }

    private static Node buildAST(List<String> tokens) {
        Stack<Node> nodeStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();

        for (String token : tokens) {
            if (token.equals("(")) {
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    if (nodeStack.size() < 2) {
                        System.err.println("Invalid expression during parsing.");
                        return null; // Return null for invalid expression
                    }
                    Node right = nodeStack.pop();
                    Node left = nodeStack.pop();
                    String operator = operatorStack.pop();
                    nodeStack.push(new Node("operator", operator, left, right));
                }
                if (!operatorStack.isEmpty()) {
                    operatorStack.pop(); // Remove the "("
                }
            } else if (token.equals("AND") || token.equals("OR")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    if (nodeStack.size() < 2) {
                        System.err.println("Invalid expression during parsing.");
                        return null; // Return null for invalid expression
                    }
                    Node right = nodeStack.pop();
                    Node left = nodeStack.pop();
                    String operator = operatorStack.pop();
                    nodeStack.push(new Node("operator", operator, left, right));
                }
                operatorStack.push(token);
            } else {
                nodeStack.push(new Node("operand", token));
            }
        }

        while (!operatorStack.isEmpty()) {
            if (nodeStack.size() < 2) {
                System.err.println("Invalid expression during final assembly.");
                return null; // Return null for invalid expression
            }
            Node right = nodeStack.pop();
            Node left = nodeStack.pop();
            String operator = operatorStack.pop();
            nodeStack.push(new Node("operator", operator, left, right));
        }

        // Check if we have a valid AST root
        if (nodeStack.size() != 1) {
            System.err.println("Invalid expression: could not build complete AST.");
            return null; // Return null if the AST is not complete
        }

        return nodeStack.pop();
    }

    public static boolean evaluate_rule(Node root, Map<String, Object> data) {
        if (root == null) return false; // Return false for invalid AST

        if (root.type.equals("operand")) {
            return evaluateCondition(root.value, data);
        }

        if (root.value.equals("AND")) {
            return evaluate_rule(root.left, data) && evaluate_rule(root.right, data);
        }

        if (root.value.equals("OR")) {
            return evaluate_rule(root.left, data) || evaluate_rule(root.right, data);
        }

        return false;
    }

    private static boolean evaluateCondition(String condition, Map<String, Object> data) {
        List<String> parts = new ArrayList<>();
        Pattern pattern = Pattern.compile("[^\\s']+|'([^']*)'");
        Matcher matcher = pattern.matcher(condition);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                parts.add(matcher.group(1).replaceAll("\\s+", ""));
            } else {
                parts.add(matcher.group());
            }
        }

        if (parts.size() < 3) {
            return false; // Not enough parts for a valid condition
        }

        String attribute = parts.get(0);
        String operator = parts.get(1);
        String value = parts.size() > 2 ? parts.get(2) : "";

        Object attributeValue = data.get(attribute);
        if (attributeValue == null) {
            return false;
        }

        String normalizedAttributeValue = attributeValue.toString().replaceAll("\\s+", "");
        String normalizedValue = value.replaceAll("\\s+", "");
        normalizedAttributeValue = normalizedAttributeValue.toLowerCase();
        normalizedValue = normalizedValue.toLowerCase();

        switch (operator) {
            case ">":
                return Double.parseDouble(normalizedAttributeValue) > Double.parseDouble(normalizedValue);
            case "<":
                return Double.parseDouble(normalizedAttributeValue) < Double.parseDouble(normalizedValue);
            case "=":
                if (normalizedValue.matches("\\d+")) {
                    return Integer.parseInt(normalizedAttributeValue) == Integer.parseInt(normalizedValue);
                }
                return normalizedAttributeValue.equals(normalizedValue);
            case "!=":
                if (normalizedValue.matches("\\d+")) {
                    return Integer.parseInt(normalizedAttributeValue) != Integer.parseInt(normalizedValue);
                }
                return !normalizedAttributeValue.equals(normalizedValue);
            default:
                return false;
        }
    }

    public static String astToString(Node root) {
        if (root == null) {
            return "";
        }

        if (root.type.equals("operand")) {
            return root.value;
        }

        String leftExpr = astToString(root.left);
        String rightExpr = astToString(root.right);

        StringBuilder sb = new StringBuilder();
        if (root.type.equals("operator")) {
            sb.append("(");
            sb.append(leftExpr).append(" ").append(root.value).append(" ").append(rightExpr);
            sb.append(")");
        }

        return sb.toString();
    }

    static public void printAST(Node ast) {
        Node temp = ast;

        Queue<Node> q = new LinkedList<Node>();
        q.offer(temp);
        int i = 0;

        while (!q.isEmpty()) {
            Node n = q.poll();

            System.err.println(i + "th" + "   " + n.value + " " + n.type);
            i++;
            if (n.left != null) q.offer(n.left);
            if (n.right != null) q.offer(n.right);
        }
    }

    public static void main(String[] args) {
        // Your existing rules
        String rule1 = "(age > 30 AND department = 'Sales Abc Def')";
        String rule2 = "(age > 30 AND department = 'Marketing')";
        String rule3 = "(age > 21 AND department = 'Home')";
        String invalidRule = "(age > 30 AND department = 'Sales Abc Def'"; // Example of an invalid rule

        List<String> rules = Arrays.asList(rule1, rule2, rule3, invalidRule);
        Node r1 = create_rule(rule1);
        
        // Print AST for valid rule
        if (r1 != null) {
            printAST(r1);
        }

        Node optimizedCombined = OptimizedRuleCombiner.combineMultipleRules(rules);
        
        System.out.println("\nOptimized Combination:");
        if (optimizedCombined != null) {
            System.out.println(astToString(optimizedCombined));
        } else {
            System.out.println("Optimized combination could not be created due to invalid rules.");
        }
        
        // Test evaluation
        Map<String, Object> testData = new HashMap<>();
        testData.put("age", 232);
        testData.put("department", "Sales Abc Def");

        System.out.println("\nEvaluation Results:");
        System.out.println("Rule 1: " + evaluate_rule(r1, testData));
        System.out.println("Optimized: " + evaluate_rule(optimizedCombined, testData));
    }
}
