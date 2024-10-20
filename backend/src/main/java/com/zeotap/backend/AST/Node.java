package com.zeotap.backend.AST;

import java.util.HashMap;
import java.util.Map;

public class Node {
    String type; // "operator" for AND/OR, "operand" for conditions
    Node left;   // Left child node
    Node right;  // Right child node
    String value; // Only for operand nodes, e.g., "age > 30"

    public Node(String type, String value, Node left, Node right) {
        this.type = type;
        this.left = left;
        this.right = right;
        this.value = value;
    }

    public Node(String type, String value) {
        this.type = type;
        this.value = value;
    }

    // Method to convert AST node to a Map for MongoDB storage
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("value", value);
        if (left != null) {
            map.put("left", left.toMap());
        }
        if (right != null) {
            map.put("right", right.toMap());
        }
        return map;
    }
    
    // Static method to deserialize the map back to AST
    public static Node fromMap(Map<String, Object> map) {
        String type = (String) map.get("type");
        String value = (String) map.get("value");
        
        Node left = map.containsKey("left") ? fromMap((Map<String, Object>) map.get("left")) : null;
        Node right = map.containsKey("right") ? fromMap((Map<String, Object>) map.get("right")) : null;
        
        return new Node(type, value, left, right);
    }
}
