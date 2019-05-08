package com.example.ugasoft.data.mongo.repo;


import com.example.ugasoft.data.mongo.entity.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfileRepository extends MongoRepository<Profile, String> {


}
