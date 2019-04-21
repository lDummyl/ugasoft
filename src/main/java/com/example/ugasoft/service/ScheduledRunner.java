package com.example.ugasoft.service;

import com.example.ugasoft.config.MainConfig;
import com.example.ugasoft.data.entity.Record;
import com.example.ugasoft.data.repo.RecordsRepository;
import com.example.ugasoft.exception.UgaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@EnableScheduling
public class ScheduledRunner {

    private final RabbitTemplate rabbitTemplate;
    private final RecordsRepository recordsRepository;
    private final DbInitWriter dbInitWriter;
    private CountDownLatch latch;

    @Value("${messages.to-send.qty}")
    private int messagesQty;
    @Value("${messages.to-send.timeout.receive-checkout}")
    private int receiveCheckoutTimeout;

    private TimeUnit timeUnit = TimeUnit.SECONDS;

    public ScheduledRunner(RabbitTemplate rabbitTemplate,
                           RecordsRepository recordsRepository,
                           DbInitWriter dbInitWriter) {
        this.rabbitTemplate = rabbitTemplate;
        this.recordsRepository = recordsRepository;
        this.dbInitWriter = dbInitWriter;
    }

    @PostConstruct
    void init(){
        dbInitWriter.write();
    }

    @Scheduled(fixedDelay = 5000, initialDelay = 500)
    public void run() throws InterruptedException {

        log.info("Sending {} messages...", messagesQty);
        latch = new CountDownLatch(messagesQty);
        Optional<Record> anyRecord = recordsRepository.findFirstByValueIsNotNull();
        Record record = anyRecord.orElseThrow(() -> new UgaException("No records in DB"));
        String message = record.getValue();

        for (int i = 0; i < messagesQty; i++) {
            rabbitTemplate.convertAndSend(MainConfig.topicExchangeName,
                    "foo.bar.baz", message);
        }

        boolean receivedInTime = latch.await(receiveCheckoutTimeout, timeUnit);

        if (receivedInTime) {
            log.info("Receivers got all {} messages", messagesQty);
        } else {
            throw new UgaException(String.format("%d messages of %d has been received in timeout %d %s",
                    messagesQty - latch.getCount(), messagesQty, receiveCheckoutTimeout, timeUnit));
        }
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}