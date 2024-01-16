package io.github.jlmc.doc4poc.service.infrastructure.messaging;

import java.util.function.Consumer;

public class TechnicalSheetUpdateListener implements Consumer<TechnicalSheetUpdateDto> {
    @Override
    public void accept(TechnicalSheetUpdateDto technicalSheetUpdateDto) {
        System.out.println(technicalSheetUpdateDto);
    }
}
