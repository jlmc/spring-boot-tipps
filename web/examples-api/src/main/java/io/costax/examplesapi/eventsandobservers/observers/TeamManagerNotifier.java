package io.costax.examplesapi.eventsandobservers.observers;

import io.costax.examplesapi.eventsandobservers.PayerScoreGoalEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TeamManagerNotifier {

    @EventListener(condition = "#event.getPayerName() != null && !#event.getPayerName().isBlank()")
    public void onPayerScoreGoalEvent(PayerScoreGoalEvent event) {
        final String th = Thread.currentThread().getName();
        System.out.println(th + " ===> Notify the Team Manager .... " + event.getPayerName());
    }

}
