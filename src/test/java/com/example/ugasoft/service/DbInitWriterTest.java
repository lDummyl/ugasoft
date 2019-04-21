package com.example.ugasoft.service;

import com.example.ugasoft.data.repo.RecordsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DbInitWriterTest {

    @Mock
    private RecordsRepository recordsRepository;

    @InjectMocks
    DbInitWriter dbInitWriter;

    @Test
    public void write() {
        dbInitWriter.write();
        verify(recordsRepository).saveAll(anyCollection());
    }
}