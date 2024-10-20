package com.zeotap.backend.repo;

import com.zeotap.backend.models.Trees;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreesRepository extends MongoRepository<Trees, String> {
    // Custom query methods can be added here if needed
}
