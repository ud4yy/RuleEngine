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
@Document("Tree") // Defines the MongoDB collection name
public class Trees {
    @Id
    private String id; // The unique identifier for the document

    private Map<String, Object> jsonObject; // A Map to represent the JSON object
}
