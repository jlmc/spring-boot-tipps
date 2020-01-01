package io.costax.food4u.domain.services;

import io.costax.food4u.domain.filters.DailySalesStatisticFilter;

public interface DailySalesStatisticReportService {

    byte[] generateReport(DailySalesStatisticFilter filter, final String timeOffset);
}
