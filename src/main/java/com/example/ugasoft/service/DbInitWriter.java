package com.example.ugasoft.service;

import com.example.ugasoft.data.entity.Record;
import com.example.ugasoft.data.repo.RecordsRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DbInitWriter {

    @Value("${messages.generated.qty}")
    private int initMessageQty;

    @Value("${messages.generated.length}")
    private int wordSize;

    private RecordsRepository recordsRepository;

    public DbInitWriter(RecordsRepository recordsRepository) {
        this.recordsRepository = recordsRepository;
    }

    void write() {
        List<Record> generatedRecords = Stream.generate(() -> new Record(RandomStringUtils.randomAscii(wordSize)))
                .limit(initMessageQty).collect(Collectors.toList());
        recordsRepository.saveAll(generatedRecords);
    }
}
