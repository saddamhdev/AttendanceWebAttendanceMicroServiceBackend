package com.example.Attendence.repository;

import com.example.Attendence.model.LocalSetting;
import com.example.Attendence.model.Position;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends MongoRepository<Position, String> {
    // You can add custom query methods here if needed
    List<Position> findAllByStatus(String status);
}
