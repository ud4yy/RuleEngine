package com.zeotap.backend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("Tree")
public class Trees {
    @Id
    private String id;

    private Map<String, Object> jsonObject;
    
    private String rule; // The rule string associated with this tree
}