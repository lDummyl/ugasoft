package com.example.ugasoft.service;

import com.example.ugasoft.data.entity.Record;
import com.example.ugasoft.data.repo.RecordsRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class DbInitWriter {

    private RecordsRepository recordsRepository;

    public DbInitWriter(RecordsRepository recordsRepository) {
        this.recordsRepository = recordsRepository;
    }

    @PostConstruct
    void init(){
        Record record = new Record();
        record.setValue("Privet!");
        recordsRepository.save(record);
    }

}
