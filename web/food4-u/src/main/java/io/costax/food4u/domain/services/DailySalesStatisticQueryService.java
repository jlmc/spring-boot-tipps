package io.costax.food4u.domain.services;

import io.costax.food4u.domain.filters.DailySalesStatisticFilter;
import io.costax.food4u.domain.model.DailySalesStatistic;

import java.util.List;

public interface DailySalesStatisticQueryService {

    List<DailySalesStatistic> getStatistics(DailySalesStatisticFilter filter, final String timeOffset);

}
