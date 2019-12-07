package io.costax.examplesapi.eventsandobservers;

public class PayerScoreGoalEvent {

    private String payerName;

    public PayerScoreGoalEvent() {
    }

    public PayerScoreGoalEvent(final String payerName) {
        this.payerName = payerName;
    }

    public String getPayerName() {
        return payerName;
    }
}
