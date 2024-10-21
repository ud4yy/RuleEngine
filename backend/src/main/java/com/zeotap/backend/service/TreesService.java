package com.zeotap.backend.service;

import com.zeotap.backend.AST.AstMain;
import com.zeotap.backend.AST.Node;
import com.zeotap.backend.AST.OptimizedRuleCombiner;
import com.zeotap.backend.models.Trees;
import com.zeotap.backend.repo.TreesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TreesService {

    @Autowired
    private TreesRepository treesRepository;

    public Trees createAST(String s) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Input string must not be null or empty");
        }
    
        Node n = AstMain.create_rule(s);
        if (n == null) {
            throw new RuntimeException("Failed to create AST Node from input string: " + s);
        }
    
        Map<String, Object> nodeMap = n.toMap();
        AstMain.printAST(n);
    
        String uniqueId = UUID.randomUUID().toString();
        Trees t = new Trees(uniqueId, nodeMap, s); 
        return treesRepository.save(t); 
    }
    

    public List<Trees> getAllTrees() {
        return treesRepository.findAll();
    }

    public Trees getTreeById(String id) {
        return treesRepository.findById(id).orElse(null);
    }

    public boolean evaluateTree(Trees tree, Map<String, Object> testData) {
        Node node = Node.fromMap(tree.getJsonObject());
        return AstMain.evaluate_rule(node, testData);
    }

    public Trees combineTrees(List<String> treeIds) {
        List<Trees> trees = treesRepository.findAllById(treeIds);
        List<String> rules = trees.stream()
                .map(tree -> AstMain.astToString(Node.fromMap(tree.getJsonObject())))
                .collect(Collectors.toList());

        Node combinedNode = OptimizedRuleCombiner.combineMultipleRules(rules);
        Map<String, Object> nodeMap = combinedNode.toMap();
        String s = AstMain.astToString(combinedNode);
        String uniqueId = UUID.randomUUID().toString();
        Trees combinedTree = new Trees(uniqueId, nodeMap, s);
        return treesRepository.save(combinedTree);
    }

    public Trees modifyAST(String id, String newRuleString) {
        Trees existingTree = treesRepository.findById(id).orElse(null);
        if (existingTree == null) {
            return null;
        }

        Node newNode = AstMain.create_rule(newRuleString);
        Map<String, Object> newNodeMap = newNode.toMap();

        existingTree.setJsonObject(newNodeMap);
        existingTree.setRule(newRuleString);

        return treesRepository.save(existingTree);
    }

    public boolean deleteAST(String id) {
        if (treesRepository.existsById(id)) {
            treesRepository.deleteById(id);
            return true;
        }
        return false;
    }
}