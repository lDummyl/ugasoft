package com.example.ugasoft.data.repo;

import com.example.ugasoft.data.entity.Record;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecordsRepository extends CrudRepository<Record, Long> {

    Optional<Record> findFirstByValueIsNotNull();

}
