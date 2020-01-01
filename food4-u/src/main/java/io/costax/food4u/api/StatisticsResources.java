package io.costax.food4u.api;

import io.costax.food4u.domain.filters.DailySalesStatisticFilter;
import io.costax.food4u.domain.model.DailySalesStatistic;
import io.costax.food4u.domain.services.DailySalesStatisticQueryService;
import io.costax.food4u.domain.services.DailySalesStatisticReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statistics")
public class StatisticsResources {

    @Autowired
    DailySalesStatisticQueryService dailySalesStatisticQuery;

    @Autowired
    DailySalesStatisticReportService dailySalesStatisticReportService;

    @GetMapping(value = "/daily-sales", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<DailySalesStatistic> dailySalesStatisticsJson(DailySalesStatisticFilter filter,
                                                          @RequestParam(defaultValue = "+01:00") String timeOffset) {
        return dailySalesStatisticQuery.getStatistics(filter, timeOffset);
    }

    @GetMapping(value = "/daily-sales", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> dailySalesStatisticsPdf(DailySalesStatisticFilter filter,
                                                              @RequestParam(defaultValue = "+01:00") String timeOffset) {
        final byte[] pdf = dailySalesStatisticReportService.generateReport(filter, timeOffset);

        final HttpHeaders httpHeaders = new HttpHeaders();

        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=daily-sales-statistic.pdf");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .headers(headers)
                .body(pdf);
    }
}
