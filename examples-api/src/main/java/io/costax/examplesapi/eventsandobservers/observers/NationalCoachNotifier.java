package io.costax.examplesapi.eventsandobservers.observers;

import io.costax.examplesapi.eventsandobservers.PayerScoreGoalEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NationalCoachNotifier {

    @EventListener(condition = "#payerScoreGoalEvent.payerName != null && #payerScoreGoalEvent.payerName.equals('Messi')")
    public void onPayerScoreGoalEvent(PayerScoreGoalEvent payerScoreGoalEvent) {
        System.out.println("===> Notify the National Coach .... " + payerScoreGoalEvent.getPayerName());
    }

}
