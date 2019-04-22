package com.example.ugasoft.service;

import com.example.ugasoft.data.entity.Record;
import com.example.ugasoft.data.repo.RecordsRepository;
import com.example.ugasoft.exception.UgaException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ScheduledRunnerTest {

    private static final int RECEIVE_CHECKOUT_TIMEOUT = 1;
    private static final int MESSAGES_QTY = 3;
    @Mock
    private DbInitWriter dbInitWriter;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private RecordsRepository recordsRepository;

    @InjectMocks
    private ScheduledRunner scheduledRunner;
    private volatile boolean counterStop;

    @Test
    public void init() {
        scheduledRunner.init();
        verify(dbInitWriter).write();
    }

    @Test(expected = UgaException.class)
    public void runNotConfirmed() throws InterruptedException {
        when(recordsRepository.findFirstByValueIsNotNull()).thenReturn(Optional.of(mock(Record.class)));
        scheduledRunner.setMessagesQty(MESSAGES_QTY);
        scheduledRunner.run();
        verify(rabbitTemplate, times(MESSAGES_QTY)).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    public void runConfirmed() throws InterruptedException {
        when(recordsRepository.findFirstByValueIsNotNull()).thenReturn(Optional.of(new Record("message")));
        scheduledRunner.setMessagesQty(MESSAGES_QTY);
        scheduledRunner.setReceiveCheckoutTimeout(RECEIVE_CHECKOUT_TIMEOUT);
        Thread thread = new Thread(() -> {
            CountDownLatch latch = scheduledRunner.getLatch();
            while (!counterStop) {
                if (latch != null) {
                    latch.countDown();
                    if (latch.getCount() == 0) counterStop = true;
                } else {
                    latch = scheduledRunner.getLatch();
                }
            }
        });
        thread.start();
        scheduledRunner.run();
        thread.join();
        verify(rabbitTemplate, times(MESSAGES_QTY)).convertAndSend(anyString(), anyString(), any(Object.class));

    }
}