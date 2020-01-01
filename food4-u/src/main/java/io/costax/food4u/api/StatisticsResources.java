package io.costax.food4u.api;

import io.costax.food4u.domain.filters.DailySalesStatisticFilter;
import io.costax.food4u.domain.model.DailySalesStatistic;
import io.costax.food4u.domain.services.DailySalesStatisticQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statistics")
public class StatisticsResources {

    @Autowired
    DailySalesStatisticQueryService dailySalesStatisticQuery;

    @GetMapping("/daily-sales")
    @ResponseStatus(HttpStatus.OK)
    public List<DailySalesStatistic> dailySalesStatistics(DailySalesStatisticFilter filter,
                                                          @RequestParam(defaultValue = "+01:00") String timeOffset) {
        return dailySalesStatisticQuery.getStatistics(filter, timeOffset);
    }
}
