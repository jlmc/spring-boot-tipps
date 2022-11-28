package io.costax.food4u.api.openapi.controllers;

import io.costax.food4u.domain.filters.DailySalesStatisticFilter;
import io.costax.food4u.domain.model.DailySalesStatistic;
import io.swagger.annotations.*;

import java.util.List;

@Api(tags = "Statistics")
public interface StatisticsResourcesOpenApi {

    @ApiOperation("Get daily Sales Statistics")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "restaurantActive", value = "ID do restaurante",
                    example = "true", dataType = "boolean"),
            @ApiImplicitParam(name = "createdAt", value = "Date/hour of creation of the requests",
                    example = "2019-12-02T23:59:59Z", dataType = "date-time")
    })
    List<DailySalesStatistic> dailySalesStatisticsJson(
            DailySalesStatisticFilter filter,
            @ApiParam(value = "Time shift to be considered in the query in relation to UTC",
                    defaultValue = "+00:00") String timeOffset);
}
