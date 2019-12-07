package io.costax.examplesapi.eventsandobservers.observers;

import io.costax.examplesapi.eventsandobservers.PayerScoreGoalEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AllFutebolLoversNotifier {

    @Async
    @EventListener(condition = "#payerScoreGoalEvent.payerName != null && ( #payerScoreGoalEvent.payerName.equalsIgnoreCase('Messi') || #payerScoreGoalEvent.payerName.equalsIgnoreCase('CR7') )")
    public void onPayerScoreGoalEvent(PayerScoreGoalEvent payerScoreGoalEvent) {
        final String th = Thread.currentThread().getName();
        System.out.println(th + " ===> Notify the All Futebol Lovers Notifier .... " + payerScoreGoalEvent.getPayerName() + " .... Started ... ");

        notifyAllFutebolLovers();

        System.out.println(th + " ===> Notify the All Futebol Lovers Notifier .... " + payerScoreGoalEvent.getPayerName() + " .... Finished ... ");
    }

    private void notifyAllFutebolLovers() {
        try {
            Thread.sleep(5_000);
        } catch (InterruptedException ignored) {
        }
    }
}
