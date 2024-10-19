package com.zeotap.backend.AST;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AstMain {

    public static Node create_rule(String rule_string) {
        List<String> tokens = RuleTokenizer.tokenizeRule(rule_string);

        for(String token : tokens) {
            System.out.println(token);
        }
        return buildAST(tokens);
    }

    private static Node buildAST(List<String> tokens) {
        Stack<Node> nodeStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();

        for (String token : tokens) {
            if (token.equals("(")) {
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.peek().equals("(")) {
                    Node right = nodeStack.pop();
                    Node left = nodeStack.pop();
                    String operator = operatorStack.pop();
                    nodeStack.push(new Node("operator",operator, left, right));
                }
                if (!operatorStack.isEmpty()) {
                    operatorStack.pop(); // Remove the "("
                }
            } else if (token.equals("AND") || token.equals("OR")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    Node right = nodeStack.pop();
                    Node left = nodeStack.pop();
                    String operator = operatorStack.pop();
                    nodeStack.push(new Node("operator",operator, left, right));
                }
                operatorStack.push(token);
            } else {
                nodeStack.push(new Node("operand", token));
            }
        }

        while (!operatorStack.isEmpty()) {
            Node right = nodeStack.pop();
            Node left = nodeStack.pop();
            String operator = operatorStack.pop();
            nodeStack.push(new Node("operator",operator, left, right));
        }

        return nodeStack.pop();
    }

    public static Node combine_rules_Chaining(List<String> rules) {
        if (rules.isEmpty()) return null;
        if (rules.size() == 1) return create_rule(rules.get(0));

        Node combinedRoot = new Node("operator","OR", null, null);
        Node current = combinedRoot;

        for (int i = 0; i < rules.size(); i++) {
            Node ruleNode = create_rule(rules.get(i));
            current.left = ruleNode;
            if (i < rules.size() - 1) {
                current.right = new Node("operator","OR", null, null);
                current = current.right;
            }
        }

        return combinedRoot;
    }
    
    public static boolean evaluate_rule(Node root, Map<String, Object> data) {
        if (root == null) return false;

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
                // This is a quoted string - add it without spaces and quotes
                parts.add(matcher.group(1).replaceAll("\\s+", ""));
            } else {
                // This is a non-quoted part
                parts.add(matcher.group());
            }
        }

        if (parts.size() < 3) {
            return false;
        }

        String attribute = parts.get(0);
        String operator = parts.get(1);
        String value = parts.size() > 2 ? parts.get(2) : "";

        Object attributeValue = data.get(attribute);
        if (attributeValue == null) {
            return false;
        }

        // Remove spaces from the attribute value for comparison
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
    
        // If the node is an operand, return its value
        if (root.type.equals("operand")) {
            return root.value;
        }
    
        // For operator nodes, recursively get the left and right expressions
        String leftExpr = astToString(root.left);
        String rightExpr = astToString(root.right);
    
        // Add parentheses for clarity in complex expressions
        StringBuilder sb = new StringBuilder();
        if (root.type.equals("operator")) {
            sb.append("(");
            sb.append(leftExpr).append(" ").append(root.value).append(" ").append(rightExpr);
            sb.append(")");
        }
    
        return sb.toString();
    }
    

   static public void printAST(Node ast){
        Node  temp = ast;

        Queue<Node> q =  new LinkedList<Node>();
        q.offer(temp);
        int i = 0;

        while(!q.isEmpty()){
            Node n =  q.poll();
            
            System.err.println(i+"th"+"   "+n.value+" "+n.type);
            i++;
            if(n.left !=null) q.offer(n.left);
            if(n.right!=null) q.offer(n.right);
        }

    }
    public static void main(String[] args) {
        // Your existing rules
        String rule1 = "(age > 30 AND department = 'Sales Abc Def')";
        String rule2 = "(age > 30 AND department = 'Marketing')";
        String rule3 = "(age > 21 AND department = 'Home')";

        List<String> rules = Arrays.asList(rule1, rule2, rule3);
        Node r1 = create_rule(rule1);
                // Try both approaches\
        printAST(r1);
        Node optimizedCombined = OptimizedRuleCombiner.combineMultipleRules(rules);
        
        System.out.println("\nOptimized Combination:");
        System.out.println(astToString(optimizedCombined));
        
        // Test evaluation
        Map<String, Object> testData = new HashMap<>();
        testData.put("age", 232);
        testData.put("department", "Sales Abc Def");

        System.out.println("\nEvaluation Results:");
        System.out.println("Optimized: " + evaluate_rule(r1, testData));
        System.out.println("\nEvaluation Results:");
        System.out.println("Optimized: " + evaluate_rule(optimizedCombined, testData));
    }
}