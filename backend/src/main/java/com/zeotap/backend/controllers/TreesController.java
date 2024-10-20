package com.zeotap.backend.controllers;

import com.google.gson.Gson;
import com.zeotap.backend.AST.AstMain;
import com.zeotap.backend.AST.Node;
import com.zeotap.backend.models.Trees;
import com.zeotap.backend.service.TreesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/trees")
public class TreesController {

    private static final Logger logger = LoggerFactory.getLogger(TreesController.class);

    @Autowired
    private TreesService treesService;

    @GetMapping("/l")
    public String getHelloMessage() {
        return "Hello";
    }
    
    @PostMapping("/create")
    public ResponseEntity<Trees> createAST(@RequestBody String ruleString) {
        if (ruleString == null || ruleString.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Trees createdTree = treesService.createAST(ruleString);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTree);
    }

    @PostMapping("/evaluate/{id}")
    public ResponseEntity<Boolean> evaluateTree(@PathVariable String id, @RequestBody Map<String, Object> testData) {
        Trees tree = treesService.getTreeById(id);
        if (tree == null) {
            return ResponseEntity.notFound().build();
        }
        boolean result = treesService.evaluateTree(tree, testData);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Trees> getTreeById(@PathVariable String id) {
        Trees t = treesService.getTreeById(id);
        if (t == null) {
            return ResponseEntity.notFound().build();
        } 
        // Map<String, Object> s = t.getJsonObject();
        // Node deserializedNode = Node.fromMap(s);
        // logger.info("Deserializing and printing AST for tree with id: {}", id);
        // AstMain.printAST(deserializedNode);
        return ResponseEntity.ok(t);
    }
    @PostMapping("/combine")
    public ResponseEntity<Trees> combineTrees(@RequestBody List<String> treeIds) {
        if (treeIds == null || treeIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Trees combinedTree = treesService.combineTrees(treeIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(combinedTree);
    }
    @GetMapping("/getall")
    public List<Trees> getAllTrees() {
        return treesService.getAllTrees();
    }
}
