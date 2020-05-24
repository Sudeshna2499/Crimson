package au.edu.sydney.cpa.erp.feaa.ordering;

import au.edu.sydney.cpa.erp.ordering.Report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CriticalImpl implements Critical {

    private double criticalLoading;

    public CriticalImpl(double criticalLoading){
        this.criticalLoading=criticalLoading;
    }

    @Override
    public double getCriticalLoading() {
        return criticalLoading;
    }

    @Override
    public String longDesc(Map<Report, Integer> reports, boolean finalised, int id, LocalDateTime date, double commission, Scheduled scheduledType) {
        double baseCommission = 0.0;
        double loadedCommission = commission;
        StringBuilder reportSB = new StringBuilder();

        List<Report> keyList = new ArrayList<>(reports.keySet());
        keyList.sort(Comparator.comparing(Report::getReportName).thenComparing(Report::getCommission));

        for (Report report : keyList) {
            double subtotal = report.getCommission() * reports.get(report);
            baseCommission += subtotal;

            reportSB.append(String.format("\tReport name: %s\tEmployee Count: %d\tCommission per employee: $%,.2f\tSubtotal: $%,.2f\n",
                    report.getReportName(),
                    reports.get(report),
                    report.getCommission(),
                    subtotal));
        }

        return String.format(finalised ? "" : "*NOT FINALISED*\n" +
                        "Order details (id #%d)\n" +
                        "Date: %s\n" +
                        "Reports:\n" +
                        "%s" +
                        "Critical Loading: $%,.2f\n" +
                        "Total cost: $%,.2f\n",
                id,
                date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                reportSB.toString(),
                loadedCommission - baseCommission,
                loadedCommission
        );
    }
    @Override
    public double getTotalCommission(double cost) {
        return cost * criticalLoading;
    }


    @Override
    public String generateInvoiceData(Map<Report, Integer> reports, double commission, int numQuarters,
                                      Scheduled scheduledType, double recurCost) {

       return scheduledType.generateInvoiceData(recurCost, numQuarters, commission);

    }
}
