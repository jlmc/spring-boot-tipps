package io.costax.food4u.domain.services.internal.reports;

import io.costax.food4u.domain.filters.DailySalesStatisticFilter;
import io.costax.food4u.domain.services.DailySalesStatisticQueryService;
import io.costax.food4u.domain.services.DailySalesStatisticReportService;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;

@Service
public class DailySalesStatisticReportServiceImpl implements DailySalesStatisticReportService {

    @Autowired
    DailySalesStatisticQueryService queryService;


    @Override
    public byte[] generateReport(final DailySalesStatisticFilter filter, final String timeOffset) {
        try {
            var inputStream = this.getClass().getResourceAsStream(
                    "/reports/vendas-diarias.jasper");

            var parameters = new HashMap<String, Object>();
            parameters.put("REPORT_LOCALE", new Locale("pt", "PT"));

            var statistics = queryService.getStatistics(filter, timeOffset);
            var dataSource = new JRBeanCollectionDataSource(statistics);

            var jasperPrint = JasperFillManager.fillReport(inputStream, parameters, dataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new ReportException("Unable to issue daily sales report", e);
        }
    }
}
