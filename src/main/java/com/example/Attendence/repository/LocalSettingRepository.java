package com.example.Attendence.repository;

import com.example.Attendence.model.LocalSetting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalSettingRepository extends MongoRepository<LocalSetting, String> {

    // Select all entries based on the status using a MongoDB query
    List<LocalSetting> findAllByStatus(String status);
    // Find entry by ID
     Optional<LocalSetting> findById(String id);
    // Find entry by ID and status
    Optional<LocalSetting> findByIdAndStatus(String id, String status);


}
