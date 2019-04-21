package com.example.ugasoft.service;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class Receiver {

    private ScheduledRunner scheduledRunner;

    public Receiver(ScheduledRunner scheduledRunner) {
        this.scheduledRunner = scheduledRunner;
    }

    /*The the server could be stopped before it receives all messages
     from the rabbit, so until the sender’s start after init delay, we mark all messages
     as coming from another session, however, this does not guarantee
     that the delay will be sufficient for the messages to not begin
     to mix(new send and from previous sessions). The guaranteed solution
     require more algorithm complexity, such as message signatures and so on,
     currently it all depends on sender’s init delay.*/

    public void listenForRabbit(String message) {
        CountDownLatch latch = scheduledRunner.getLatch();
        if (latch != null) {
            log.info("Received <{}>", message);
            scheduledRunner.getLatch().countDown();
        } else {
            log.info("Receiving not current session message <{}>", message);
        }
    }
}