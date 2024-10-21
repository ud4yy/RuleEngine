package com.example.backend.controllers;

import com.example.backend.models.Trees;
import com.example.backend.service.TreesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trees")
@CrossOrigin(origins = {"http://localhost:5173","https://rule-engine-delta.vercel.app"})
public class TreesController {

    private static final Logger logger = LoggerFactory.getLogger(TreesController.class);

    @Autowired
    private TreesService treesService;

    @GetMapping("/l")
    public String getHelloMessage() {
        return "Hello";
    }
    
    @PostMapping("/create")
    public ResponseEntity<String> createAST(@RequestBody String ruleString) {
        if (ruleString == null || ruleString.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Rule string cannot be null or empty");
        }
        try {
            Trees createdTree = treesService.createAST(ruleString);
            return ResponseEntity.ok("Tree created successfully with ID: " + createdTree.getId());
        } catch (Exception e) {
            logger.error("Error creating AST: {}", e.getMessage() + "Invalid Rule Or Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error creating tree: " + e.getMessage());
        }
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

    @PutMapping("/modify/{id}")
    public ResponseEntity<Trees> modifyAST(@PathVariable String id, @RequestBody String newRuleString) {
        if (newRuleString == null || newRuleString.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Trees modifiedTree = treesService.modifyAST(id, newRuleString);
        if (modifiedTree == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(modifiedTree);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAST(@PathVariable String id) {
        boolean deleted = treesService.deleteAST(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}