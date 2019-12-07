package io.costax.examplesapi.eventsandobservers.observers;

import io.costax.examplesapi.eventsandobservers.PayerScoreGoalEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TeamManagerNotifier {

    @EventListener(condition = "#event.getPayerName() != null && !#event.getPayerName().isBlank()")
    public void onPayerScoreGoalEvent(PayerScoreGoalEvent event) {
        System.out.println("===> Notify the Team Manager .... " + event.getPayerName());
    }

}
