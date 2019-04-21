package com.example.ugasoft.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class ScheduledRunnerTest {

    @Mock
    private DbInitWriter dbInitWriter;

    @InjectMocks
    private ScheduledRunner scheduledRunner;

    @Test
    public void init() {
        scheduledRunner.init();
        verify(dbInitWriter).write();
    }
}