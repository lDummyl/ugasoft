package com.example.ugasoft.service;

import com.example.ugasoft.data.mongo.entity.Profile;
import com.example.ugasoft.data.mongo.repo.ProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Slf4j
@Service
public class MongoService {

    private ProfileRepository profileRepository;
    private ObjectMapper mapper;

    public MongoService(ProfileRepository profileRepository, ObjectMapper mapper) {
        this.profileRepository = profileRepository;
        this.mapper = mapper;
    }

    @SneakyThrows
    @PostConstruct
    public void init() {
        Profile petrofProf = new Profile("Petrov", "Andrey");
        profileRepository.insert(petrofProf);
        String json = "{\"id\":\"12\", \"firstName\": \"Alfred\", \"lastName\":\"Hitchcock\"}";
        Profile freddy = mapper.readValue(json, Profile.class);
        profileRepository.save(freddy);
        Optional<Profile> byId = profileRepository.findById("12");
        byId.ifPresent(i -> log.info(i.toString()));
        Optional<Profile> one = profileRepository.findOne(Example.of(petrofProf));
        Profile petroff = one.orElseThrow(() -> new NotFoundException("Cant Find Petroff ¯\\_(ツ)_/¯"));
        int timestamp = new ObjectId(petroff.getId()).getTimestamp();
        log.info(LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp),
                TimeZone.getDefault().toZoneId()).toString());
        List<Profile> freds = profileRepository.findAllByFirstNameLike("fred");
        System.out.println(freds);
    }
}
