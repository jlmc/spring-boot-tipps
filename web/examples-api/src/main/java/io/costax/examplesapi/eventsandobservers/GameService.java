package io.costax.examplesapi.eventsandobservers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class GameService {

    @Autowired ApplicationEventPublisher applicationEventPublisher;

    public void scoreGoal(String payerName) {
        // ...

        final String th = Thread.currentThread().getName();

        System.out.println(th + " ===> GameService : " + th);

        final PayerScoreGoalEvent payerScoreGoalEvent = new PayerScoreGoalEvent(payerName);

        applicationEventPublisher.publishEvent(payerScoreGoalEvent);
    }
}
