package com.zeotap.backend.AST;

import java.util.*;
import java.util.stream.Collectors;


public class OptimizedRuleCombiner {
    
    static class Condition {
        String attribute;
        String operator;
        String value;
        
        public Condition(String condition) {
            String[] parts = condition.split(" ");
            this.attribute = parts[0];
            this.operator = parts[1];
            this.value = parts[2];
        }
        
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Condition)) return false;
            Condition other = (Condition) o;
            return attribute.equals(other.attribute) &&
                operator.equals(other.operator) &&
                value.equals(other.value);
        }
        
        @Override
        public String toString() {
            return attribute + " " + operator + " " + value;
        }
    }
    
    private static Set<String> findConditions(Node node) {
        Set<String> conditions = new HashSet<>();
        if (node == null) return conditions;
        
        if (node.type.equals("operand")) {
            conditions.add(node.value);
            return conditions;
        }
        
        conditions.addAll(findConditions(node.left));
        conditions.addAll(findConditions(node.right));
        return conditions;
    }
    
    private static Map<String, List<Condition>> groupConditionsByAttribute(Set<String> conditions) {
        Map<String, List<Condition>> grouped = new HashMap<>();
        
        for (String condStr : conditions) {
            Condition cond = new Condition(condStr);
            grouped.computeIfAbsent(cond.attribute, k -> new ArrayList<>()).add(cond);
        }
        
        return grouped;
    }
    
    private static Set<String> findCommonConditions(Node ast1, Node ast2) {
        Set<String> conditions1 = findConditions(ast1);
        Set<String> conditions2 = findConditions(ast2);
        
        Set<String> common = new HashSet<>(conditions1);
        common.retainAll(conditions2);
        return common;
    }
    
    public static Node combineRulesOptimized(Node ast1, Node ast2) {
        Set<String> commonConds = findCommonConditions(ast1, ast2);
        
        if (commonConds.isEmpty()) {
            return new Node("operator", "OR", ast1, ast2);
        }
        
        Node transformed1 = removeConditions(ast1, commonConds);
        Node transformed2 = removeConditions(ast2, commonConds);
        
        Node commonNode = createCommonConditionNode(commonConds);
        
        Node orNode = new Node("operator", "OR", transformed1, transformed2);
        
        return new Node("operator", "AND", commonNode, orNode);
    }
    
    private static Node removeConditions(Node node, Set<String> conditions) {
        if (node == null) return null;
        
        if (node.type.equals("operand")) {
            return conditions.contains(node.value) ? null : node;
        }
        
        Node leftTransformed = removeConditions(node.left, conditions);
        Node rightTransformed = removeConditions(node.right, conditions);
        
        if (leftTransformed == null && rightTransformed == null) {
            return null;
        }
        if (leftTransformed == null) return rightTransformed;
        if (rightTransformed == null) return leftTransformed;
        
        return new Node("operator", node.value, leftTransformed, rightTransformed);
    }
    
    private static Node createCommonConditionNode(Set<String> conditions) {
        if (conditions.isEmpty()) return null;
        
        Iterator<String> it = conditions.iterator();
        Node result = new Node("operand", it.next());
        
        while (it.hasNext()) {
            Node nextNode = new Node("operand", it.next());
            result = new Node("operator", "AND", result, nextNode);
        }
        
        return result;
    }
    
    public static Node combineMultipleRules(List<String> rules) {
        if (rules == null || rules.isEmpty()) return null;
        if (rules.size() == 1) return AstMain.create_rule(rules.get(0));
        
        List<Node> asts = rules.stream()
            .map(AstMain::create_rule)
            .collect(Collectors.toList());
        
        Node result = asts.get(0);
        
        for (int i = 1; i < asts.size(); i++) {
            result = combineRulesOptimized(result, asts.get(i));
        }
        
        return result;
    }
}