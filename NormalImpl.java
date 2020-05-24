package au.edu.sydney.cpa.erp.feaa.ordering;

import au.edu.sydney.cpa.erp.ordering.Report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class NormalImpl implements Critical {

    private double criticalLoading;

    public NormalImpl(){
        this.criticalLoading=1;
    }

    @Override
    public double getCriticalLoading() {
        return criticalLoading;
    }

    @Override
    public String longDesc(Map<Report, Integer> reports, boolean finalised, int id,
                           LocalDateTime date, double commission, Scheduled scheduledType) {

        if(scheduledType instanceof ScheduledImpl){
            double totalLoadedCost = scheduledType.getTotalCommission(commission);
            StringBuilder reportSB = new StringBuilder();

        List<Report> keyList = new ArrayList<>(reports.keySet());
        keyList.sort(Comparator.comparing(Report::getReportName).thenComparing(Report::getCommission));

        for (Report report : keyList) {
            double subtotal = report.getCommission() * reports.get(report);

            reportSB.append(String.format("\tReport name: %s\tEmployee Count: %d\tCommission per employee: $%,.2f\tSubtotal: $%,.2f\n",
                    report.getReportName(),
                    reports.get(report),
                    report.getCommission(),
                    subtotal));
        }

        return String.format(finalised? "" : "*NOT FINALISED*\n" +
                        "Order details (id #%d)\n" +
                        "Date: %s\n" +
                        "Number of quarters: %d\n" +
                        "Reports:\n" +
                        "%s" +
                        "Recurring cost: $%,.2f\n" +
                        "Total cost: $%,.2f\n",
                id,
                date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                scheduledType.getNumQuarters(),
                reportSB.toString(),
                commission,
                totalLoadedCost
        );
        }
        StringBuilder reportSB = new StringBuilder();

        List<Report> keyList = new ArrayList<>(reports.keySet());
        keyList.sort(Comparator.comparing(Report::getReportName).thenComparing(Report::getCommission));

        for (Report report : keyList) {
            double subtotal = report.getCommission() * reports.get(report);

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
                        "Total cost: $%,.2f\n",
                id,
                date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                reportSB.toString(),
               commission
        );


    }

    @Override
    public double getTotalCommission(double cost) {
        return 0;
    }

    @Override
    public String generateInvoiceData(Map<Report, Integer> reports, double commission, int numQuarters, Scheduled scheduledType, double recurCost) {
        double baseCommission = 0.0;
        double loadedCommission = commission;

        StringBuilder sb = new StringBuilder();

        sb.append("Thank you for your Crimson Permanent Assurance accounting order!\n");
        sb.append("The cost to provide these services: $");
        sb.append(String.format("%,.2f", commission));
        sb.append("\nPlease see below for details:\n");
        List<Report> keyList = new ArrayList<>(reports.keySet());
        keyList.sort(Comparator.comparing(Report::getReportName).thenComparing(Report::getCommission));

        for (Report report : keyList) {
            double subtotal = report.getCommission() * reports.get(report);
            baseCommission += subtotal;

            sb.append("\tReport name: ");
            sb.append(report.getReportName());
            sb.append("\tEmployee Count: ");
            sb.append(reports.get(report));
            sb.append("\tCost per employee: ");
            sb.append(String.format("$%,.2f", report.getCommission()));
            sb.append("\tSubtotal: ");
            sb.append(String.format("$%,.2f\n", subtotal));
        }
        return sb.toString();
    }
}
