package com.zeotap.backend.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        Node n = AstMain.create_rule(s);
        Map<String, Object> nodeMap = n.toMap();
        AstMain.printAST(n);

        String uniqueId = UUID.randomUUID().toString();
        Trees t = new Trees(uniqueId, nodeMap); 
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
    
            String uniqueId = UUID.randomUUID().toString();
            Trees combinedTree = new Trees(uniqueId, nodeMap);
            return treesRepository.save(combinedTree);
    }

}
