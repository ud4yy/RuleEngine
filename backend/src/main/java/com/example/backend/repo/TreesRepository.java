package com.example.backend.repo;

import com.example.backend.models.Trees;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreesRepository extends MongoRepository<Trees, String> {
    // Custom query methods can be added here if needed
}
