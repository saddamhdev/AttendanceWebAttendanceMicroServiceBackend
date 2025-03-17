package com.example.Attendence.repository;

import com.example.Attendence.model.GlobalSetting;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GlobalSettingRepository extends MongoRepository<GlobalSetting, String> {

    // Select all entries based on the status using a MongoDB query
    List<GlobalSetting> findAllByStatus(String status);
    // Find entry by ID
     Optional<GlobalSetting> findById(String id);
    // Find entry by ID and status
    Optional<GlobalSetting> findByIdAndStatus(String id, String status);


}
