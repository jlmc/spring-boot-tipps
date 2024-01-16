package io.github.jlmc.doc4poc.service.infrastructure.messaging;

import java.util.function.Consumer;

public class ReportChangesListener implements Consumer<OrderChanged> {
    @Override
    public void accept(OrderChanged dto) {
        System.out.println("==>>> " + dto);
    }
}
