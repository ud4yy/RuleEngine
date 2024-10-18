package com.zeotap.backend.datastructure;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AstMain {

    static class Node {
        String type; // "operator" for AND/OR, "operand" for conditions
        Node left;   // Left child node
        Node right;  // Right child node
        String value; // Only for operand nodes, e.g., "age > 30"
        String dtype; // Datatype of the value

        public Node(String type,String value, Node left, Node right) {
            this.type = type;
            this.left = left;
            this.right = right;
            this.value = value;
        }
    
        public Node(String type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    public static Node create_rule(String rule_string) {
        List<String> tokens = RuleTokenizer.tokenizeRule(rule_string);
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
                operatorStack.pop(); // Remove the "("
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

    public static Node combine_rules(List<String> rules) {
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
        String[] parts = condition.split(" ");
        String attribute = parts[0];
        String operator = parts[1];
        String value = parts[2];

        Object attributeValue = data.get(attribute);
        // System.out.println("this is that"+""+attributeValue.toString());
        switch (operator) {
            case ">":
                return Double.parseDouble(attributeValue.toString()) > Double.parseDouble(value);
            case "<":
                return Double.parseDouble(attributeValue.toString()) < Double.parseDouble(value);
            case "=":
            
                String regex = "\\d+";                
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(value);
                if(matcher.find()){
                    Integer num = Integer.parseInt(matcher.group());
                    return Integer.parseInt(attributeValue.toString()) == num;
                }
                else{
                    return attributeValue.toString().equals(value);

                }
            case "!=":
                String r = "\\d+";                
                Pattern p = Pattern.compile(r);
                Matcher m = p.matcher(value);
                if(m.find()){
                    Integer num = Integer.parseInt(m.group());
                    return Integer.parseInt(attributeValue.toString()) != num;
                }
                else{
                    return !attributeValue.toString().equals(value);
                }
                
            default:
                return false;
        }
    }
    
    public static void main(String[] args) {
        String rule1 = "((age > 30 AND department != 'Sales') OR (age < 25 AND department != Marketing)) AND (salary > 50000 OR experience > 5)";
        String rule2 = "((age > 30 AND department = 'Marketing')) AND (salary = 20000 OR experience > 5)";
        
        Node ast1 = create_rule(rule1);
        Node ast2 = create_rule(rule2);
        printAST(ast1);
        
        List<String> rules = Arrays.asList(rule1, rule2);
        Node combinedAST = combine_rules(rules);

        Map<String, Object> data1 = new HashMap<>();
        data1.put("age", 304);
        data1.put("department", "abc");
        data1.put("salary", 2000000);
        data1.put("experience", 3);

        System.out.println(evaluate_rule(ast1, data1));
        boolean result1 = evaluate_rule(combinedAST, data1);
        System.out.println("Result for data1: " + result1);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("age", 41);
        data2.put("department", "Marketing");
        data2.put("salary", 45000);
        data2.put("experience", 2);

        boolean result2 = evaluate_rule(combinedAST, data2);
        System.out.println("Result for data2: " + result2);
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
}