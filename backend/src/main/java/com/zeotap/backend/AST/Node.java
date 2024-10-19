package com.zeotap.backend.AST;

public class Node {
    String type; // "operator" for AND/OR, "operand" for conditions
    Node left;   // Left child node
    Node right;  // Right child node
    String value; // Only for operand nodes, e.g., "age > 30"
    String dtype; 

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