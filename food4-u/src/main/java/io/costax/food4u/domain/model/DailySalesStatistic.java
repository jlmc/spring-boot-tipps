package io.costax.food4u.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class DailySalesStatistic implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate date;
    private Long totalSales;
    private BigDecimal totalInvoice;

    public DailySalesStatistic(final LocalDate date, final Long totalSales, final BigDecimal totalInvoice) {
        this.date = date;
        this.totalSales = totalSales;
        this.totalInvoice = totalInvoice;
    }
}
