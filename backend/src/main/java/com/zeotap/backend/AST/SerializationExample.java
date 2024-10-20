package com.zeotap.backend.AST;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.HashMap;
import java.util.Map;

public class SerializationExample {
    public static void main(String[] args) {
        // Create a rule and convert to AST
        Node rootNode = AstMain.create_rule("(age>31 AND aa = 'dept') OR (age > 30 AND department = 'Sales')");

        // Serialize the Node to Map
        Map<String, Object> nodeMap = rootNode.toMap();
        AstMain.printAST(rootNode);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(nodeMap);
        System.out.println(json);
        
        Map<String, Object> deserializedMap = gson.fromJson(json, Map.class);
        
        Node deserializedNode = Node.fromMap(deserializedMap);

        AstMain.printAST(deserializedNode);
    }
}
