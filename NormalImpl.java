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
    public double getTotalCommission(double cost) {
        return 0;
    }

    @Override
    public String generateInvoiceData(Map<Report, Integer> reports, double commission,
                                      int numQuarters, Scheduled scheduledType,
                                      double recurCost, int maxCountedEmployees, OrderType type) {
        double baseCommission = 0.0;
        double loadedCommission = commission;

        StringBuilder sb = new StringBuilder();

        if(scheduledType instanceof ScheduledImpl){
            sb.append("Thank you for your Crimson Permanent Assurance accounting order!\n");
        sb.append("The cost to provide these services: $");
        sb.append(String.format("%,.2f", recurCost));
        sb.append(" each quarter, with a total overall cost of: $");
        sb.append(String.format("%,.2f", scheduledType.getTotalCommission(commission)));
        sb.append("\nPlease see below for details:\n");

        List<Report> keyList = new ArrayList<>(reports.keySet());
        keyList.sort(Comparator.comparing(Report::getReportName).thenComparing(Report::getCommission));

        for (Report report : keyList) {
            sb.append("\tReport name: ");
            sb.append(report.getReportName());
            sb.append("\tEmployee Count: ");
            sb.append(reports.get(report));
            sb.append("\tCost per employee: ");
            sb.append(String.format("$%,.2f", report.getCommission()));
            if(type instanceof RegularOrder){
                if (reports.get(report) >maxCountedEmployees) {
                    sb.append("\tThis report cost has been capped.");
                }
            }
            sb.append("\tSubtotal: ");
            sb.append(String.format("$%,.2f\n", report.getCommission() * reports.get(report)));
        }

        return sb.toString();
        }

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
