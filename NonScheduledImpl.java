package au.edu.sydney.cpa.erp.feaa.ordering;

import au.edu.sydney.cpa.erp.ordering.Report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class NonScheduledImpl implements Scheduled {

    /**
     * Normal orders that are one off and not scheduled will not have any changes in the scheduled type.
     */

    private int numQuarters;

    public NonScheduledImpl(int numQuarters){
        this.numQuarters=numQuarters;
    }

    @Override
    public double getTotalCommission(double commission) {
        return commission;
    }

    @Override
    public String longDesc(Map<Report, Integer> reports, double commission, int id, LocalDateTime date, boolean finalised, Critical criticalType) {
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
    public String shortDesc(int id, double recurCost, double commission) {
        return String.format("ID:%s $%,.2f", id, commission);
    }

    @Override
    public String generateInvoiceData(double recurCost, int numQuarters, double commission) {
        return String.format("Your priority business account has been charged: $%,.2f" +
                "\nPlease see your internal accounting department for itemised details.", commission);

    }

    @Override
    public int getNumQuarters() {
        return 1;
    }
}
